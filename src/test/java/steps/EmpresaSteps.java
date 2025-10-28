package steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.junit.jupiter.api.Assertions;
import services.EmpresaService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

public class EmpresaSteps {
    EmpresaService empresaService = new EmpresaService();

    @Dado("que eu possuo dados válidos de uma empresa")
    public void queEuPossuoDadosVálidosDeUmaEmpresa() {
        empresaService.setEmpresaValida();
    }

    @Quando("eu envio uma requisição GET de empresa para {string}")
    public void euEnvioUmaRequisiçãoGETDeEmpresaPara(String endpoint) {
        empresaService.getEmpresas(endpoint);
    }

    @Quando("eu envio uma requisição POST de empresa para {string}")
    public void euEnvioUmaRequisiçãoPOSTDeEmpresaPara(String endpoint) {
        empresaService.createEmpresa(endpoint);
    }

    @E("o corpo da resposta deve conter uma lista de empresas")
    public void oCorpoDaRespostaDeveConterUmaListaDeEmpresas() {
        Assertions.assertTrue(empresaService.response.jsonPath().getList("$").size() >= 0);
    }

    @Entao("o endpoint de empresa deve retornar status {int}")
    public void oEndpointDeEmpresaDeveRetornarStatus(int statusCode) {
        empresaService.response.then().statusCode(statusCode);
    }


    @E("o corpo da resposta do endpoint de empresa deve conter o campo {string}")
    public void oCorpoDaRespostaDoEndpointDeEmpresaDeveConterOCampo(String campo) {
        assertThat(empresaService.response.asString(), containsString(campo));
    }
}
