package park.sharkteam.services;

import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import park.sharkteam.models.User;
import org.springframework.stereotype.Service;
import park.sharkteam.utilities.ErrorCoder;
import park.sharkteam.views.responses.ErrorResponse;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
public class UserService {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserService(PasswordEncoder passwordEncoder, JdbcTemplate template) {
        this.passwordEncoder = passwordEncoder;
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
                        passwordEncoder.encode(user.getPassword()),
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

    public User getUserById(Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE (users.id) = (?)",
                USER_MAPPER,
                id
        );
    }

    public User getUserByLoginPassword(String login, String password) {
        User user;
        try {
            user = jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE (users.login) = ?",
                    USER_MAPPER,
                    login
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        if (passwordEncoder.matches(password, user.getPassword())){
            return user;
        }
        return null;
    }

    public User updateUser(User user, Integer id) {
        String querry = "UPDATE users SET ";

        ArrayList params = new ArrayList() ;

        if (user.getLogin() != null) {
            querry += " login = ?,";
            params.add(user.getLogin());
        }
        if (user.getEmail() != null) {
            querry += " email = ?,";
            params.add(user.getEmail());
        }
        if (user.getPassword() != null) {
            querry += " password = ?,";
            params.add(passwordEncoder.encode(user.getPassword()));
        }
        querry = querry.substring(0, querry.length()-1);
        querry += " WHERE users.id = ? ;";

        params.add(id);
        if (!params.isEmpty()) {
            jdbcTemplate.update(querry, params.toArray());
        }
        return user;
    }

    public void updateScore(Integer id, Integer scores) {
        jdbcTemplate.update("UPDATE users SET score= score + ? WHERE id = ? ;", scores, id);
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