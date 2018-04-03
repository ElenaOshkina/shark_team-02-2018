package park.sharkteam.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpSession;

import park.sharkteam.services.UserService;
import park.sharkteam.models.User;
import park.sharkteam.utilities.ErrorCoder;
import park.sharkteam.views.ErrorResponce;
import park.sharkteam.views.SuccessResponce;

/**
 * Created by Alex on 19.02.2018.
 */

@RestController
//ToDo: реальный URL фронтенд-сервера
@CrossOrigin(origins = {"http://frontend_site.herokuapp.com", "http://localhost:3000", "http://127.0.0.1:3000"})
@RequestMapping(path = "/api/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User body) {
        final String login = body.getLogin();
        final String email = body.getEmail();
        final String password = body.getPassword();

        if (
                StringUtils.isEmpty(login)
                        || StringUtils.isEmpty(email)
                        || StringUtils.isEmpty(password)
                ) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponce(ErrorCoder.EMPTY_FIELDS));
        }

        if (userService.getUser(login) != null) {
            // Отправка запроса с ошибкой
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponce(ErrorCoder.USER_DUPLICATE));
        }

        final User user = new User(login, email, password);
        userService.addUser(user);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody User body, HttpSession httpSession) {
        final String login = body.getLogin();
        final String password = body.getPassword();

        if (
                StringUtils.isEmpty(login)
                        || StringUtils.isEmpty(password)
                ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponce(ErrorCoder.EMPTY_FIELDS));
        }

        if (httpSession.getAttribute("login") != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponce(ErrorCoder.ALREADY_LOGGED));
        }

        final User currentUser = userService.getUser(login);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponce(ErrorCoder.USER_NOT_EXIST));
        }

        if (!currentUser.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponce(ErrorCoder.UNCORRECT_PASSWORD));
        }

        //ToDo: Когда будет БД - в аттрибут записывать id
        httpSession.setAttribute("login", login);
        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logOut(HttpSession httpSession) {

        if (httpSession.getAttribute("login") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponce(ErrorCoder.USER_NOT_LOGINED));
        }

        httpSession.setAttribute("login", null);
        httpSession.invalidate();

        return ResponseEntity.ok(new SuccessResponce("User is successfully log out!"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(HttpSession httpSession) {

        final String currentUserLogin = (String) httpSession.getAttribute("login");

        if (currentUserLogin == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponce(ErrorCoder.USER_NOT_LOGINED));
        }
        User currentUser = userService.getUser(currentUserLogin);
        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/me")
    public ResponseEntity<?> changeUserData(@RequestBody User body, HttpSession httpSession) {

        final String currentUserLogin = (String) httpSession.getAttribute("login");

        if (currentUserLogin == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponce(ErrorCoder.USER_NOT_LOGINED));
        }

        User currentUser = userService.getUser(currentUserLogin);

        final String oldLogin = currentUser.getLogin();
        final String newLogin = body.getLogin();
        final String newPassword = body.getPassword();
        final String newEmail = body.getPassword();

        if (
                StringUtils.isEmpty(newLogin)
                        && StringUtils.isEmpty(newPassword)
                        && StringUtils.isEmpty(newEmail)
                ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponce(ErrorCoder.EMPTY_FIELDS));
        }

        if (!StringUtils.isEmpty(newLogin)) {
            currentUser.setLogin(newLogin);
            //ToDo убрать когда будет id
            httpSession.setAttribute("login", newLogin);
        }

        if (!StringUtils.isEmpty(newPassword)) {
            currentUser.setPassword(newPassword);
        }
        if (!StringUtils.isEmpty(newEmail)) {
            currentUser.setEmail(newEmail);
        }

        userService.removeUser(oldLogin);
        userService.addUser(currentUser);

        return ResponseEntity.ok(currentUser);
    }
}
