package park.sharkteam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import park.sharkteam.models.User;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(JdbcTemplate template) {
        this.jdbcTemplate = template;
        deleteTable();
        createTable();
    }

    public void createTable() {
        final String createTableQuery =
                  "CREATE TABLE IF NOT EXISTS  users (" +
                        "id SERIAL NOT NULL PRIMARY KEY," +
                        "login VARCHAR(255) NOT NULL UNIQUE," +
                        "email VARCHAR(255) NOT NULL UNIQUE," +
                        "password VARCHAR(255) NOT NULL," +
                        "score INTEGER DEFAULT 0" +
                  ");";
        jdbcTemplate.execute(createTableQuery);
    }

    public void deleteTable() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS users CASCADE");
    }

    private static final RowMapper<User> USER_MAPPER = (responce, num) ->
            new User(
                    responce.getInt("id"),
                    responce.getString("login"),
                    responce.getString("password"),
                    responce.getString("email"),
                    responce.getInt("score")
             );

    public int addUser(@NotNull User user) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO users(login, email, password, score) VALUES(?, ?, ?, ?)" +
                        "RETURNING id",
                new Object[]{
                        user.getLogin(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getScore()
                },
                (response, rowNum) -> new Integer(
                        response.getInt("id")
                )
        );
    }

     public User getUserByLogin(String login) {
         return jdbcTemplate.queryForObject(
                 "SELECT * FROM users WHERE (users.login) = ?",
                 new Object[]{login},
                 (response, rowNum) -> new User(
                         response.getInt("id"),
                         response.getString("login"),
                         response.getString("email"),
                         response.getString("password"),
                         response.getInt("score")
                 )
         );
    }

    public User getUserByEmail(String email) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.email) = ?",
                new Object[]{email},
                (response, rowNum) -> new User(
                        response.getString("login"),
                        response.getString("email"),
                        response.getString("password"),
                        response.getInt("score")
                )
        );
    }

    public User getUserById(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.id) = (?)",
                new Object[]{id},
                (response, rowNum) -> new User(
                        response.getString("login"),
                        response.getString("email"),
                        response.getString("password"),
                        response.getInt("score")
                )
        );
    }

    public User updateUser(User user, Integer id){
        StringBuilder querry = new StringBuilder();

        querry.append("UPDATE users SET ");
        boolean f = false;
        if (user.getEmail() != null) {
            querry.append("login = '" + user.getLogin() + "',");
            f = true;
        }
        if (user.getLogin() != null) {
            querry.append("email = '" + user.getEmail() + "',");
            f = true;
        }
        if (user.getPassword() != null) {
            querry.append("password = '" + user.getPassword() + "',");
            f = true;
        }

        querry.deleteCharAt(querry.length() - 1);
        querry.append(" WHERE users.id = '" + id + "';");
        if (f) {
            jdbcTemplate.update(querry.toString());
        }
        return user;
    }

    public void updateScore(Integer id, Integer scores) {
        jdbcTemplate.update("UPDATE users SET score=? WHERE id=?", scores, id);
    }

    public List<User> getTopPlayers(int limit, int offset) {
        return jdbcTemplate.query(
                "SELECT id, login, email, password, score " +
                        "FROM users " +
                        "ORDER BY score DESC, login ASC " +
                        "LIMIT ? OFFSET ?",
                new Object[]{limit, offset},
                (response, rowNum) -> new User(
                        response.getString("login"),
                        response.getString("email"),
                        response.getString("password"),
                        response.getInt("score")
                )
        );
    }
}