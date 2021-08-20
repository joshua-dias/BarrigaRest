package barrigarest.tests;

import barrigarest.core.BaseTest;
import org.junit.Test;

import static io.restassured.RestAssured.given;

public class BarrigaTest extends BaseTest {
    @Test
    public void naoDeveAcessarAPISemToken(){
                given()
                .when()
                .then()
                ;
    }
}
