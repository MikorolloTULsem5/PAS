package integrationtests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTests {

    @Test
    public void getAllClientsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
//        request.relaxedHTTPSValidation();
        Response response = request.get(new URI("http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/users"));
        String responseString = response.asString();

        System.out.println(responseString);

//        assertTrue(responseString.contains("\"login\":\"aAdamski\""));
    }
}
