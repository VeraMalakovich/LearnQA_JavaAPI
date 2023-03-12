import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

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
}
