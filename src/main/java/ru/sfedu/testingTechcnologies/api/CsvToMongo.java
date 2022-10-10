package ru.sfedu.testingTechcnologies.api;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.testingTechcnologies.model.RequestStatus;
import ru.sfedu.testingTechcnologies.model.User;

import java.util.List;

@Log4j2
public class CsvToMongo {
  private static CsvToMongo INSTANCE = null;
  private static CsvApi CSV_API = null;
  private static MongoApi MONGO_API = null;

  public static CsvToMongo getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new CsvToMongo();
      CSV_API = CsvApi.getInstance();
      MONGO_API = MongoApi.getInstance();
    }
    return INSTANCE;
  }

  public RequestStatus moveUsersFromCsvToMongo(String csvPath) {
    try {
      List<User> users = CSV_API.getUsersFromFile(csvPath);
      return MONGO_API.addUsers(users);
    } catch (Exception e) {
      log.error(e);
    }
    return RequestStatus.FAILED;
  }

}
