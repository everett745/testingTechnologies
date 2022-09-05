package ru.sfedu.testingTechcnologies.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import ru.sfedu.testingTechcnologies.Constants;
import ru.sfedu.testingTechcnologies.model.Entity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class MongoClientUtil {
  private static final String DEFAULT_MONGO_HOST = "localhost";
  private static final int DEFAULT_MONGO_PORT = 27017;
  private static final String DEFAULT_MONGO_DB_NAME = "TestingTechnologies";
  private static final String DEFAULT_MONGO_DRIVER = "org.mongodb.driver";

  private static MongoClientUtil INSTANCE = null;
  private static Logger mongoLogger = null;
  private static MongoClient mongoClient = null;
  private static MongoDatabase database = null;

  public MongoClient getMongoClient() {
    return mongoClient;
  }

  public MongoDatabase getDatabase() {
    return database;
  }

  private static MongoClient initMongoClient() {
    try {
      return new MongoClient(
              PropertyProvider.getProperty(Constants.MONGO_HOST),
              Integer.parseInt(PropertyProvider.getProperty(Constants.MONGO_PORT))
      );
    } catch (IOException e) {
      log.error(e);
      return new MongoClient(DEFAULT_MONGO_HOST, DEFAULT_MONGO_PORT);
    }
  }

  private static MongoDatabase initMongoDatabase() {
    try {
      return mongoClient.getDatabase(PropertyProvider.getProperty(Constants.MONGO_DB_NAME));
    } catch (IOException e) {
      log.error(e);
      return mongoClient.getDatabase(DEFAULT_MONGO_DB_NAME);
    }
  }

  private static Logger initMongoLogger() {
    try {
      return Logger.getLogger(PropertyProvider.getProperty(Constants.MONGO_DRIVER));
    } catch (IOException e) {
      log.error(e);
      return Logger.getLogger(DEFAULT_MONGO_DRIVER);
    }
  }

  public static MongoClientUtil getInstance() {
    if (INSTANCE == null) {
      mongoLogger = initMongoLogger();
      mongoLogger.setLevel(Level.SEVERE);

      mongoClient = initMongoClient();
      database = initMongoDatabase();

      INSTANCE = new MongoClientUtil();
    }
    return INSTANCE;
  }

  private <T extends Entity> Document getObjectDocument(T object) {
    return Document.parse(object.toJSON());
  }

  public <T extends Entity> void write(T object) {
    try {
      database.getCollection(object.getClass().getSimpleName()).insertOne(getObjectDocument(object));
    } catch (MongoException e) {
      log.error(e);
    }
  }

  public <T extends Entity> void write(T object, List<T> objectList) {
    try {
      database.getCollection(object.getClass().getSimpleName())
              .insertMany(objectList.stream().map(this::getObjectDocument).toList());
    } catch (MongoException e) {
      log.error(e);
    }
  }

  public List<Document> readAll(Class<?> tClass) {
    try {
      List<Document> resList = new ArrayList<>();
      FindIterable<Document> iterDoc = database.getCollection(tClass.getSimpleName()).find();
      for (Document document : iterDoc) {
        resList.add(document);

        String a = document.toJson(JsonWriterSettings
                .builder()
                .outputMode(JsonMode.RELAXED)
                .build());
        System.out.println(a);
      }
      return resList;
    } catch (MongoException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  public <T extends Entity> void update(T object) {
    try {
      database.getCollection(object.getClass().getSimpleName())
              .updateOne(
                      new Document("_id", object.getId()),
                      new Document("$set", getObjectDocument(object))
              );
    } catch (MongoException e) {
      log.error(e);
    }
  }

  public <T extends Entity> void delete(T object) {
    try {
      database.getCollection(object.getClass().getSimpleName())
              .deleteOne(new Document("_id", object.getId()));
    } catch (MongoException e) {
      log.error(e);
    }
  }
}
