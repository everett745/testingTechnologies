package ru.sfedu.testingTechcnologies.model;

import org.bson.types.ObjectId;

public abstract class Entity {
  public abstract String getId();
  public abstract String toJSON();
}
