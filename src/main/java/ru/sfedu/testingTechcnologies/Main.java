package ru.sfedu.testingTechcnologies;

import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.sfedu.testingTechcnologies.api.CsvApi;
import ru.sfedu.testingTechcnologies.api.IApi;
import ru.sfedu.testingTechcnologies.api.MongoApi;
import ru.sfedu.testingTechcnologies.model.User;
import ru.sfedu.testingTechcnologies.utils.MongoClientUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
  public static void main(String[] args) {
    MongoClientUtil mongoClientUtil = MongoClientUtil.getInstance();

    User user = new User();
    user.setId(new ObjectId().toString());
    user.setName("Test second name !!!");

    IApi csvApi = CsvApi.getInstance();
    csvApi.createUser(user);

//    User uFromCsv = csvApi.getUser("ae7e25c1-a2f4-4304-b837-26738b1270e7");
//    mongoClientUtil.write(uFromCsv);


//    User user = new User();
//    user.setId(new ObjectId().toString());
//    user.setName("Test second name");
//    mongoClientUtil.write(user);
//    mongoClientUtil.readAll(user.getClass()).forEach(System.out::println);

//
//    User user = new User();
//    user.setId("63162813a353ac2eeb38ca70");
//    user.setName("Second name");
//    mongoClientUtil.write(user);
//    mongoClientUtil.update(user);
//    mongoClientUtil.readAll(user.getClass()).forEach(System.out::println);

//    mongoClientUtil.delete(user);
//    mongoClientUtil.readAll(user.getClass()).forEach(System.out::println);
  }
}