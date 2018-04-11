package park.sharkteam.models;

/**
 * Created by Alex on 19.02.2018.
 */
import com.fasterxml.jackson.annotation.JsonProperty;


public class User {
    private final int id;

    @JsonProperty("email")
    private String email;
    @JsonProperty("login")
    private String login;
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty("score")
    private int score;
    @JsonProperty("avatar")
    private String avatar;

    public User(
            int id,
            String login,
            String email,
            String password,
            int score,
            String avatar
    ) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.password = password;
        this.score = score;
        this.avatar = avatar;
    }

    public User(String login, String email, String password) {
        this.id = 0;
        this.login = login;
        this.email = email;
        this.password = password;
        this.score = 0;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
