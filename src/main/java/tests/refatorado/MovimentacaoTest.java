package tests.refatorado;

import barrigarest.core.BaseTest;
import barrigarest.tests.Movimentacoes;
import utils.BarrigaUtils;
import utils.DataUtils;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MovimentacaoTest extends BaseTest {

    @Test
    public void deveInserirMovimentacaoComSucesso(){
        Movimentacoes mov = getMovimentacaoValida();

                given()
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
    public void naoDeveRemoverContaComMovimentacao(){
                given()
                    .pathParam("id", BarrigaUtils.getIdContaPeloNome("Conta com movimentacao"))
                .when()
                    .delete("/contas/{id}")
                .then()
                    .statusCode(500)
                    .body("constraint", is("transacoes_conta_id_foreign"))
        ;
    }
    @Test
    public void removerMovimentacao(){
                given()
                    .pathParam("id", BarrigaUtils.getIdMovPelaDescricao("Movimentacao para exclusao"))
                .when()
                    .delete("/transacoes/{id}")
                .then()
                    .statusCode(204)
        ;
    }


    private Movimentacoes getMovimentacaoValida(){
        Movimentacoes mov = new Movimentacoes();
        mov.setConta_id(BarrigaUtils.getIdContaPeloNome("Conta para movimentacoes"));
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
