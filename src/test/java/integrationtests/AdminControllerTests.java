package integrationtests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static integrationtests.NewCleaningClassForTests.admin1;
import static integrationtests.NewCleaningClassForTests.admin2;
import static integrationtests.NewCleaningClassForTests.cleanUsers;
import static integrationtests.NewCleaningClassForTests.initAdmins;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdminControllerTests {

    static final String appUrlAdmins = "http://localhost:8080/CourtRent-1.0-SNAPSHOT/api/admins";

    @BeforeAll
    static void init() throws URISyntaxException  {
        RestAssured.given().get(new URI(appUrlAdmins));
    }

    @AfterAll
    static void cleanAtTheEnd() {
        cleanUsers();
    }

    @BeforeEach
    void cleanAndInitDatabase() {
        cleanUsers();
        initAdmins();
    }

    @Test
    void getAllAdminsTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins));
        String responseString = response.asString();

        String[] splitedRespStr = responseString.split("},");

        assertEquals(2, splitedRespStr.length);

        //First Admin
        assertTrue(splitedRespStr[0].contains("\"archive\":false"));
        assertTrue(splitedRespStr[0].contains("\"id\":\"%s\"".formatted(admin1.getId())));
        assertTrue(splitedRespStr[0].contains("\"login\":\"adminek1@1234\""));

        //Second Admin
        assertTrue(splitedRespStr[1].contains("\"archive\":false"));
        assertTrue(splitedRespStr[1].contains("\"id\":\"%s\"".formatted(admin2.getId())));
        assertTrue(splitedRespStr[1].contains("\"login\":\"adminek2@9876\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAllAdminsTestNoCont() throws URISyntaxException {
        cleanUsers();
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }
/*
    @Test
    void createAdminTestPos() throws URISyntaxException {
        cleanUsers();
        String JSON = """
                {
                  "firstName": "John",
                  "lastName": "Bravo",
                  "login": "johnBravo",
                  "AdminTypeName": "normal"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.isEmpty());

        Response responsePost = requestPost.post(appUrlAdmins + "/addAdmin");

        assertEquals(201, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"johnBravo\""));
        assertTrue(responseString.contains("\"AdminTypeName\":\"normal\""));
        assertTrue(responseString.contains("\"firstName\":\"John\""));
        assertTrue(responseString.contains("\"lastName\":\"Bravo\""));
    }

    @Test
    void createAdminTestNegInvalidData() throws URISyntaxException {
        String json = """
                {
                  "firstName": "John",
                  "lastName": "  ",
                  "login": "johnBravo",
                  "AdminTypeName": "normal"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertFalse(responseString.contains("\"login\":\"johnBravo\""));
        assertFalse(responseString.contains("\"lastName\":\"  \""));

        Response responsePost = requestPost.post(appUrlAdmins + "/addAdmin");

        assertEquals(400, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertFalse(responseString.contains("\"login\":\"johnBravo\""));
        assertFalse(responseString.contains("\"lastName\":\"  \""));
    }

    @Test
    void createAdminTestNegSameLogin() throws URISyntaxException {
        String json = """
                {
                  "firstName": "John",
                  "lastName": "Bravo",
                  "login": "michas13",
                  "AdminTypeName": "normal"
                }
                """;
        RequestSpecification requestPost = RestAssured.given();
        requestPost.contentType("application/json");
        requestPost.body(json);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"michas13\""));

        assertFalse(responseString.contains("\"firstName\":\"John\""));
        assertFalse(responseString.contains("\"lastName\":\"Bravo\""));

        Response responsePost = requestPost.post(appUrlAdmins + "/addAdmin");

        assertEquals(409, responsePost.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertFalse(responseString.contains("\"firstName\":\"John\""));
        assertFalse(responseString.contains("\"lastName\":\"Bravo\""));
    }

    @Test
    void getAdminByLoginTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins + "/get?login=michas13"));
        String responseString = response.asString();

        assertTrue(responseString.contains("\"login\":\"michas13\",\"AdminTypeName\":\"coach\",\"firstName\":\"Michal\",\"lastName\":\"Pi\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAdminByLoginTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins + "/get?login=564545415612121121"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getAdminByIdTest() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();

        //Retrieve UUID
        String responseLogin = request.get(new URI(appUrlAdmins + "/get?login=michas13")).asString();
        int index = responseLogin.indexOf("\"id\":\"") + 6;
        String AdminId = responseLogin.substring(index, index + 36);

        Response responseById = request.get(new URI(appUrlAdmins + "/" + AdminId));
        String responseByIdString = responseById.asString();

        assertTrue(responseByIdString.contains("\"login\":\"michas13\",\"AdminTypeName\":\"coach\",\"firstName\":\"Michal\",\"lastName\":\"Pi\""));

        assertEquals(200, responseById.getStatusCode());
    }

    @Test
    void getAdminByIdTestNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins + "/" + UUID.randomUUID()));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());
        assertEquals(204, response.getStatusCode());
    }

    @Test
    void getAdminByLoginMatchingPos() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins + "/match?login=login"));
        String responseString = response.asString();

        String[] splitedRespStr = responseString.split("},");

        assertEquals(2, splitedRespStr.length);

        //First Admin
        assertTrue(splitedRespStr[0].contains(
                "\"login\":\"loginek\"," +
                "\"AdminTypeName\":\"normal\"," +
                "\"firstName\":\"Adam\"," +
                "\"lastName\":\"Smith\""));

        //Second Admin
        assertTrue(splitedRespStr[1].contains(
                "\"login\":\"loginek13\"," +
                "\"AdminTypeName\":\"athlete\"," +
                "\"firstName\":\"Eva\"," +
                "\"lastName\":\"Braun\""));

        assertEquals(200, response.getStatusCode());
    }

    @Test
    void getAdminByLoginMatchingNoCont() throws URISyntaxException {
        RequestSpecification request = RestAssured.given();
        Response response = request.get(new URI(appUrlAdmins + "/match?login=uwuwuuwuwu"));
        String responseString = response.asString();

        assertTrue(responseString.isEmpty());

        assertEquals(204, response.getStatusCode());
    }

    @Test
    void modifyAdminTest() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "firstName": "John",
                  "lastName": "Smith",
                  "login": "loginek",
                  "AdminTypeName": "coach"
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        //Retrieve UUID
        String responseLogin = requestGet.get(new URI(appUrlAdmins + "/get?login=loginek")).asString();
        int index = responseLogin.indexOf("\"id\":\"") + 6;
        String AdminId = responseLogin.substring(index, index + 36);

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + AdminId + "\"," +
                "\"login\":\"loginek\"," +
                "\"AdminTypeName\":\"normal\"," +
                "\"firstName\":\"Adam\"," +
                "\"lastName\":\"Smith\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + AdminId + "\"," +
                "\"login\":\"loginek\"," +
                "\"AdminTypeName\":\"coach\"," +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Smith\""));

        Response responsePut = requestPut.put(appUrlAdmins + "/modifyAdmin/" + AdminId);

        assertEquals(204, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertFalse(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + AdminId + "\"," +
                "\"login\":\"loginek\"," +
                "\"AdminTypeName\":\"normal\"," +
                "\"firstName\":\"Adam\"," +
                "\"lastName\":\"Smith\""));
        assertTrue(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + AdminId + "\"," +
                "\"login\":\"loginek\"," +
                "\"AdminTypeName\":\"coach\"," +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Smith\""));
    }

    @Test
    void modifyAdminTestNegInvalidData() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "firstName": "   ",
                  "lastName": "Smith",
                  "login": "loginek",
                  "AdminTypeName": "coach"
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        //Retrieve UUID
        String responseLogin = requestGet.get(new URI(appUrlAdmins + "/get?login=loginek")).asString();
        int index = responseLogin.indexOf("\"id\":\"") + 6;
        String AdminId = responseLogin.substring(index, index + 36);

        assertTrue(responseString.contains(
                "\"archive\":false," +
                        "\"id\":\"" + AdminId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"AdminTypeName\":\"normal\"," +
                        "\"firstName\":\"Adam\"," +
                        "\"lastName\":\"Smith\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                        "\"id\":\"" + AdminId + "\"," +
                        "\"login\":\"loginek\"," +
                        "\"AdminTypeName\":\"coach\"," +
                        "\"firstName\":\"John\"," +
                        "\"lastName\":\"Smith\""));

        Response responsePut = requestPut.put(appUrlAdmins + "/modifyAdmin/" + AdminId);

        assertEquals(400, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + AdminId + "\"," +
                "\"login\":\"loginek\"," +
                "\"AdminTypeName\":\"normal\"," +
                "\"firstName\":\"Adam\"," +
                "\"lastName\":\"Smith\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + AdminId + "\"," +
                "\"login\":\"loginek\"," +
                "\"AdminTypeName\":\"coach\"," +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Smith\""));
    }

    @Test
    void modifyAdminTestNegRepeatLoginOfAnotherAdmin() throws URISyntaxException {
        String JSON = """
                {
                  "archive": true,
                  "firstName": "John",
                  "lastName": "Smith",
                  "login": "michas13",
                  "AdminTypeName": "coach"
                }
                """;
        RequestSpecification requestPut = RestAssured.given();
        requestPut.contentType("application/json");
        requestPut.body(JSON);

        RequestSpecification requestGet = RestAssured.given();
        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        //Retrieve UUID
        String responseLogin = requestGet.get(new URI(appUrlAdmins + "/get?login=loginek")).asString();
        int index = responseLogin.indexOf("\"id\":\"") + 6;
        String AdminId = responseLogin.substring(index, index + 36);

        assertTrue(responseString.contains("\"login\":\"michas13\""));

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + AdminId + "\"," +
                "\"login\":\"loginek\"," +
                "\"AdminTypeName\":\"normal\"," +
                "\"firstName\":\"Adam\"," +
                "\"lastName\":\"Smith\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + AdminId + "\"," +
                "\"login\":\"michas13\"," +
                "\"AdminTypeName\":\"coach\"," +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Smith\""));

        Response responsePut = requestPut.put(appUrlAdmins + "/modifyAdmin/" + AdminId);

        assertEquals(409, responsePut.getStatusCode());

        responseString = requestGet.get(new URI(appUrlAdmins)).asString();

        assertTrue(responseString.contains("\"login\":\"michas13\""));

        assertTrue(responseString.contains(
                "\"archive\":false," +
                "\"id\":\"" + AdminId + "\"," +
                "\"login\":\"loginek\"," +
                "\"AdminTypeName\":\"normal\"," +
                "\"firstName\":\"Adam\"," +
                "\"lastName\":\"Smith\""));
        assertFalse(responseString.contains(
                "\"archive\":true," +
                "\"id\":\"" + AdminId + "\"," +
                "\"login\":\"michas13\"," +
                "\"AdminTypeName\":\"coach\"," +
                "\"firstName\":\"John\"," +
                "\"lastName\":\"Smith\""));
    }
*/
//    @Test
//    void archiveAndActivateAdminTest() throws URISyntaxException {
//        RequestSpecification requestGet = RestAssured.given();
//        String responseString = requestGet.get(new URI(appUrlAdmins)).asString();
//
//        //Retrieve UUID
//        String responseLogin = requestGet.get(new URI(appUrlAdmins + "/get?login=loginek")).asString();
//        int index = responseLogin.indexOf("\"id\":\"") + 6;
//        String AdminId = responseLogin.substring(index, index + 36);
//
//        /*Archive test*/
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + AdminId + "\""));
//        assertFalse(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + AdminId + "\""));
//
//        RequestSpecification requestPost = RestAssured.given();
//        Response responsePost = requestPost.post(appUrlAdmins + "/deactivate/" + AdminId);
//
//        assertEquals(204, responsePost.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlAdmins)).asString();
//
//        assertFalse(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + AdminId + "\""));
//        assertTrue(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + AdminId + "\""));
//
//        /*Activate test*/
//        RequestSpecification requestPost2 = RestAssured.given();
//        Response responsePost2 = requestPost2.post(appUrlAdmins + "/activate/" + AdminId);
//
//        assertEquals(204, responsePost2.getStatusCode());
//
//        responseString = requestGet.get(new URI(appUrlAdmins)).asString();
//
//        assertTrue(responseString.contains(
//                "\"archive\":false," +
//                "\"id\":\"" + AdminId + "\""));
//        assertFalse(responseString.contains(
//                "\"archive\":true," +
//                "\"id\":\"" + AdminId + "\""));
//    }

}
