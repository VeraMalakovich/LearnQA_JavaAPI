package tests;

import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.BaseTestCase;
import lib.Assertions;
import lib.ApiCoreRequests;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.ApiCoreRequests.*;

@Epic("Удаление пользователя")
public class UserDeleteTest extends BaseTestCase {

    @Test
    @DisplayName("Удалить пользователя с ID=2")
    @Description("Пытаемся удалить пользователя, у которого проставлен флаг запрета на удаление из системы")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testDeleteUser() {
        Response responseGetAuth = authRequest("vinkotov@example.com", "1234");
        Response responseDelete = deleteUserRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"), "2");

        Assertions.assertResponseTextEquals(responseDelete, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @DisplayName("Удаление созданного пользователя")
    @Description("Создали пользователя, удалили его, проверили, что пользователь действиетльно удален")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testDeleteCreatedUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = generateUserRequest(userData);
        String userId = responseCreateAuth.getString("id");
        String userEmail = userData.get("email");
        Response responseGetAuth = authRequest(userEmail, userData.get("password"));

        deleteUserRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"), userId);

        Response userDataResponse = getUserDataRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"), userId);

        Assertions.assertResponseTextEquals(userDataResponse, "User not found");
    }

    @Test
    @DisplayName("Удаление пользователя, авторизованным другим пользователем")
    @Description("Авторизоваться пользователем и попытаться удалить другого пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testDeleteAnotherUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = generateUserRequest(userData);
        String userId = responseCreateAuth.getString("id");
        String userEmail = userData.get("email");
        Response responseSecondGetAuth = authRequest("vinkotov@example.com", "1234");

        Response responseDelete = deleteUserRequest(
                this.getHeader(responseSecondGetAuth, "x-csrf-token"),
                this.getCookie(responseSecondGetAuth, "auth_sid"), userId);

        Assertions.assertResponseTextEquals(responseDelete,
                "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        Response responseGetAuth = authRequest(userEmail, userData.get("password"));

        Response userDataResponse = getUserDataRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"), userId);

        Assertions.assertJsonByName(userDataResponse, "id", userId);
        Assertions.assertJsonByName(userDataResponse, "username", userData.get("username"));
        Assertions.assertJsonByName(userDataResponse, "email", userData.get("email"));
        Assertions.assertJsonByName(userDataResponse, "firstName", userData.get("firstName"));
        Assertions.assertJsonByName(userDataResponse, "lastName", userData.get("lastName"));
    }
}
