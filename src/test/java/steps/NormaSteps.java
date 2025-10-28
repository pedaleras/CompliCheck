package steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import services.NormaService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class NormaSteps {
    NormaService normaService = new NormaService();

    @Dado("que eu possuo dados incompletos de uma norma")
    public void queEuPossuoDadosIncompletosDeUmaNorma() {
        normaService.criarNormaIncompleta();
    }

    @Quando("eu envio uma requisição POST de norma para {string}")
    public void euEnvioUmaRequisiçãoPOSTDeNormaPara(String endpoint) {
        normaService.createNorma(endpoint);
    }

    @Entao("o endpoint de norma deve retornar status {int}")
    public void oEndpointDeNormaDeveRetornarStatus(int statusCode) {
        normaService.response.then().statusCode(statusCode);
    }

    @E("o corpo da resposta de norma deve conter a mensagem {string}")
    public void oCorpoDaRespostaDeNormaDeveConterAMensagem(String mensagem) {
        assertThat(normaService.response.asString(), containsString(mensagem));
    }
}
