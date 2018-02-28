package park.sharkteam.services;

import park.sharkteam.models.User;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
public class UserService {

    private final HashMap<String, User> registeredUser = new HashMap<String, User>();

    public void addUser(String login, String email, String password) {
        registeredUser.put(login, new User(login, email, password));
    }

    public void addUser(User user) {
        registeredUser.put(user.getLogin(), user);
    }

    public User getUser(String login) {
        return registeredUser.get(login);
    }

    public void removeUser(String login) {
        registeredUser.remove(login);
    }

}