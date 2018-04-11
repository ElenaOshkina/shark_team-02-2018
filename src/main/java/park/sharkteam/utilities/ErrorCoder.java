package park.sharkteam.utilities;

/**
 * Created by Alex on 25.02.2018.
 */
public enum ErrorCoder {
    INVALID_EMAIL(0, "Email is not valid!"),
    INVALID_LOGIN(1, "Login is not valid!"),
    INVALID_PWD(2, "Password is not valid!"),
    INVALID_INFO(3, "Users data is uncorrect!"),
    USER_DUPLICATE(4, "User with such login is already exists!!"),
    EMPTY_FIELDS(5, "Fields are empty!"),
    UNCORRECT_PASSWORD(6, "Uncorrect login or password!"),
    USER_NOT_EXIST(6, "Uncorrect login or password!"),
    USER_NOT_LOGINED(7, "You are not logined!"),
    ALREADY_LOGGED(8, "You are already logined!"),
    UNCORRECT_FILE(9, "You are try to upload uncorrect file!"),
    UNEXISTED_FILE(10, "Avatar not exist!");
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
