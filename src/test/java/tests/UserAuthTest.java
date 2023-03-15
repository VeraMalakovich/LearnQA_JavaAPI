package tests;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lib.Assertions;
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;

    @BeforeEach
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        this.cookie = this.getCookie(responseGetAuth, "auth_sid"); //перед вставкой extends BaseTestCase, было после = responseGetAuth.getCookie("auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token"); //responseGetAuth.getHeader("x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");//responseGetAuth.jsonPath().getInt("user_id");
    }

    @Test
    public void test3AuthUserWithBeforeEach() {
        Response responseCheckAuth = RestAssured //было до импорта lib.Assertions JsonPath responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid", this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn(); //было до импорта lib.Assertions .jsonPath();

        /*int userIdOnCheck = responseCheckAuth.getInt("user_id");
        assertTrue(userIdOnCheck > 0, "Unexpected user id");

        assertEquals(
                userIdOnAuth,
                userIdOnCheck,
                "User id from auth request isn`t equal to user_id from check request"
        );*/ //было до импорта lib.Assertions
        //появилось после импорта lib.Assertions
        Assertions.asserJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void test4NegativeAuthUserWithBeforeEach(String condition){
        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if (condition.equals("cookie")) {
            spec.cookie("auth_sid", this.cookie);
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", this.header);
        } else {
            throw new IllegalArgumentException("Condition value is know" + condition);
        }

        Response responseForCheck = spec.get().andReturn();//было до импорта lib.Assertions Json responseForCheck = spec.get().jsonPath()
        //было до импорта lib.Assertions assertEquals(0, responseForCheck.getInt("user_id"), "User_id should be 0 for unauth request");
        Assertions.asserJsonByName(responseForCheck, "user_id", 0);
    }

    @Test
    public void test1AuthUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        Map<String, String> cookies = responseGetAuth.getCookies();
        Headers headers = responseGetAuth.getHeaders();
        int userIdOnAuth = responseGetAuth.jsonPath().getInt("user_id");

        assertEquals(200, responseGetAuth.statusCode(), "Unexpected status code");
        assertTrue(cookies.containsKey("auth_sid"), "Response doen`t have 'auth_sid' cookie");
        assertTrue(headers.hasHeaderWithName("x-csrf-token"), "Response doen`t have 'x-csrf-token' header");
        assertTrue(responseGetAuth.jsonPath().getInt("user_id") > 0, "User id should be greater than 0");

        JsonPath responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", responseGetAuth.getHeader("x-csrf-token"))
                .cookie("auth_sid", responseGetAuth.getCookie("auth_sid"))
                .get("https://playground.learnqa.ru/api/user/auth")
                .jsonPath();

        int userIdOnCheck = responseCheckAuth.getInt("user_id");
        assertTrue(userIdOnCheck > 0, "Unexpected user id");

        assertEquals(
                userIdOnAuth,
                userIdOnCheck,
                "User id from auth request isn`t equal to user_id from check request"
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void test2NegativeAuthUser(String condition){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        Map<String, String> cookies = responseGetAuth.getCookies();
        Headers headers = responseGetAuth.getHeaders();

        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if (condition.equals("cookie")) {
            spec.cookie("auth_sid", cookies.get("auth_sid"));
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", headers.get("x-csrf-token"));
        } else {
            throw new IllegalArgumentException("Condition value is know" + condition);
        }

        JsonPath responseForCheck = spec.get().jsonPath();
        assertEquals(0, responseForCheck.getInt("user_id"), "User_id should be 0 for unauth request");
    }
}
