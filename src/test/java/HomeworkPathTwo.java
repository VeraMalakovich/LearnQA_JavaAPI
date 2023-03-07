import com.fasterxml.jackson.annotation.JsonValue;
//import com.sun.tools.javac.util.Assert;
//import com.sun.tools.javac.util.Assert;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
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

    @Test
    public void testEx8Token() throws InterruptedException {
        //отправка запроса
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();

        response.prettyPrint();

        //запрос с token ДО того, как задача готова, убеждаемся в правильности поля status
        String responsetoken = response.body().jsonPath().getString("token");

        int responseSeconds = response.body().jsonPath().getInt("seconds");

        Map<String, String> params = new HashMap<>();
        params.put("token", responsetoken);

                 response = RestAssured
                .given()
                .queryParams(params)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();

        response.prettyPrint();

        String responseStatus = response.body().jsonPath().getString("status");
        System.out.println(responseStatus.equals("Job is NOT ready"));
        //???а как можно написать проверку, что текст в статусе ошибочный, эти Assert не работают.
        //Assert.check(true, "Job is NOT ready");
        //Assert.check(responseStatus.equals("Job is NOT ready"), "error");
        Thread.sleep(responseSeconds*1000);

        //запрос c token ПОСЛЕ того, как задача готова, проверка поля status и наличии поля result
        response = RestAssured
                .given()
                .queryParams(params)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .andReturn();

        response.prettyPrint();

        responseStatus = response.body().jsonPath().getString("status");
        System.out.println(responseStatus.equals("Job is ready"));

        String responseResult = response.body().jsonPath().getString("result");
        System.out.println(responseResult != null);
    }

    @Test
    public void testEx9Password() {

        String[] array = {
        "123456", "123456789", "qwerty", "password", "1234567", "12345678", "iloveyou", "12345", "111111", "123123",
                "abc123", "qwerty123", "1q2w3e4r", "admin", "qwertyuiop", "654321", "555555", "lovely", "7777777", "welcome",
                "888888", "princess", "dragon", "password1", "123qwe"};
        int i=0;
        while (i<array.length) {
            Map<String, String> data = new HashMap<>();

            data.put("login", "super_admin");
            data.put("password", array[i]);

            Response responseForGet = RestAssured
                    .given()
                    .body(data)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie = responseForGet.getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if(responseCookie != null) {
            cookies.put("auth_cookie", responseCookie);


            Response responseForCheck = RestAssured
                    .given()
                    .body(data)
                    .cookies(cookies)
                    .when()
                    .post("https://playground.learnqa.ru/api/check_auth_cookie")
                    .andReturn();

            responseForCheck.print();

            if (responseForCheck.asString().contains("You are authorized"))
            {
                System.out.println("correct:"+array[i]);
                break;
            }
            else
            {
                System.out.println("tried but incorrect:"+array[i]);
            }
        }
            i++;
        }
    }
}
