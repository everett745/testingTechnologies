import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import ru.sfedu.testingTechcnologies.api.CsvApi;
import ru.sfedu.testingTechcnologies.api.MongoApi;
import ru.sfedu.testingTechcnologies.model.RequestStatus;
import ru.sfedu.testingTechcnologies.model.User;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log4j2
public class MongoApiTest {
    public static CsvApi csv = CsvApi.getInstance();
    public static MongoApi mongo = MongoApi.getInstance();

    String getDataSourcePath() {
        Path currentRelativePath = Paths.get("src/test/java/data/dataSource.csv");
        return currentRelativePath.toAbsolutePath().toString();
    }

    List<User> getUsersFromDataSource() {
        return csv.getUsersFromFile(getDataSourcePath());
    }

    @Test
    public void writeUser() {
        mongo.dropUsers();
        User user = getUsersFromDataSource().get(0);
        RequestStatus status = mongo.addUser(user);
        assertEquals(status, RequestStatus.SUCCESS, "Не удалось записать пользователя");
    }

    @Test
    public void writeUsers() {
        mongo.dropUsers();
        RequestStatus status = mongo.addUsers(getUsersFromDataSource());
        assertEquals(status, RequestStatus.SUCCESS, "Не удалось записать пользователей");
    }

    @Test
    public void getUsers() {
        writeUsers();

        List<User> mongoUsers = mongo.getUsers();
        assertTrue(mongoUsers.size() > 0, "Не удалось получить пользователей");
    }

    @Test
    public void getUserById() {
        writeUsers();

        User user = getUsersFromDataSource().get(0);
        User mongoUser = mongo.getUser(user.getId());
        assertNotNull(mongoUser, "Пользователь не найден");

        assertAll("user",
                () -> assertEquals(user.getId(), mongoUser.getId(), "id не совпадает"),
                () -> assertEquals(user.getName(), mongoUser.getName(), "имя не совпадает")
        );
    }

    @Test
    public void editUser() {
        writeUsers();

        List<User> mongoUsers = mongo.getUsers();
        assertTrue(mongoUsers.size() > 0, "Не удалось получить пользователей");
        User mongoUser = mongoUsers.get(0);
        assertNotNull(mongoUser, "Пользователь не найден");

        mongoUser.setName("New user name");
        mongoUser.setCity("New City");

        RequestStatus status = mongo.editUser(mongoUser);
        assertEquals(status, RequestStatus.SUCCESS, "Не удалось изменить пользователя");

        User editedUser = mongo.getUser(mongoUser.getId());
        assertNotNull(mongoUser, "Пользователь не найден после редактирования");

        assertAll("user",
                () -> assertEquals(mongoUser.getId(), editedUser.getId(), "id не совпадает"),
                () -> assertEquals(mongoUser.getName(), editedUser.getName(), "name не совпадает"),
                () -> assertEquals(mongoUser.getCity(), editedUser.getCity(), "city не совпадает")
        );
    }

    @Test
    public void deleteUser() {
        writeUsers();

        List<User> mongoUsers = mongo.getUsers();
        assertTrue(mongoUsers.size() > 0, "Не удалось получить пользователей");

        String deletedUserId = mongoUsers.get(0).getId();

        RequestStatus status = mongo.deleteUser(deletedUserId);
        assertEquals(status, RequestStatus.SUCCESS, "Пользователь не был удален");

        User deletedUser = mongo.getUser(deletedUserId);

        assertNull(deletedUser, "Пользователя удалось получить после удаления");
    }
}
