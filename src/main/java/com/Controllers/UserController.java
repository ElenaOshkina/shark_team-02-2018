package com.controllers;

import com.utilities.ErrorCoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpSession;

import com.services.UserService;
import com.models.User;

import com.views.*;

/**
 * Created by Alex on 19.02.2018.
 */

@RestController
//ToDo: реальный URL
@CrossOrigin(origins = {"http://frontend_site.herokuapp.com", "http://localhost:3000", "http://127.0.0.1:3000"})
@RequestMapping(path = "/api/users")
public class UserController {
    private UserService userService;

    //Конструктор
    @Autowired
    public UserController(UserService userService) {
        //userService - занимается работой с БД
        this.userService = userService;
    }

    //Получением информации о текущем пользователе
    // Метод: Get
    // Адрес: "/user"
    @RequestMapping(path = "/signup",
            method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity signUp(@RequestBody User body) {
        final String login = body.getLogin();
        final String email = body.getEmail();
        final String password = body.getPassword();

        // Проверка: пустые ли поля?
        if (
            StringUtils.isEmpty(login)
            || StringUtils.isEmpty(email)
            || StringUtils.isEmpty(password)
        ) {
            // Отправка запроса с ошибкой
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCoder.EMPTY_FIELDS));
        }

        // Проверка: существует ли уже такой пользователь?
        if (userService.getUser(login) != null) {
            // Отправка запроса с ошибкой
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCoder.USER_DUPLICATE));
        }

        // Все проверки пройдены!
        // Регистрация пользователя
        final User user = new User(login, email, password);
        userService.addUser(user);

        return ResponseEntity.ok(user);
    }

    //Авторизация пользователя
    // Метод: Post
    // Адрес: "/signin"
    @RequestMapping(path = "/signin",
            method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity signIn(@RequestBody User body, HttpSession httpSession) {
        final String login = body.getLogin();
        final String password = body.getPassword();

        if (
            StringUtils.isEmpty(login)
            || StringUtils.isEmpty(password)
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ErrorCoder.EMPTY_FIELDS));
        }

        // Проверка: пустые ли поля?
        if (httpSession.getAttribute("login") != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCoder.USER_DUPLICATE));
        }

        //Получаем пользователя
        final User currentUser = userService.getUser(login);

        // Проверка: существует ли такой пользователь?
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ErrorCoder.USER_NOT_EXIST));
        }

        // Проверка: правильный ли введен пароль?
        if (!currentUser.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ErrorCoder.UNCORRECT_PASSWORD));
        }

        // Все проверки пройдены!
        // Авторизация пользователя
        //ToDo: Когда будет БД - в аттрибут записывать id
        httpSession.setAttribute("login", login);
        return ResponseEntity.ok(currentUser);
    }

    //Разлогинивание пользователя
    // Метод: Post
    // Адрес: "/logout"
    @RequestMapping(path = "/logout",
            method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity logOut(HttpSession httpSession) {
        //Проверка: залогинен ли пользова
        if (httpSession.getAttribute("login") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCoder.USER_NOT_LOGINED));
        }

        // Все проверки пройдены!
        // Разлогиниваем пользователя
        httpSession.removeAttribute("login");
        return ResponseEntity.ok(new SuccessResponce("User is successfully log out!"));
    }

    //Получение информации о текущем пользователя
    // Метод: Get
    // Адрес: "/"
    @RequestMapping(path = "/",
            method = RequestMethod.GET,
            produces = "application/json"
    )
    public ResponseEntity currentUser(HttpSession httpSession) {
        //Проверка: залогинен ли пользователь
        final String currentUserLogin = (String) httpSession.getAttribute("login");

        if (currentUserLogin == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCoder.USER_NOT_LOGINED));
        }
        User currentUser = userService.getUser(currentUserLogin);
        return ResponseEntity.ok(currentUser);
    }
}
