package ru.sfedu.testingTechcnologies.api;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import ru.sfedu.testingTechcnologies.Messages;
import ru.sfedu.testingTechcnologies.model.RequestStatus;
import ru.sfedu.testingTechcnologies.model.User;
import ru.sfedu.testingTechcnologies.utils.CsvUtil;

import java.io.*;
import java.util.*;

@Log4j2
public class CsvApi extends CsvUtil implements IApi {
  private static CsvApi INSTANCE = null;

  private RequestStatus updateUsersList(List<User> users) {
    try {
      write(User.class, users, true);
      return RequestStatus.SUCCESS;
    } catch (IOException e) {
      log.error(e);
      return RequestStatus.FAILED;
    }
  }

  public static CsvApi getInstance() {
    if (INSTANCE == null) {
      CsvUtil.getInstance();
      INSTANCE = new CsvApi();
    }
    return INSTANCE;
  }

  public List<User> getUsersFromFile(String path) {
    try {
      return read(User.class, path);
    } catch (IOException e) {
      log.error(e);
    }
    return new ArrayList<>();
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
  public RequestStatus addUser(@NonNull User user) {
    try {
      write(user);
      log.info(String.format(Messages.USER_ADDED_SUCCESS, user));
    } catch (IOException e) {
      log.error(e);
      return RequestStatus.FAILED;
    }
    return RequestStatus.SUCCESS;
  }

  @Override
  public RequestStatus addUsers(@NonNull List<User> users) {
    try {
      write(users);
      log.info(String.format(Messages.USERS_ADDED_SUCCESS));
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

}
