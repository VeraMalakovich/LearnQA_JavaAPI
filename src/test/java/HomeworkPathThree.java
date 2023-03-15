import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomeworkPathThree {
    @Test
    public void testEx10ShortPhrase() {
        String phrase = "Love will save the world";//28

        assertTrue(phrase.length()>15, "The phase is less 15 symbols");
    }

    @Test
    public void testEx11Cookies() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        System.out.println("\nCookies:");
        Map<String,String> responseCookies = response.getCookies();
        System.out.println(responseCookies);
        assertEquals("{HomeWork=hw_value}", response.getCookies().toString(), "Unexpected cookie");
    }

    @Test
    public void testEx12Headers() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        System.out.println("\nHeaders:");
        Headers responseHeader = response.getHeaders();
        System.out.println(responseHeader);

        System.out.println("\nSecret header:");
        String locationHeader = response.getHeader("x-secret-homework-header");
        System.out.println(locationHeader);
        assertEquals("Some secret value", response.getHeader("x-secret-homework-header"), "Unexpected header");
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30*Mobile*No*Android",
            "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1*Mobile*Chrome*iOS",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)*Googlebot*Unknown*Unknown",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0*Web*Chrome*No",
            "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1*Mobile*No*iPhone"}, delimiter = '*')

    public void test13UserAgent(String userAgent, String platform, String browser, String device){

        Response response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .when()
                .get("https://playground.learnqa.ru/api/user_agent_check")
                .andReturn();

        assertEquals(platform, (String) response.jsonPath().get("platform"), "The platform isn`t correct for User Agent " + userAgent);
        assertEquals(browser, (String) response.jsonPath().get("browser"), "The browser isn`t correct for User Agent " + userAgent);
        assertEquals(device, (String) response.jsonPath().get("device"), "The device isn`t correct for User Agent " + userAgent);
    }
}
