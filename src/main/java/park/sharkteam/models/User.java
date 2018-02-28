package park.sharkteam.models;

/**
 * Created by Alex on 19.02.2018.
 */
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.atomic.AtomicLong;

public class User {
    private static final AtomicLong COUNTER = new AtomicLong(0);
    private final long id;

    //Чтобы доставать/выдавать данные из/в тела запроса - Json-объекта
    @JsonProperty("email")
    private String email;
    @JsonProperty("login")
    private String login;
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public User() {
        this.id = COUNTER.getAndIncrement();
    }

    public User(String login, String email, String password) {
        this.id = COUNTER.getAndIncrement();
        this.login = login;
        this.email = email;
        this.password = password;
    }

    public long getId() {
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
}