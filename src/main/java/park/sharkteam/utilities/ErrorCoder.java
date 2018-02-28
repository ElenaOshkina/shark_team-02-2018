package park.sharkteam.utilities;

/**
 * Created by Alex on 25.02.2018.
 */
public enum ErrorCoder {
    NOT_VALID_EMAIL(0, "Email is not valid!"),
    NOT_VALID_LOGIN(1, "Login is not valid!"),
    NOT_VALID_PWD(2, "Password is not valid!"),
    USER_DUPLICATE(3, "User with such login is already exists!!"),
    EMPTY_FIELDS(4, "Fields are empty!"),
    UNCORRECT_PASSWORD(5, "Uncorrect login or password!"),
    USER_NOT_EXIST(5, "Uncorrect login or password!"),
    USER_NOT_LOGINED(6, "You are not logined!");
    private Integer errorCode;
    private String message;

    ErrorCoder(Integer code, String msg) {
        this.errorCode = code;
        this.message = msg;
    }

    public String getMsg() {
        return message;
    }

    public Integer getCode() {
        return errorCode;
    }
}
