import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomeworkPathThree {
    @Test
    public void testEx10ShortPhrase() {
        String phrase = "Love will save the world";//28

        assertTrue(phrase.length()>15, "The phase is less 15 symbols");
    }
}
