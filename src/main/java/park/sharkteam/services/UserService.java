package park.sharkteam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import park.sharkteam.models.User;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotNull;
import java.util.*;

@Service
public class UserService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    private static final RowMapper<User> USER_MAPPER = (responce, num) ->
            new User(
                    responce.getInt("id"),
                    responce.getString("login"),
                    responce.getString("email"),
                    responce.getString("password"),
                    responce.getInt("score"),
                    responce.getString("avatar")
             );

    public int addUser(@NotNull User user) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO users(login, email, password, score) VALUES(?, ?, ?, ?)"
                        + "RETURNING id",
                        (response, rowNum) -> (
                                response.getInt("id")
                        ),
                        user.getLogin(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getScore()
        );
    }

     public User getUserByLogin(String login) {
         return jdbcTemplate.queryForObject(
                 "SELECT * FROM users WHERE (users.login) = ?",
                 USER_MAPPER,
                 login
         );
    }

    public User getUserByEmail(String email) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.email) = ?",
                USER_MAPPER,
                email
        );
    }

    public User getUserById(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.id) = (?)",
                USER_MAPPER,
                id
        );
    }

    public User updateUser(User user, Integer id) {
        StringBuilder querry = new StringBuilder();
        querry.append("UPDATE users SET ");

        List<String> updatedValues = new ArrayList<String>();

        if (user.getEmail() != null) {
            updatedValues.add("login = '" + user.getLogin() + "'");
        }
        if (user.getLogin() != null) {
            updatedValues.add("email = '" + user.getEmail() + "'");
        }
        if (user.getPassword() != null) {
            updatedValues.add("password = '" + user.getPassword() + "'");
        }

        querry.append(String.join(", ", updatedValues) + " WHERE users.id = '" + id + "';");
        if (!updatedValues.isEmpty()) {
            jdbcTemplate.update(querry.toString());
        }
        return user;
    }

    public void updateScore(Integer id, Integer scores) {
        jdbcTemplate.update("UPDATE users SET score=? WHERE id=?", scores, id);
    }

    public List<User> getTopPlayers(int limit, int offset) {
        return jdbcTemplate.query(
                "SELECT * "
                        + "FROM users "
                        + "ORDER BY score DESC, login ASC "
                        + "LIMIT ? OFFSET ?",
                USER_MAPPER,
                limit,
                offset
        );
    }
}