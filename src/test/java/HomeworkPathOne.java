import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
public class HomeworkPathOne {
    @Test
    public void testEx3Hello(){
        System.out.println("Hello from Vera");
    }

    @Test
    public void testEx4Get(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }
}
