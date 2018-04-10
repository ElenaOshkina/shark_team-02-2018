package park.sharkteam.views.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Alex on 16.03.2018.
 */
public class UserForm {

    private String email;

    private String login;

    private String password;

    public UserForm(
            @JsonProperty("loginField") String login,
            @JsonProperty("emailField") String email,
            @JsonProperty(value = "passwordField", access = JsonProperty.Access.WRITE_ONLY) String password) {
        this.login = login;
        this.email = email;
        this.password = password;
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
