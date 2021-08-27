package tests.refatorado;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ContasTest {
    @BeforeClass
    public static void loginNaAPI(){

        //Pegando o token
        Map<String, String> login = new HashMap<>();
        login.put("email","joshuadias09@gmail.com");
        login.put("senha","123456");
        String TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token")
                ;

        RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);

        RestAssured.get("/reset").then().statusCode(200);
    }
}
