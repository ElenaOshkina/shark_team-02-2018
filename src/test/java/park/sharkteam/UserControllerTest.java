package park.sharkteam;

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
                        .content(
                                "{" +
                                        "\"loginField\":\"\", " +
                                        "\"passwordField\":\"\"," +
                                        " \"emaiFieldl\":\"\"" +
                                        "}"
                        )
        ).andExpect(status().is4xxClientError());


        //OK_RESPONSE
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(
                                "{" +
                                        "\"loginField\":\"login\", " +
                                        "\"passwordField\":\"password\"," +
                                        " \"emailField\":\"user@mail.ru\"" +
                                        "}"
                        )
        ).andExpect(status().isOk());

        //FORBIDDEN
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(
                                "{" +
                                        "\"loginField\":\"login\", " +
                                        "\"passwordField\":\"password\"," +
                                        " \"emailField\":\"user@mail.ru\"" +
                                        "}"
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
                                "{" +
                                        "\"loginField\":\"login\", " +
                                        "\"passwordField\":\"password\"," +
                                        " \"emailField\":\"user@mail.ru\"" +
                                        "}"
                        )
        ).andExpect(status().isOk());

        final User user = userService.getUserByLogin("login");
        assertNotNull(user);

        //logout
        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/logout")
                .header("content-type", "application/json")
                .sessionAttr("id", user.getId())
                .content("")
        ).andExpect(status().isOk());


        //NOT_FOUND
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(
                                "{" +
                                        "\"loginField\":\"login\", " +
                                        "\"passwordField\":\"uncorrect_password\"," +
                                        " \"emailField\":\"user@mail.ru\"" +
                                        "}"
                        )
        ).andExpect(status().is4xxClientError());

        //NOT_FOUND
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(
                                "{" +
                                        "\"loginField\":\"uncorrect_login\", " +
                                        "\"passwordField\":\"password\"," +
                                        " \"emailField\":\"user@mail.ru\"" +
                                        "}"
                        )
        ).andExpect(status().is4xxClientError());

        //OK
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(
                                "{" +
                                        "\"loginField\":\"login\", " +
                                        "\"passwordField\":\"password\"," +
                                        " \"emailField\":\"user@mail.ru\"" +
                                        "}"
                        )
        ).andExpect(status().isOk());
    }

    @Test
    public void testChange() throws Exception {
        //SignUp
        //OK_RESPONSE
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signup")
                        .header("content-type", "application/json")
                        .content(
                                "{" +
                                        "\"loginField\":\"login\", " +
                                        "\"passwordField\":\"password\"," +
                                        " \"emailField\":\"user@mail.ru\"" +
                                        "}"
                        )
        ).andExpect(status().isOk());

        final User user = userService.getUserByLogin("login");
        assertNotNull(user);

        //OK_RESPONSE
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/me")
                .sessionAttr("id", user.getId())
                .header("content-type", "application/json")
                .content(
                        "{" +
                                "\"loginField\":\"new_login\", " +
                                "\"passwordField\":\"new_password\"," +
                                " \"emailField\":\"new_email@mail.ru\"" +
                                "}"
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
                .content("")
        ).andExpect(status().isOk());

        //OK
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/users/signin")
                        .header("content-type", "application/json")
                        .content(
                                "{" +
                                        "\"loginField\":\"new_login\", " +
                                        "\"passwordField\":\"new_password\"," +
                                        " \"emailField\":\"new_email@mail.ru\"" +
                                        "}"
                        )
        ).andExpect(status().isOk());
    }
}
