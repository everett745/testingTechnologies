import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import ru.sfedu.testingTechcnologies.api.CsvToMongo;
import ru.sfedu.testingTechcnologies.api.MongoApi;
import ru.sfedu.testingTechcnologies.model.RequestStatus;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class CsvToMongoTest {
  public static CsvToMongo api = CsvToMongo.getInstance();
  public static MongoApi mongo = MongoApi.getInstance();

  String getDataSourcePath() {
    Path currentRelativePath = Paths.get("src/test/java/data/dataSource.csv");
    return currentRelativePath.toAbsolutePath().toString();
  }

  @Test
  void parseCsv() {
    mongo.dropUsers();
    assertEquals(
            api.moveUsersFromCsvToMongo(getDataSourcePath()),
            RequestStatus.SUCCESS,
            "Ошибка перемещения"
    );
  }

}
