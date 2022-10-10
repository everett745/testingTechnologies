package ru.sfedu.testingTechcnologies.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.log4j.Log4j2;
import ru.sfedu.testingTechcnologies.Constants;
import ru.sfedu.testingTechcnologies.Messages;

import java.io.*;
import java.util.Collections;
import java.util.List;

@Log4j2
public class CsvUtil {
  protected static CsvUtil INSTANCE = null;

  public static CsvUtil getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CsvUtil();
    }
    return INSTANCE;
  }

  protected <T> void write(T object) throws IOException {
    write(object.getClass(), Collections.singletonList(object), false);
  }

  protected <T> void write(Class<?> tClass, List<T> objectList, boolean overwrite) throws IOException {
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

  protected <T> CSVWriter getCsvWriter(Class<T> tClass) throws IOException {
    FileWriter writer;
    File path = new File(PropertyProvider.getProperty(Constants.CSV_PATH));
    File file = getFile(tClass);

    if (!file.exists() && path.mkdirs() && !file.createNewFile()) {
      throw new IOException(String.format(Messages.CREATE_FILE_SUCCESS, file.getName()));
    }

    writer = new FileWriter(file);
    return new CSVWriter(writer);
  }

  protected CSVReader getCsvReader(File file) throws IOException {
    if (!file.exists() && !file.createNewFile()) {
      throw new IOException(String.format(Messages.CREATE_FILE_ERROR, file.getName()));
    }

    FileReader fileReader = new FileReader(file);
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    return new CSVReader(bufferedReader);
  }

  protected <T> CSVReader getCsvReader(Class<T> tClass) throws IOException {
    File file = getFile(tClass);
    return getCsvReader(file);
  }

  protected <T> List<T> read(Class<T> tClass, CSVReader csvReader) throws IOException {
    List<T> tList;

    try {
      CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(csvReader)
              .withType(tClass)
              .withIgnoreLeadingWhiteSpace(true)
              .build();
      tList = csvToBean.parse();
      csvReader.close();

      if (tList.isEmpty()) {
        log.info("File is empty");
      } else {
        log.info("File read success");
      }
    } catch (IOException e) {
      log.error(e);
      throw e;
    }
    return tList;
  }

  protected <T> List<T> read(Class<T> tClass) throws IOException {
    try {
      CSVReader csvReader = getCsvReader(tClass);
      return read(tClass, csvReader);
    } catch (IOException e) {
      log.error(e);
      throw e;
    }
  }

  protected <T> List<T> read(Class<T> tClass, String path) throws IOException {
    try {
      File file = getFile(path);
      CSVReader csvReader = getCsvReader(file);
      return read(tClass, csvReader);
    } catch (IOException e) {
      log.error(e);
      throw e;
    }
  }

  protected <T> File getFile(Class<T> tClass) throws IOException {
    String path = PropertyProvider.getProperty(Constants.CSV_PATH)
            + tClass.getSimpleName().toLowerCase()
            + PropertyProvider.getProperty(Constants.CSV_EXTENSION);

    log.info(String.format("Get file by path: %s", path));
    File file = new File(path);

    return validateFile(file);
  }

  protected File getFile(String path) throws IOException {
    log.info(String.format("Get file by path: %s", path));
    return validateFile(new File(path));
  }

  protected <T> void deleteFile(Class<T> tClass) {
    try {
      log.info(String.format(Messages.DELETE_FILE, getFile(tClass).getName(), getFile(tClass).delete()));
    } catch (IOException e) {
      log.error(e);
    }
  }

  private File validateFile(File file) throws IOException {
    if (!file.exists()) {
      throw new IOException(String.format("Incorrect file name - %s", file.getName()));
    }
    return file;
  }

}
