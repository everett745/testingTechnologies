package ru.sfedu.testingTechcnologies.api;

import ru.sfedu.testingTechcnologies.model.RequestStatus;
import ru.sfedu.testingTechcnologies.model.User;

import java.util.List;

import lombok.NonNull;

public interface IApi {

  RequestStatus createUser(@NonNull User name);

  List<User> getUsers();

  User getUser(@NonNull String id);

  RequestStatus editUser(@NonNull User user);

  RequestStatus deleteUser(@NonNull String id);

}
