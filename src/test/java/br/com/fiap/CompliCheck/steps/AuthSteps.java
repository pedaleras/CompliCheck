package br.com.fiap.CompliCheck.steps;
import br.com.fiap.CompliCheck.model.Usuario;
import br.com.fiap.CompliCheck.model.UsuarioRole;
import br.com.fiap.CompliCheck.repository.UsuarioRepository;
import br.com.fiap.CompliCheck.util.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;



@Component

public class AuthSteps extends CommonApiSteps {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Before
    public void setupAuthUser() {

        String existingUserEmail = "usuario@teste.com";
        if (usuarioRepository.findByEmail(existingUserEmail).isEnabled()) { // Usar .isEmpty() se findByEmail retornar Optional
            Usuario usuario = new Usuario();
            usuario.setNome("Usuario Login Teste");
            usuario.setEmail(existingUserEmail);
            usuario.setSenha(new BCryptPasswordEncoder().encode("senha123")); // Criptografa a senha
            usuario.setRole(UsuarioRole.USER);
            usuarioRepository.save(usuario);
        }
    }

    @After // Hook do Cucumber que roda depois de CADA cenário
    public void cleanupAuthUser() {
        // Limpa o usuário criado dinamicamente após cada cenário, se houver
        String currentTestEmail = TestContext.get("currentTestEmail", String.class);
        if (currentTestEmail != null) {
            usuarioRepository.findByEmail(currentTestEmail).isCredentialsNonExpired();
            TestContext.set("currentTestEmail", null); // Limpa do contexto após deletar
        }
    }


    @Given("que um usuário ADMIN está autenticado")
    public void queUmUsuarioAdminEstaAutenticado() {

        TestContext.set("authToken", "Bearer DUMMY_ADMIN_TOKEN_FOR_TESTING");

    }

    @Given("que eu tenho as credenciais válidas:")
    public void queEuTenhoAsCredenciaisValidas(List<Map<String, String>> credentials) {
        // Este passo é descritivo. O usuário "usuario@teste.com" é criado no método @Before.
        // Os dados válidos serão usados no passo 'When'.
    }

    @Given("que eu tenho as credenciais inválidas:")
    public void queEuTenhoAsCredenciaisInvalidas(List<Map<String, String>> credentials) {
        // Este passo é descritivo. Os dados inválidos serão usados no passo 'When'.
    }

    @Given("que eu envio um JSON sem o campo {string}")
    public void queEuEnvioUmJsonSemOCampo(String campoAusente) {
        // Este passo é descritivo, o JSON sem o campo será passado no passo 'When'.
    }

    @Given("que desejo registrar um novo usuário com os seguintes dados:")
    public void queDesejoRegistrarUmNovoUsuarioComOsSeguintesDados(List<Map<String, String>> userData) {
        Map<String, String> data = userData.get(0);
        // Gera um email dinâmico para evitar conflitos de UNIQUE constraint
        String originalEmail = data.get("email");
        String dynamicEmail = originalEmail.replace("@", "_" + ThreadLocalRandom.current().nextInt(100000) + "@");
        TestContext.set("currentTestEmail", dynamicEmail); // Armazena o email dinâmico no TestContext

        // Usando Text Blocks para formatar o JSON
        String requestBody = String.format("""
            {
              "nome": "%s",
              "email": "%s",
              "senha": "%s",
              "role": "%s"
            }
            """,
                data.get("nome"), dynamicEmail, data.get("senha"), data.get("role"));
        TestContext.set("requestBody", requestBody); // Armazena o JSON no TestContext para o próximo passo 'When'
    }

    @When("eu faço uma requisição POST para o endpoint {string} de autenticação")
    public void euFacoUmaRequisicaoPostParaOEndpointDeAutenticacao(String endpoint) {
        String requestBody = TestContext.get("requestBody", String.class);
        if (requestBody == null) {
            throw new IllegalStateException("RequestBody não foi definido no contexto de teste para esta requisição.");
        }
        // Usa o método herdado do CommonApiSteps
        super.euFacoUmaRequisicaoParaOEndpointComOSeguinteJSON("POST", endpoint, requestBody);
    }

    @And("o corpo da resposta deve conter o campo {string} com valor não nulo")
    public void oCorpoDaRespostaDeveConterOCampoComValorNaoNulo(String fieldName) {
        super.oCorpoDaRespostaDeveConter(fieldName); // Reutiliza o método do CommonApiSteps
    }

    @And("o email retornado deve ser o email dinâmico")
    public void oEmailRetornadoDeveSerODinamico() {
        String expectedEmail = TestContext.get("currentTestEmail", String.class);
        super.oCorpoDaRespostaDeveConterValorString("email", expectedEmail);
    }

    @Given("que desejo registrar um novo usuário sem informar o campo {string}")
    public void queDesejoRegistrarUmNovoUsuarioSemInformarOCampo(String campoAusente) {
    }
}