package barrigarest.tests;

import barrigarest.core.BaseTest;
import barrigarest.tests.utils.DataUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

    private static String CONTA_NAME = "Conta " + System.nanoTime();
    private static Integer CONTA_ID;
    private static Integer MOV_ID;

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
    }
    @Test
    public void t03_deveIncluirContaComSucesso(){
                 CONTA_ID = given()
                         .body("{\"nome\" : \""+CONTA_NAME+"\"}")
                .when()
                         .post("/contas")
                .then()
                         .statusCode(201)
                         .extract().path("id")
        ;
    }
    @Test
    public void t04_deveAlterarContaComSucesso(){
                given()
                        .body("{\"nome\" : \""+CONTA_NAME+" alterada\"}")
                        .pathParam("id", CONTA_ID)
                .when()
                        .put("/contas/{id}")
                .then()
                        .statusCode(200)
                        .body("nome", is(CONTA_NAME+" alterada"))
        ;
    }
    @Test
    public void t05_naoDeveIncluirContaComNomeRepetida(){
                given()
                        .body("{\"nome\" : \""+CONTA_NAME+" alterada\"}")
                .when()
                        .post("/contas")
                .then()
                        .statusCode(400)
                        .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }
    @Test
    public void t06_deveInserirMovimentacaoComSucesso(){
        Movimentacoes mov = getMovimentacaoValida();

                MOV_ID = given()
                        .body(mov)
                .when()
                        .post("/transacoes")
                .then()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }
    @Test
    public void t07_deveValidarCamposObrigatoriosMov(){
                given()
                        .body("{}")
                .when()
                        .post("/transacoes")
                .then()
                        .statusCode(400)
                        .body("$", hasSize(8))
                        .body("msg", hasItems(
                                "Data da Movimentação é obrigatório",
                                "Data do pagamento é obrigatório",
                                "Descrição é obrigatório",
                                "Interessado é obrigatório",
                                "Valor é obrigatório",
                                "Valor deve ser um número",
                                "Conta é obrigatório",
                                "Situação é obrigatório"
                        ))
        ;
    }
    @Test
    public void t08_naoDeveCadastrarMovimentacaoFutura(){
        Movimentacoes mov = getMovimentacaoValida();
        mov.setData_transacao(DataUtils.getDataDeferencaDias(2));

                given()
                        .body(mov)
                .when()
                        .post("/transacoes")
                .then()
                        .statusCode(400)
                        .body("$", hasSize(1))
                        .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"));
    }
    @Test
    public void t09_naoDeveRemoverContaComMovimentacao(){
                given()
                        .pathParam("id", CONTA_ID)
                .when()
                        .delete("/contas/{id}")
                .then()
                        .statusCode(500)
                        .body("constraint", is("transacoes_conta_id_foreign"))
                ;
    }
    @Test
    public void t10_deveCalcularSaldo(){
                 given()
                .when()
                        .get("/saldo")
                .then()
                        .statusCode(200)
                        .body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"))
        ;
    }
    @Test
    public void t11_removerMovimentacao(){
                given()
                        .pathParam("id", MOV_ID)
                .when()
                        .delete("/transacoes/{id}")
                .then()
                        .statusCode(204)
        ;
    }
    @Test
    public void t12_naoDeveAcessarAPISemToken(){
        FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
        req.removeHeader("Authorization");
        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401)
        ;
    }

    private Movimentacoes getMovimentacaoValida(){
        Movimentacoes mov = new Movimentacoes();
        mov.setConta_id(CONTA_ID);
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao(DataUtils.getDataDeferencaDias(-1));
        mov.setData_pagamento(DataUtils.getDataDeferencaDias(5));
        mov.setValor(100f);
        mov.setStatus(true);

        return mov;
    }
}

