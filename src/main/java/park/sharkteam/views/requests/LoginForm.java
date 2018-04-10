package park.sharkteam.views.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginForm {

    private String login;

    private String password;

    public LoginForm(
            @JsonProperty("loginField") String login,
            @JsonProperty(value = "passwordField", access = JsonProperty.Access.WRITE_ONLY) String password) {
        this.login = login;
        this.password = password ;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
