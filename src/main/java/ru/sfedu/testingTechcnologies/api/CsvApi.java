package ru.sfedu.testingTechcnologies.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import ru.sfedu.testingTechcnologies.Constants;
import ru.sfedu.testingTechcnologies.Messages;
import ru.sfedu.testingTechcnologies.model.RequestStatus;
import ru.sfedu.testingTechcnologies.model.User;
import ru.sfedu.testingTechcnologies.utils.PropertyProvider;

import java.io.*;
import java.util.*;

@Log4j2
public class CsvApi implements IApi {
  private static IApi INSTANCE = null;

  public static IApi getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CsvApi();
    }
    return INSTANCE;
  }

  private <T> void write(T object) throws IOException {
    write(object.getClass(), Collections.singletonList(object), false);
  }

  private <T> void write(Class<?> tClass, List<T> objectList, boolean overwrite) throws IOException {
    List<T> tList;

    if (!overwrite) {
      tList = (List<T>) read(tClass);
      tList.addAll(objectList);
    } else {
      tList = objectList;
    }

    if (tList.isEmpty()) {
      deleteFile(tClass);
    }

    CSVWriter csvWriter = getCsvWriter(tClass);
    StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)
            .withApplyQuotesToAll(false)
            .build();

    try {
      beanToCsv.write(tList);
    } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
      log.error(e);
    }

    csvWriter.close();
  }

  private <T> CSVWriter getCsvWriter(Class<T> tClass) throws IOException {
    FileWriter writer;
    File path = new File(PropertyProvider.getProperty(Constants.CSV_PATH));
    File file = getFile(tClass);

    if (!file.exists() && path.mkdirs() && !file.createNewFile()) {
      throw new IOException(String.format(Messages.CREATE_FILE_SUCCESS, file.getName()));
    }

    writer = new FileWriter(file);
    return new CSVWriter(writer);
  }

  private <T> CSVReader getCsvReader(Class<T> tClass) throws IOException {
    File file = getFile(tClass);

    if (!file.exists() && !file.createNewFile()) {
      throw new IOException(String.format(Messages.CREATE_FILE_ERROR, file.getName()));
    }

    FileReader fileReader = new FileReader(file);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    return new CSVReader(bufferedReader);
  }

  private <T> List<T> read(Class<T> tClass) throws IOException {
    List<T> tList;

    try {
      CSVReader csvReader = getCsvReader(tClass);
      CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
              .withType(tClass)
              .withIgnoreLeadingWhiteSpace(true)
              .build();
      tList = csvToBean.parse();
      csvReader.close();
    } catch (IOException e) {
      log.error(e);
      throw e;
    }
    return tList;
  }

  private <T> File getFile(Class<T> tClass) throws IOException {
    return new File(PropertyProvider.getProperty(Constants.CSV_PATH)
            + tClass.getSimpleName().toLowerCase()
            + PropertyProvider.getProperty(Constants.CSV_EXTENSION));
  }

  private <T> void deleteFile(Class<T> tClass) {
    try {
      ;
      log.info(String.format(Messages.DELETE_FILE, getFile(tClass).getName(), getFile(tClass).delete()));
    } catch (IOException e) {
      log.error(e);
    }
  }


  @Override
  public RequestStatus createUser(@NonNull User user) {
    try {
      String id = new ObjectId().toString();
      user.setId(id);
      write(user);
      log.info(String.format(Messages.USER_CREATE_SUCCESS, user));
    } catch (IOException e) {
      log.error(e);
      return RequestStatus.FAILED;
    }
    return RequestStatus.SUCCESS;
  }

  @Override
  public List<User> getUsers() {
    try {
      return read(User.class);
    } catch (IOException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  @Override
  public User getUser(@NonNull String id) {
    List<User> users = getUsers();
    Optional<User> user = users
            .stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();

    if (user.isPresent()) {
      return user.get();
    }

    log.error(String.format(Messages.USER_NOT_FOUND, id));
    return null;
  }

  @Override
  public RequestStatus editUser(@NonNull User user) {
    List<User> usersList = getUsers();
    User editedUser = getUser(user.getId());
    if (editedUser != null) {
      List<User> newUsersList = usersList
              .stream()
              .map(u -> {
                if (u.getId().equals(user.getId())) {
                  return user;
                }
                return u;
              })
              .toList();

      RequestStatus status = updateUsersList(newUsersList);
      if (status == RequestStatus.SUCCESS) {
        log.info(String.format(Messages.USER_UPDATE_SUCCESS, user));
      } else {
        log.error(String.format(Messages.USER_UPDATE_ERROR, user));
      }
      return status;
    }

    log.error(String.format(Messages.USER_UPDATE_ERROR, user));
    return RequestStatus.FAILED;
  }

  @Override
  public RequestStatus deleteUser(@NonNull String id) {
    List<User> usersList = getUsers();
    User editedUserOptionals = getUser(id);
    if (editedUserOptionals != null) {
      List<User> newUsersList = usersList
              .stream()
              .filter(u -> !u.getId().equals(id))
              .toList();

      RequestStatus status = updateUsersList(newUsersList);

      if (status == RequestStatus.SUCCESS) {
        log.info(String.format(Messages.USER_DELETE_SUCCESS, id));
      } else {
        log.info(String.format(Messages.USER_DELETE_ERROR, id));
      }
      return status;
    }

    log.error(String.format(Messages.USER_DELETE_ERROR, id));
    return RequestStatus.FAILED;
  }

  private RequestStatus updateUsersList(List<User> users) {
    try {
      write(User.class, users, true);
      return RequestStatus.SUCCESS;
    } catch (IOException e) {
      log.error(e);
      return RequestStatus.FAILED;
    }
  }

}
