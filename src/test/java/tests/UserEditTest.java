package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.ApiCoreRequests.*;

@Epic("Редактирование пользователя")
public class UserEditTest extends BaseTestCase {
    @Test
    @DisplayName("Изменение данных неавторизованным пользователем")
    @Description("Неавторизованный пользователь не может отредактировать свои данные")
    @Severity(SeverityLevel.NORMAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testEditUserWithoutAuth() {
        String newName = "vera";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);

        Response responseEditUser = putEditUserRequest(
                "", "", editData, "2");
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");

        Response userDataResponse = getUserDataRequest("", "", "2");
        Assertions.assertJsonByName(userDataResponse, "username", "Vitaliy");
    }

    @Test
    @DisplayName("Редактирование данных другим пользователем")
    @Description("Авторизоваться и попытаться отредактировать данные другого пользователя")
    @Severity(SeverityLevel.NORMAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testEditUserWithAnotherUser() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        generateUserRequest(userData);

        Response responseGetAuthForEdit = authRequest("vinkotov@example.com", "1234");
        Response responseGetAuthAnother = authRequest(userData.get("email"), userData.get("password"));

        String newName = "Change name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = putEditUserRequest(
                this.getHeader(responseGetAuthAnother, "x-csrf-token"),
                this.getCookie(responseGetAuthAnother, "auth_sid"),
                editData, "2");
        responseEditUser.prettyPrint();

        Response userDataResponse = getUserDataRequest(
                this.getHeader(responseGetAuthForEdit,"x-csrf-token"),
                this.getCookie(responseGetAuthForEdit, "auth_sid"), "2");
        Assertions.assertJsonByName(userDataResponse, "firstName", "Vitalii");
    }

    @Test
    @DisplayName("Попытка изменить email у которого нет @")
    @Description("Авторизованный пользователь пытается сохранить email без @")
    @Severity(SeverityLevel.NORMAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testEditWithWrongEmail() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = generateUserRequest(userData);
        String userId = responseCreateAuth.getString("id");
        String userEmail = userData.get("email");

        Response responseGetAuth = authRequest(userEmail, userData.get("password"));
        Map<String, String> editData = new HashMap<>();
        editData.put("email", "vera.test.ru");

        Response responseEditUser = putEditUserRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData, userId);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");

        Response userDataResponse = getUserDataRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                userId);
        Assertions.assertJsonByName(userDataResponse, "email", userEmail);
    }

    @Test
    @DisplayName("Сохранить имя пользователя с одним символом")
    @Description("Пытаемся сохранить firstName только с одним символом V")
    @Severity(SeverityLevel.NORMAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testEditFirstNameTooShort() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = generateUserRequest(userData);
        String userId = responseCreateAuth.getString("id");
        Response responseGetAuth = authRequest(userData.get("email"), userData.get("password"));

        String newName = "V";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = putEditUserRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                editData, userId);
        Assertions.assertJsonByName(responseEditUser, "error", "Too short value for field firstName");

        Response responseUserData = getUserDataRequest(
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid"),
                userId);
        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    @DisplayName("Редактирование только что созданного пользователя")
    @Description("Создаем пользователя, а потом его же и редактируем")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testEditJustCreatedTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }
}
