package ru.sfedu.testingTechcnologies.api;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import ru.sfedu.testingTechcnologies.Messages;
import ru.sfedu.testingTechcnologies.model.RequestStatus;
import ru.sfedu.testingTechcnologies.model.User;
import ru.sfedu.testingTechcnologies.utils.MongoClientUtil;
import java.util.List;

@Log4j2
public class MongoApi implements IApi {
  private static MongoClientUtil mongoClientUtil = null;
  private static MongoApi INSTANCE = null;

  public static MongoApi getInstance() {
    if (INSTANCE == null) {
      mongoClientUtil = MongoClientUtil.getInstance();
      INSTANCE = new MongoApi();
    }
    return INSTANCE;
  }

  public RequestStatus dropUsers() {
    return mongoClientUtil.dropTable(User.class);
  }

  @Override
  public RequestStatus createUser(@NonNull User user) {
    String id = new ObjectId().toString();
    user.setId(id);

    RequestStatus status = mongoClientUtil.write(user);
    if (status == RequestStatus.SUCCESS) {
      log.info(String.format(Messages.USER_CREATE_SUCCESS, user));
    }
    return status;
  }

  @Override
  public RequestStatus addUser(@NonNull User user) {
    RequestStatus status = mongoClientUtil.write(user);
    if (status == RequestStatus.SUCCESS) {
      log.info(String.format(Messages.USER_ADDED_SUCCESS, user));
    }
    return status;
  }

  @Override
  public RequestStatus addUsers(@NonNull List<User> users) {
    RequestStatus status = mongoClientUtil.write(users);
    if (status == RequestStatus.SUCCESS) {
      log.info(String.format(Messages.USERS_ADDED_SUCCESS));
    }
    return status;
  }

  @Override
  public List<User> getUsers() {
    return mongoClientUtil.readAll(User.class);
  }

  @Override
  public User getUser(@NonNull String id) {
    return mongoClientUtil.getById(User.class, id);
  }

  @Override
  public RequestStatus editUser(@NonNull User user) {
    RequestStatus status = mongoClientUtil.update(user);
    if (status == RequestStatus.SUCCESS) {
      log.info(String.format(Messages.USER_UPDATE_SUCCESS, user));
    }
    return status;
  }

  @Override
  public RequestStatus deleteUser(@NonNull String id) {
    RequestStatus status = mongoClientUtil.delete(User.class, id);
    if (status == RequestStatus.SUCCESS) {
      log.info(String.format(Messages.USER_DELETE_SUCCESS, id));
    }
    return status;
  }
}
