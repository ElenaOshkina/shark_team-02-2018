package park.sharkteam;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;

import park.sharkteam.services.UserService;
import park.sharkteam.models.User;

/**
 * Created by Alex on 03.04.2018.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class UserServiceTest extends Assert {

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private UserService userService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @After
    public void clearTestTable() {
        template.execute("TRUNCATE TABLE users");
    }

    @Test
    public void testAddingUser() {
        final User user = new User(
                "login",
                "email@email.com",
                "password"
        );

        final int userId =  userService.addUser(user);
        assertNotNull(userId);
        final User newUser = userService.getUserById(userId);
        assertEquals(user.getLogin(), newUser.getLogin());
        assertEquals(user.getPassword(), newUser.getPassword());
        assertEquals(user.getEmail(), newUser.getEmail());
    }

    @Test
    public void testAddingExistingUser() throws DuplicateKeyException {
        final User user = new User(
                "login",
                "email@email.com",
                "password"
        );
        userService.addUser(user);

        expectedException.expect(DuplicateKeyException.class);
        userService.addUser(user);
    }

    @Test
    public void testChangingUserInfo() throws DuplicateKeyException{
        final User user = new User(
                "login",
                "email",
                "password"
        );

        final int userId =  userService.addUser(user);

        final User newUserData = new User(
                "newLogin",
                "newEmail@email.com",
                "newPassword"
        );

        userService.updateUser(newUserData,userId);
        final User changedUser = userService.getUserById(userId);
        if(changedUser != null) {
            assertEquals("newLogin", changedUser.getLogin());
            assertEquals("newPassword", changedUser.getPassword());
            assertEquals("newEmail@email.com", changedUser.getEmail());
        }
    }

    @Test
    public void testScoreBoard() {
        final User firstUser = new User("login", "email", "password");
        final int firstUserId = userService.addUser(firstUser);
        assertNotNull(firstUserId);
        userService.updateScore(firstUserId, 100);

        final User secondUser = new User("login2", "email2", "password");
        final int secondUserId = userService.addUser(secondUser);
        assertNotNull(secondUserId);
        userService.updateScore(secondUserId, 1);

        final List<User> result = userService.getTopPlayers(2,0);

        assertEquals(result.get(0).getLogin(), firstUser.getLogin());
        assertEquals(result.get(1).getLogin(), secondUser.getLogin());
    }
}