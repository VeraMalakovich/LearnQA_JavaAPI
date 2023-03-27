package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Link;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Severity;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import lib.ApiCoreRequests;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


@Epic("Регистрация")
public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("Регистрация с некорректным email")
    @Description("Потаемся зарегистрировать пользователя у которого пропущен @ в email")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testCreateUserWithIncorrectEmail() {
        String email = "example.test.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "email", "password", "firstName", "lastName"})
    @DisplayName("Регистрация без одного из обязательных параметров")
    @Description("Пытаемся зарегистрировать пользователя без одного из полей \"username\", \"email\", \"password\", \"firstName\", \"lastName\"")
    @Severity(SeverityLevel.BLOCKER)
    @Link("https://playground.learnqa.ru/api/map")
    public void testCreateUserWithoutOneOfParameters(String condition) {
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);

        if (condition.equals("username") || condition.equals("email") ||
                condition.equals("password") || condition.equals("firstName") ||
                condition.equals("lastName")) {
            userData.remove(condition);
        } else {
            throw new IllegalArgumentException("Condition value is unknow: " + condition);
        }

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + condition);
    }

    @Test
    @DisplayName("Регистрация пользователя с коротким именем")
    @Description("Пытаемся регистрировать пользователя с именем в один символ")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testCreateUserWithOneSymbolName(){
        String username = "V";
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    @Test
    @DisplayName("Регистрация пользователя с длинным именем")
    @Description("Пытаемся зарегистрировать пользователя с именем в 260 символов")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://playground.learnqa.ru/api/map")
    public void testCreateUserWithLongName(){
        String username = "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" +
                "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" +
                "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv234567890";
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }

    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData); //данные нужны

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully() {
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData(); //данные дефолтные

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

}
