package park.sharkteam;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import park.sharkteam.services.UserService;
import park.sharkteam.models.User;

import java.util.Map;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Alex on 03.04.2018.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@Transactional
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSignupUser() throws Exception {
        //BAD_REQUEST
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                .header("content-type", "application/json")
                .content(
                        mapper.writeValueAsString(Map.of(
                                "loginField","",
                                "emailField","",
                                "passwordField","password"
                                )
                        )
                )
        ).andExpect(status().is4xxClientError());

        //OK_RESPONSE
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(
                             mapper.writeValueAsString(Map.of(
                                    "loginField","login",
                                    "emailField","user@mail.ru",
                                    "passwordField","password"
                                     )
                             )
                        )
        ).andExpect(status().isOk());

        //FORBIDDEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","login",
                                        "emailField","user@mail.ru",
                                        "passwordField","password"
                                        )
                                )
                        )
        ).andExpect(status().is4xxClientError());
    }

    @Test
    public void testSigninUser() throws Exception {
        //SignUp

        //OK_RESPONSE
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","login",
                                        "emailField","user@mail.ru",
                                        "passwordField","password"
                                        )
                                )
                        )
        ).andExpect(status().isOk());

        final User user = userService.getUserByLogin("login");
        assertNotNull(user);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .sessionAttr("id", user.getId())
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","login",
                                        "emailField","user@mail.ru",
                                        "passwordField","password"
                                        )
                                )
                        )
        ).andExpect(status().is4xxClientError());


        //logout
        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/logout")
                .header("content-type", "application/json")
                .sessionAttr("id", user.getId())
        ).andExpect(status().isOk());

        //FORBIDDEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/logout")
                .header("content-type", "application/json")
        ).andExpect(status().is4xxClientError());

        //FORBIDDEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","login",
                                        "passwordField","uncorrect_password"
                                        )
                                )
                        )
        ).andExpect(status().is4xxClientError());

        //FORBIDDEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","",
                                        "passwordField",""
                                        )
                                )
                        )
        ).andExpect(status().is4xxClientError());

        //NOT_FOUND
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","uncorrect_login",
                                        "passwordField","password"
                                        )
                                )
                        )
        ).andExpect(status().is4xxClientError());

        //OK
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","login",
                                        "passwordField","password"
                                        )
                                )
                        )
        ).andExpect(status().isOk());

        //FORBIDDEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .sessionAttr("id", user.getId())
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","login",
                                        "passwordField","password"
                                        )
                                )
                        )
        ).andExpect(status().is4xxClientError());
    }

    @Test
    public void testChange() throws Exception {

        //FORBIDDEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me")
                .header("content-type", "application/json")
        )
        .andExpect(status().is4xxClientError());

        //FORBIDDEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me")
                .header("content-type", "application/json")
                .sessionAttr("id", -1)
        )
        .andExpect(status().is4xxClientError());

        //SignUp
        //OK_RESPONSE
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","login",
                                        "emailField","user@mail.ru",
                                        "passwordField","password"
                                        )
                                )
                        )
        ).andExpect(status().isOk());

        final User user = userService.getUserByLogin("login");
        assertNotNull(user);

        //Forbidden
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/me")
                .header("content-type", "application/json")
                .content(
                        mapper.writeValueAsString(Map.of(
                                "loginField","new_login",
                                "emailField","new_email@mail.ru",
                                "passwordField","new_password"
                                )
                        )
                )
        ).andExpect(status().is4xxClientError());

        //Forbidden
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/me")
                .header("content-type", "application/json")
                .sessionAttr("id",-1)
                .content(
                        mapper.writeValueAsString(Map.of(
                                "loginField","new_login",
                                "emailField","new_email@mail.ru",
                                "passwordField","new_password"
                                )
                        )
                )
        ).andExpect(status().is4xxClientError());
        //Forbidden
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/me")
                .sessionAttr("id", user.getId())
                .header("content-type", "application/json")
                .content(
                        mapper.writeValueAsString(Map.of(
                                "loginField","",
                                "emailField","",
                                "passwordField",""
                                )
                        )
                )
        ).andExpect(status().is4xxClientError());

        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/me")
                .sessionAttr("id", user.getId())
                .header("content-type", "application/json")
                .content(
                        mapper.writeValueAsString(Map.of(
                                "loginField","new_login",
                                "emailField","new_email@mail.ru",
                                "passwordField","new_password"
                                )
                        )
                )
        ).andExpect(status().isOk());

        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me")
                .sessionAttr("id", user.getId())
                .header("content-type", "application/json")
        )
        .andExpect(status().isOk());

        //logout
        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/logout")
                .sessionAttr("id", user.getId())
                .header("content-type", "application/json")
        ).andExpect(status().isOk());

        //OK
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","new_login",
                                        "passwordField","new_password"
                                        )
                                )
                        )
        ).andExpect(status().isOk());
    }

    @Test
    public void testScore() throws Exception {
        final User firstUser = new User("login1", "email1", "password");
        final int firstUserId = userService.addUser(firstUser);
        assertNotNull(firstUserId);
        userService.updateScore(firstUserId, 100);

        final User secondUser = new User("login2", "email2", "password");
        final int secondUserId = userService.addUser(secondUser);
        assertNotNull(secondUserId);
        userService.updateScore(secondUserId, 1);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/score")
                .header("content-type", "application/json")
        )
        .andExpect(status().is4xxClientError());

        //OK_RESPONSE
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "loginField","login",
                                        "emailField","email@mail.ru",
                                        "passwordField","password"
                                        )
                                )
                        )
        ).andExpect(status().isOk());

        final User user = userService.getUserByLogin("login");
        assertNotNull(user);

        //FORBIDDEN
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/score")
                        .header("content-type", "application/json")
        ).andExpect(status().is4xxClientError());

        //OK_RESPONSE
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/users/score")
                        .header("content-type", "application/json")
                        .sessionAttr("id", user.getId())
                        .content(
                                mapper.writeValueAsString(Map.of(
                                        "startPos","0",
                                        "numberElements","3")
                                )
                        )
        ).andExpect(status().isOk());
    }
}
