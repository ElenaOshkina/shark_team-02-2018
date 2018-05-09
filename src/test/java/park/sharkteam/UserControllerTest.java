package park.sharkteam;

import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import park.sharkteam.controllers.UserController;
import park.sharkteam.services.UserService;
import park.sharkteam.models.User;
import park.sharkteam.views.requests.UserForm;

import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.jdbc.JdbcTestUtils.countRowsInTable;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Test
    public void testSignupUser() throws Exception {


        //BAD_REQUEST
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                .header("content-type", "application/json")
                .content(JsonBuilder.signUpForm("","",""))
        ).andExpect(status().is4xxClientError());

        //OK_RESPONSE
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(JsonBuilder.signUpForm("login","password","user@mail.ru"))
        ).andExpect(status().isOk());

        //FORBIDDEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(JsonBuilder.signUpForm("login","password","user@mail.ru"))
        ).andExpect(status().is4xxClientError());
    }

    @Test
    public void testSigninUser() throws Exception {
        //SignUp

        //OK_RESPONSE
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(JsonBuilder.signUpForm("login","password","user@mail.ru"))
        ).andExpect(status().isOk());

        final User user = userService.getUserByLogin("login");
        assertNotNull(user);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .sessionAttr("id", user.getId())
                        .content(JsonBuilder.signUpForm("login","password","user@mail.ru"))
        ).andExpect(status().is4xxClientError());


        //logout
        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/logout")
                .header("content-type", "application/json")
                .sessionAttr("id", user.getId())
                .content("")
        ).andExpect(status().isOk());

        //FORBIDDEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/logout")
                .header("content-type", "application/json")
                .content("")
        ).andExpect(status().is4xxClientError());

        //FORBIDDEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(JsonBuilder.signInForm("login","uncorrect_password"))
        ).andExpect(status().is4xxClientError());

        //FORBIDDEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(JsonBuilder.signInForm("",""))
        ).andExpect(status().is4xxClientError());

        //NOT_FOUND
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(JsonBuilder.signInForm("uncorrect_login","password"))
        ).andExpect(status().is4xxClientError());

        //OK
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(JsonBuilder.signInForm("login","password"))
        ).andExpect(status().isOk());

        //FORBIDDEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .sessionAttr("id", user.getId())
                        .content(JsonBuilder.signInForm("login","password"))
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
                        .content(JsonBuilder.signUpForm("login","password","user@mail.ru"))
        ).andExpect(status().isOk());

        final User user = userService.getUserByLogin("login");
        assertNotNull(user);

        //Forbidden
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/me")
                .header("content-type", "application/json")
                .content(JsonBuilder.signUpForm("new_login","new_password","new_email@mail.ru"))
        ).andExpect(status().is4xxClientError());

        //Forbidden
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/me")
                .header("content-type", "application/json")
                .sessionAttr("id",-1)
                .content(JsonBuilder.signUpForm("new_login","new_password","new_email@mail.ru"))
        ).andExpect(status().is4xxClientError());
        //Forbidden
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/me")
                .sessionAttr("id", user.getId())
                .header("content-type", "application/json")
                .content(JsonBuilder.signUpForm("","",""))
        ).andExpect(status().is4xxClientError());

        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/me")
                .sessionAttr("id", user.getId())
                .header("content-type", "application/json")
                .content(JsonBuilder.signUpForm("new_login","new_password","new_email@mail.ru"))
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
                .content("")
        ).andExpect(status().isOk());

        //OK
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(JsonBuilder.signUpForm("new_login","new_password","new_email@mail.ru"))
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
                        .content(JsonBuilder.signUpForm("login","password","user@mail.ru"))
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
                        .content(JsonBuilder.scoreForm("0","3"))
        ).andExpect(status().isOk());
    }
}
