import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import ru.sfedu.testingTechcnologies.api.CsvApi;
import ru.sfedu.testingTechcnologies.model.User;
import ru.sfedu.testingTechcnologies.utils.ValidatorUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
public class CsvApiTest {
    public static CsvApi csv = CsvApi.getInstance();

    String getDataSourcePath() {
        Path currentRelativePath = Paths.get("src/test/java/data/dataSource.csv");
        return currentRelativePath.toAbsolutePath().toString();
    }

    String getDataWithIncorrectSourcePath() {
        Path currentRelativePath = Paths.get("src/test/java/data/dataSourceWithIncorrect.csv");
        return currentRelativePath.toAbsolutePath().toString();
    }

    String getIncorrectDataSourcePath() {
        Path currentRelativePath = Paths.get("src/test/java/data/error");
        return currentRelativePath.toAbsolutePath().toString();
    }

    String getEmptyDataSourcePath() {
        Path currentRelativePath = Paths.get("src/test/java/data/empty.csv");
        return currentRelativePath.toAbsolutePath().toString();
    }

    @Test
    void getFileCorrect() {
        String path = getDataSourcePath();
        csv.getUsersFromFile(path);
    }

    @Test
    void getFileInCorrect() {
        String path = getIncorrectDataSourcePath();
        csv.getUsersFromFile(path);
    }

    @Test
    void getFileEmpty() {
        String path = getEmptyDataSourcePath();
        csv.getUsersFromFile(path);
    }

    @Test
    void getFromCsv() {
        String path = getDataSourcePath();
        List<User> users = csv.getUsersFromFile(path);

        assertTrue(users.size() > 0, "Пользователи не найдены в файле: " + path);

        log.info("Пользователей получено в файле :" + users.size());
    }

    @Test
    void validation() {
        String path = getDataSourcePath();
        List<User> users = csv.getUsersFromFile(path);

        assertTrue(
                users.stream().allMatch(ValidatorUtil::validate),
                "Проверка не прошла"
        );
    }

    @Test
    void validationIncorrect() {
        String path = getDataWithIncorrectSourcePath();
        List<User> users = csv.getUsersFromFile(path);

        List<Boolean> list = users.stream()
                .map(ValidatorUtil::validate)
                .toList();

        assertTrue(list.stream().anyMatch(v -> !v), "Проверка прошла");
    }
}
