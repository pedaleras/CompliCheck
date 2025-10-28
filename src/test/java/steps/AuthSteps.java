package steps;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.pt.*;
import org.json.JSONObject;
import services.AuthService;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AuthSteps {
    AuthService authService = new AuthService();
    String schemasPath = "src/test/resources/schemas/";
    JSONObject jsonSchema;
    private final ObjectMapper mapper = new ObjectMapper();

    @Dado("que eu possuo dados válidos para registro")
    public void queEuPossuoDadosVálidosParaRegistro() {
        authService.setNewUser();
    }

    @Quando("eu envio uma requisição POST de usuário para {string}")
    public void euEnvioUmaRequisiçãoPOSTDeUsuárioPara(String endpoint) {
        authService.createAuthentication(endpoint);
    }


    @E("o corpo da resposta deve conter o campo {string}")
    public void oCorpoDaRespostaDeveConterOCampo(String campo) {
        Object valorCampo = authService.response.jsonPath().get(campo);
        assertThat("O campo '" + campo + "' deve estar presente na resposta", valorCampo, notNullValue());
    }

    @Dado("que eu possuo credenciais inválidas")
    public void queEuPossuoCredenciaisInválidas() {
        authService.setInvalidUser();
    }

    @Entao("o endpoint de autenticação deve retornar status {int}")
    public void oEndpointDeAutenticaçãoDeveRetornarStatus(int statusCode) {
        authService.response.then().statusCode(statusCode);
    }

    @E("o corpo da resposta do endpoint de autenticação deve conter o campo {string}")
    public void oCorpoDaRespostaDoEndpointDeAutenticaçãoDeveConterOCampo(String campo) {
        assertThat(authService.response.asString(), containsString(campo));
    }


    @Dado("que eu tenha os seguintes dados de usuário:")
    public void queEuTenhaOsSeguintesDadosDeUsuário(Map<String, String> dadosUsuario) {
        authService.createUser(dadosUsuario);
    }

    @Quando("eu enviar a requisição para o endpoint {string} de cadastro de usuário")
    public void euEnviarARequisiçãoParaOEndpointDeCadastroDeUsuário(String endpoint) {
        authService.createAuthentication(endpoint);
    }

    @Então("o status code da resposta deve ser {int}")
    public void oStatusCodeDaRespostaDeveSer(int statusCode) {
        assertThat(authService.response.statusCode(), equalTo(statusCode));
    }


    @E("que o arquivo de contrato esperado é o {string}")
    public void queOArquivoDeContratoEsperadoÉO(String contract) throws IOException {
        authService.setContract(contract);
    }

    @Então("a resposta da requisição deve estar em conformidade com o contrato selecionado")
    public void aRespostaDaRequisiçãoDeveEstarEmConformidadeComOContratoSelecionado() {
    }
}
