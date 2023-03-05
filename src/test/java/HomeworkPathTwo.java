import com.fasterxml.jackson.annotation.JsonValue;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;

public class HomeworkPathTwo {
    @Test
    public void testEx5JsonPath() {
        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        Object answer = response.get("messages[1]");
        System.out.println(answer);
    }

    @Test
    public void testEx6Redirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        response.prettyPrint();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void testEx7LongRedirect() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int code = 0;
        while(code!=200){

            Response response = RestAssured
                        .given()
                        .redirects()
                        .follow(false)
                        .when()
                        .get(url)
                        .andReturn();

                response.prettyPrint();

                int statusCode = response.getStatusCode();
                System.out.println(statusCode);
                code = statusCode;

                String locationHeader = response.getHeader("Location");
                System.out.println(locationHeader);
                url = locationHeader;
        }
    }
}
