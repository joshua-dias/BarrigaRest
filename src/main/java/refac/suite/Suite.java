package refac.suite;

import barrigarest.core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import tests.refatorado.AuthTest;
import tests.refatorado.ContasTest;
import tests.refatorado.MovimentacaoTest;
import tests.refatorado.SaldoTest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@RunWith(org.junit.runners.Suite.class)
@org.junit.runners.Suite.SuiteClasses({
        ContasTest.class,
        MovimentacaoTest.class,
        SaldoTest.class,
        AuthTest.class
})
public class Suite extends BaseTest {
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
