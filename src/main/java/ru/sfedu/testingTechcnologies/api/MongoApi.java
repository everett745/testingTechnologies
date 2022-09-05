package ru.sfedu.testingTechcnologies.api;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.testingTechcnologies.utils.MongoClientUtil;

@Log4j2
public class MongoApi {
  private static MongoClientUtil mongoClientUtil = null;
  private static MongoApi INSTANCE = null;

  public static MongoApi getInstance() {
    if (INSTANCE == null) {
      mongoClientUtil = MongoClientUtil.getInstance();
      INSTANCE = new MongoApi();
    }
    return INSTANCE;
  }


}
