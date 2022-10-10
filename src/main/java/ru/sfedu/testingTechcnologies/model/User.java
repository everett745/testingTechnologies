package ru.sfedu.testingTechcnologies.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class User extends Entity {
  @NotNull(message = "ID cannot be null")
  private String id;

  @NotNull(message = "Name cannot be null")
  @Size(min = 3, message="Name must be more than 3 characters")
  private String name;

  @NotNull(message = "Traffic cannot be null")
  private String traffic;

  @NotNull(message = "Birthdate cannot be null")
  @Pattern(
          regexp = "^[0-9]{1,2}-[0-9]{1,2}-[0-9]{4}$",
          message = "Date format: **-**-****"
  )
  private String birthday;

  @NotNull(message = "City cannot be null")
  private String city;

  @NotNull(message = "Street cannot be null")
  private String street;

  private String home;

  @Override
  public String toJSON() {
    return "{" +
            "_id:'" + id + '\'' +
            ", name:'" + name + '\'' +
            ", traffic:'" + traffic + '\'' +
            ", birthday:'" + birthday + '\'' +
            ", city:'" + city + '\'' +
            ", street:'" + street + '\'' +
            ", home:'" + home + '\'' +
            '}';
  }
}
