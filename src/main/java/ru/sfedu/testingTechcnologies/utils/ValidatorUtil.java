package ru.sfedu.testingTechcnologies.utils;

import lombok.extern.log4j.Log4j2;
import ru.sfedu.testingTechcnologies.model.Entity;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Log4j2
public class ValidatorUtil {

  private static Validator getValidator() {
    ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
    return vf.getValidator();
  }

  public static boolean validate(Entity object) {
    Validator validator = getValidator();
    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object);

    int errors = constraintViolations.size();

    if (errors > 0) {
      log.info(
        String.format(
          "Validate \"%s\" [id]=\"%s\"",
          object.getClass().getSimpleName(), object.getId()
        )
      );
      log.info(String.format("Errors: %d", constraintViolations.size()));

      for (ConstraintViolation<Object> cv : constraintViolations)
        log.info(
          String.format(
            "Error: { property: [%s], value: [%s], message: [%s] }",
            cv.getPropertyPath(), cv.getInvalidValue(), cv.getMessage())
        );

      log.info(String.format("%n"));
    }

    return errors == 0;
  }

}
