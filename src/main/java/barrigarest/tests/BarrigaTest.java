package barrigarest.tests;

import barrigarest.core.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BarrigaTest extends BaseTest {

    private String TOKEN;

    @Before
    public void loginNaAPI(){

        //Pegando o token
        Map<String, String> login = new HashMap<>();
        login.put("email","joshuadias09@gmail.com");
        login.put("senha","123456");
        TOKEN = given()
                    .body(login)
                .when()
                    .post("/signin")
                .then()
                    .statusCode(200)
                    .extract().path("token")
                ;
    }
    @Test
    public void naoDeveAcessarAPISemToken(){
                given()
                .when()
                        .get("/contas")
                .then()
                        .statusCode(401)
                ;
    }
    @Test
    public void deveIncluirContaComSucesso(){
                 given()
                         .header("Authorization", "JWT " + TOKEN)
                         .body("{\"nome\" : \"conta qualquer\"}")
                .when()
                         .post("/contas")
                .then()
                         .statusCode(201)
        ;
    }
    @Test
    public void deveAlterarContaComSucesso(){
                given()
                    .header("Authorization", "JWT " + TOKEN)
                    .body("{\"nome\" : \"Conta Josh\"}")
                .when()
                    .put("/contas/763632")
                .then()
                    .statusCode(200)
                        .body("nome", is("Conta Josh"))
        ;
    }
    @Test
    public void naoDeveIncluirContaComNomeRepetida(){
                given()
                        .header("Authorization", "JWT " + TOKEN)
                        .body("{\"nome\" : \"Conta Josh\"}")
                .when()
                        .post("/contas")
                .then()
                        .statusCode(400)
                        .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }
    @Test
    public void deveInserirMovimentacaoComSucesso(){
        Movimentacoes mov = getMovimentacaoValida();

                given()
                        .header("Authorization", "JWT " + TOKEN)
                        .body(mov)
                .when()
                        .post("/transacoes")
                .then()
                        .statusCode(201)
        ;
    }
    @Test
    public void deveValidarCamposObrigatoriosMov(){
                given()
                        .header("Authorization", "JWT " + TOKEN)
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
    public void naoDeveCadastrarMovimentacaoFutura(){
        Movimentacoes mov = getMovimentacaoValida();
        mov.setData_transacao("20/05/2022");

                given()
                        .header("Authorization", "JWT " + TOKEN)
                        .body(mov)
                .when()
                        .post("/transacoes")
                .then()
                        .statusCode(400)
                        .body("$", hasSize(1))
                        .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"));
    }
    private Movimentacoes getMovimentacaoValida(){
        Movimentacoes mov = new Movimentacoes();
        mov.setConta_id(763632);
        mov.setDescricao("Descricao da movimentacao");
        mov.setEnvolvido("Envolvido na mov");
        mov.setTipo("REC");
        mov.setData_transacao("01/01/2000");
        mov.setData_pagamento("10/05/2010");
        mov.setValor(100f);
        mov.setStatus(true);

        return mov;
    }
}

