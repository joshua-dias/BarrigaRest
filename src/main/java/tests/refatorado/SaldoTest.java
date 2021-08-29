package tests.refatorado;

import barrigarest.core.BaseTest;
import org.junit.Test;
import utils.BarrigaUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SaldoTest extends BaseTest {
    @Test
    public void deveCalcularSaldo(){
        Integer CONTA_ID = BarrigaUtils.getIdContaPeloNome("Conta para saldo");
                given()
                .when()
                    .get("/saldo")
                .then()
                    .statusCode(200)
                    .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("534.00"))
        ;
    }


}
