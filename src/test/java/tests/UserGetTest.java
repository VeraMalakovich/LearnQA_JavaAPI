package tests;

import io.qameta.allure.Epic;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
@Epic("Getting user data")
public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("Getting data for unauthorized user")
    public void testGetUserDataNotAuth(){
        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String[] unexpectedFieldNames = {"firstName", "lastName", "email"};
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFieldNames);
    }

    @Test
    @DisplayName("Getting another user`s data by an authorized user")
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .log().all()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .when()
                .get("https://playground.learnqa.ru/api/user/1")
                .andReturn();

        responseUserData.prettyPrint();
        String[] expectedFieldNames = {"username"};
        Assertions.assertJsonHasFields(responseUserData, expectedFieldNames);
    }
}
