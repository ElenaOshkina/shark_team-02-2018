package com.Services;

import org.springframework.stereotype.Service;
import java.util.HashMap;

import com.Models.User;

@Service
public class UserService {
    // "БД" пользователей
    private final HashMap<String, User> registeredUser = new HashMap<String, User>();

    // Регистрация нового пользователя
    @SuppressWarnings("unused")
    public void addUser(String login, String email, String password) {
        registeredUser.put(login, new User(login, email, password));
    }

    public void addUser(User user) {
        registeredUser.put(user.getLogin(), user);
    }

    // Поиск пользователя
    @SuppressWarnings("unused")
    public User getUser(String login) {
        return registeredUser.get(login);
    }

    // Удаление существующего пользователя
    @SuppressWarnings("unused")
    public void removeUser(String login) {
        registeredUser.remove(login);
    }
}