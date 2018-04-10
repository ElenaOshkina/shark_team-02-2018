package park.sharkteam.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import park.sharkteam.models.User;
import org.springframework.stereotype.Service;
import javax.validation.constraints.NotNull;
import java.util.List;

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
        (response, rowNum) -> new Integer(
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
                 new Object[]{login},
                 USER_MAPPER
         );
    }

    public User getUserByEmail(String email) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.email) = ?",
                new Object[]{email},
                USER_MAPPER
        );
    }

    public User getUserById(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.id) = (?)",
                new Object[]{id},
                USER_MAPPER
        );
    }

    public User updateUser(User user, Integer id) {
        StringBuilder querry = new StringBuilder();

        querry.append("UPDATE users SET ");
        boolean changed = false;
        if (user.getEmail() != null) {
            querry.append("login = '" + user.getLogin() + "',");
            changed = true;
        }
        if (user.getLogin() != null) {
            querry.append("email = '" + user.getEmail() + "',");
            changed = true;
        }
        if (user.getPassword() != null) {
            querry.append("password = '" + user.getPassword() + "',");
            changed = true;
        }

        if (user.getAvatar() != null) {
            querry.append("avatar = '" + user.getAvatar() + "',");
            changed = true;
        }

        querry.deleteCharAt(querry.length() - 1);
        querry.append(" WHERE users.id = '" + id + "';");
        if (changed) {
            jdbcTemplate.update(querry.toString());
        }
        return user;
    }

    public void updateScore(Integer id, Integer scores) {
        jdbcTemplate.update("UPDATE users SET score=? WHERE id=?", scores, id);
    }

    public List<User> getTopPlayers(int limit, int offset) {
        return jdbcTemplate.query(
                "SELECT id, login, email, password, score "
                        + "FROM users "
                        + "ORDER BY score DESC, login ASC "
                        + "LIMIT ? OFFSET ?",
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