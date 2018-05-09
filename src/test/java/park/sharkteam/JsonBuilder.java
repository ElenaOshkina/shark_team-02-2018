package park.sharkteam;

import net.minidev.json.JSONObject;

public class JsonBuilder {

    public static String signInForm(
            String login,
            String password
    ){
        final JSONObject uncorrectJson = new JSONObject();
        uncorrectJson.put("loginField", login);
        uncorrectJson.put("passwordField", password);

        return uncorrectJson.toString();
    }

    public static String signUpForm(
            String login,
            String password,
            String email
    ){
        final JSONObject uncorrectJson = new JSONObject();
        uncorrectJson.put("loginField", login);
        uncorrectJson.put("passwordField", password);
        uncorrectJson.put("emailField", email);

        return uncorrectJson.toString();
    }

    public static String scoreForm(
            String startPos,
            String numberElements
    ){
        final JSONObject uncorrectJson = new JSONObject();
        uncorrectJson.put("startPos", startPos);
        uncorrectJson.put("numberElements", numberElements);

        return uncorrectJson.toString();
    }
}
