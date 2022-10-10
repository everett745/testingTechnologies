package ru.sfedu.testingTechcnologies.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import ru.sfedu.testingTechcnologies.Constants;
import ru.sfedu.testingTechcnologies.model.Entity;
import ru.sfedu.testingTechcnologies.model.RequestStatus;

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
  private static MongoClient mongoClient = null;
  private static MongoDatabase database = null;

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
      CodecRegistry defaultCodecRegistry = MongoClientSettings.getDefaultCodecRegistry();
      CodecRegistry fromProvider = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
      CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(defaultCodecRegistry, fromProvider);

      return mongoClient
              .getDatabase(PropertyProvider.getProperty(Constants.MONGO_DB_NAME))
              .withCodecRegistry(pojoCodecRegistry);
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

  private Document getFilterById(String id) {
    return new Document("_id", id);
  }

  public static MongoClientUtil getInstance() {
    if (INSTANCE == null) {
      Logger mongoLogger = initMongoLogger();
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

  public <T extends Entity> RequestStatus write(T object) {
    try {
      String className = object.getClass().getSimpleName();
      database.getCollection(className).insertOne(getObjectDocument(object));
      log.info(String.format("Element was written in table \"%s\"", className));
      return RequestStatus.SUCCESS;
    } catch (MongoException e) {
      log.error(e);
      return RequestStatus.FAILED;
    }
  }

  public <T extends Entity> RequestStatus write(List<T> objectList) {
    try {
      String className = objectList.get(0).getClass().getSimpleName();
      database.getCollection(className)
              .insertMany(objectList.stream().map(this::getObjectDocument).toList());
      log.info(String.format("Elements was written in table \"%s\"", className));
      return RequestStatus.SUCCESS;
    } catch (MongoException e) {
      log.error(e);
      return RequestStatus.FAILED;
    }
  }

  public <T extends Entity> List<T> readAll(Class<T> tClass) {
    try {
      List<T> resList = new ArrayList<>();
      FindIterable<T> iterDoc = database.getCollection(tClass.getSimpleName())
              .find(new Document(), tClass);

      for (T item : iterDoc) {
        resList.add(item);
      }
      return resList;
    } catch (MongoException e) {
      log.error(e);
      return new ArrayList<>();
    }
  }

  public <T extends Entity> T getById(Class<T> tClass, String id) {
    try {
      String className = tClass.getSimpleName();
      T item = database.getCollection(className)
              .find(getFilterById(id), tClass)
              .first();
      if (item == null) {
        log.error(String.format("Element with id=\"%s\" in table \"%s\" not found", id, className));
      }
      return item;
    } catch (MongoException e) {
      log.error(e);
      return null;
    }
  }

  public <T extends Entity> RequestStatus update(T object) {
    try {
      String className = object.getClass().getSimpleName();
      String objectId = object.getId();
      database.getCollection(className)
              .updateOne(
                      getFilterById(objectId),
                      new Document("$set", getObjectDocument(object))
              );
      log.info(String.format("Element \"%s\" was updated in table \"%s\"", objectId, className));
      return RequestStatus.SUCCESS;
    } catch (MongoException e) {
      log.error(e);
      return RequestStatus.FAILED;
    }
  }

  public <T extends Entity> RequestStatus delete(Class<T> tClass, String id) {
    try {
      String className = tClass.getSimpleName();
      database.getCollection(className)
              .deleteOne(getFilterById(id));
      log.info(String.format("Element \"%s\" was deleted from table \"%s\"", id, className));
      return RequestStatus.SUCCESS;
    } catch (MongoException e) {
      log.error(e);
      return RequestStatus.FAILED;
    }
  }

  public <T extends Entity> RequestStatus dropTable(Class<T> tClass) {
    try {
      database.getCollection(tClass.getSimpleName()).drop();
      log.info(String.format("Table \"%s\" was dropped", tClass.getSimpleName()));
      return RequestStatus.SUCCESS;
    } catch (MongoException e) {
      log.error(e);
      return RequestStatus.FAILED;
    }
  }
}
