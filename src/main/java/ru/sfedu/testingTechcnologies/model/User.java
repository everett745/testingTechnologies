package ru.sfedu.testingTechcnologies.model;

import lombok.Data;

@Data
public class User extends Entity {
  private String id;
  private String name;

  @Override
  public String toJSON() {
    return "{" +
            "_id:'" + id + '\'' +
            ", name:'" + name + '\'' +
            '}';
  }
}
