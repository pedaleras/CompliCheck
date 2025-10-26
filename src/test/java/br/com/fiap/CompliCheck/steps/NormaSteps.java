package br.com.fiap.CompliCheck.steps;

import br.com.fiap.CompliCheck.model.Empresa;
import br.com.fiap.CompliCheck.model.Norma;
import br.com.fiap.CompliCheck.repository.EmpresaRepository;
import br.com.fiap.CompliCheck.repository.NormaRepository;
import br.com.fiap.CompliCheck.util.TestContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
/**
 * Definições de passos para gerenciar a entidade Norma.
 * Estende CommonApiSteps para utilizar funcionalidades de API genéricas e o TestContext compartilhado.
 */
public class NormaSteps extends CommonApiSteps {

    @Autowired
    private NormaRepository normaRepository;
    @Autowired
    private EmpresaRepository empresaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper(); // Reutiliza o ObjectMapper


    /**
     * Cria uma norma diretamente no banco de dados para pré-condicionar um cenário.
     * Associa a norma a uma empresa existente, cujo ID foi armazenado no TestContext pelo EmpresaSteps.
     * Exemplo: "que uma norma foi cadastrada com ID 200 e descrição "Norma para Listagem" e categoria "Legal" e dataLimite "2025-12-31" e empresaId 1"
     *
     * @param logicalNormaId Um ID lógico usado no feature file para referenciar esta norma no TestContext.
     * @param descricao A descrição da norma.
     * @param categoria A categoria da norma.
     * @param dataLimiteStr A data limite da norma (string no formato "yyyy-MM-dd").
     * @param logicalEmpresaId O ID lógico da empresa à qual a norma será associada.
     */
    @Given("que uma norma foi cadastrada com ID {long} e descrição {string} e categoria {string} e dataLimite {string} e empresaId {long}")
    public void queUmaNormaFoiCadastradaComIDDescricaoCategoriaDataLimiteEEmpresaId(
            Long logicalNormaId, String descricao, String categoria, String dataLimiteStr, Long logicalEmpresaId) {

        // 1. Recupera o ID real da empresa do TestContext
        Long empresaId = TestContext.get("empresaId_" + logicalEmpresaId, Long.class);
        if (empresaId == null) {
            throw new IllegalStateException("Empresa com logicalId " + logicalEmpresaId + " não foi encontrada no TestContext. " +
                    "Certifique-se de que o passo 'que existe uma empresa com ID ...' foi executado no Background.");
        }
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalStateException("Empresa real com ID " + empresaId + " não encontrada no banco de dados."));

        // 2. Cria e salva a Norma
        Norma norma = new Norma();
        // Não é recomendado definir o ID manualmente se o banco usa estratégia IDENTITY.
        // O ID real gerado pelo banco será o que usaremos.
        norma.setDescricao(descricao);
        norma.setCategoria(categoria);
        norma.setDataLimite(LocalDate.parse(dataLimiteStr));
        norma.setEmpresa(empresa);

        Norma savedNorma = normaRepository.save(norma);
        assertNotNull(savedNorma.getId(), "Norma deveria ter um ID após ser salva.");

        // 3. Armazena o ID real gerado pelo banco para o ID lógico do teste no TestContext
        TestContext.set("normaId_" + logicalNormaId, savedNorma.getId());
    }

    /**
     * Versão simplificada do passo acima, para quando a categoria e data limite não importam para o teste.
     * Exemplo: "que existe uma norma com ID 203 e descrição "Norma para Exclusão" e empresaId 1"
     */
    @Given("que existe uma norma com ID {long} e descrição {string} e empresaId {long}")
    public void queUmaNormaFoiCadastradaComDescricaoEEmpresaId(Long logicalNormaId, String descricao, Long logicalEmpresaId) {
        // 1. Recupera o ID real da empresa do TestContext
        Long empresaId = TestContext.get("empresaId_" + logicalEmpresaId, Long.class);
        if (empresaId == null) {
            throw new IllegalStateException("Empresa com logicalId " + logicalEmpresaId + " não foi encontrada no TestContext.");
        }
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalStateException("Empresa real com ID " + empresaId + " não encontrada no banco de dados."));

        // 2. Cria e salva a Norma com valores padrão para categoria e data
        Norma norma = new Norma();
        norma.setDescricao(descricao);
        norma.setCategoria("Default"); // Valor padrão
        norma.setDataLimite(LocalDate.now().plusMonths(6)); // Data padrão
        norma.setEmpresa(empresa);

        Norma savedNorma = normaRepository.save(norma);
        assertNotNull(savedNorma.getId(), "Norma deveria ter um ID após ser salva.");

        // 3. Armazena o ID real gerado pelo banco para o ID lógico do teste no TestContext
        TestContext.set("normaId_" + logicalNormaId, savedNorma.getId());
    }

    @Given("que desejo cadastrar uma nova norma com os seguintes dados:")
    public void queDesejoCadastrarUmaNovaNormaComOsSeguintesDados(DataTable dataTable) {
        // Este passo é descritivo. O JSON será construído no 'When'.
    }

    @Given("que desejo cadastrar uma nova norma sem o campo {string} com os seguintes dados:")
    public void queDesejoCadastrarUmaNovaNormaSemOCampoComOsSeguintesDados(String campoAusente, DataTable dataTable) {
        // Este passo é descritivo. O JSON será construído no 'When'.
    }

    @Given("que não existe uma norma com ID {long}")
    public void queNaoExisteUmaNormaComID(Long idInexistente) {
        // Este passo é apenas descritivo. O TestContext.clear() em CucumberSpringConfiguration
        // garante que não haverá dados preexistentes, a menos que sejam criados por outro @Given.
        // Podemos armazenar este ID no TestContext para referência, se necessário, mas para este caso,
        // o endpoint com este ID simplesmente não encontrará nada.
        TestContext.set("idNormaInexistente", idInexistente);
    }

    // --- Steps de Ação (When) ---

    @When("eu faço uma requisição POST para o endpoint {string} com o seguinte JSON para norma:")
    public void euFacoUmaRequisicaoPOSTParaOEndpointComOSeguinteJSONParaNorma(String endpoint, String requestBody) throws IOException {
        ObjectNode rootNode = (ObjectNode) objectMapper.readTree(requestBody);

        // O JSON para norma pode conter placeholders para o ID da empresa (ex: $empresaId_1)
        // A lógica de resolvePlaceholders do CommonApiSteps cuidará disso automaticamente.

        String finalJsonBody = objectMapper.writeValueAsString(rootNode); // Converte o JSON modificado de volta para string

        super.euFacoUmaRequisicaoParaOEndpointComOSeguinteJSON("POST", endpoint, finalJsonBody);
    }

    @When("eu faço uma requisição GET para o endpoint {string} de normas")
    public void euFacoUmaRequisicaoGETParaOEndpointDeNormas(String endpoint) {
        super.euFacoUmaRequisicaoParaOEndpoint("GET", endpoint);
    }

    @When("eu faço uma requisição GET para o endpoint {string} com ID {long} de normas")
    public void euFacoUmaRequisicaoGETParaOEndpointComIDDeNormas(String endpointPrefix, Long logicalNormaId) {
        // Recupera o ID real da norma do TestContext
        Long normaId = TestContext.get("normaId_" + logicalNormaId, Long.class);
        if (normaId == null) {
            throw new IllegalStateException("ID da norma com logicalId " + logicalNormaId + " não encontrado no TestContext.");
        }
        // Usa o metodo herdado do CommonApiSteps com o endpoint resolvido
        super.euFacoUmaRequisicaoParaOEndpoint("GET", endpointPrefix + normaId);
    }

    @When("eu faço uma requisição PUT para o endpoint {string} com o seguinte JSON de atualização de norma:")
    public void euFacoUmaRequisicaoPUTParaOEndpointComOSeguinteJSONDeAtualizacaoDeNorma(String endpoint, String requestBody) throws IOException {
        ObjectNode rootNode = (ObjectNode) objectMapper.readTree(requestBody);

        // A lógica para incluir o ID da norma a ser atualizada no JSON já é gerenciada por resolvePlaceholders
        // se o feature file usar um placeholder como "$normaId_..." no JSON ou no endpoint.
        // Ex: { "id": $normaId_202, "descricao": "..." }

        String finalJsonBody = objectMapper.writeValueAsString(rootNode);

        // Usa o metodo herdado do CommonApiSteps para fazer a requisição
        super.euFacoUmaRequisicaoParaOEndpointComOSeguinteJSON("PUT", endpoint, finalJsonBody);
    }

    @When("eu faço uma requisição DELETE para o endpoint {string} com ID {long} de normas")
    public void euFacoUmaRequisicaoDELETEParaOEndpointComIDDeNormas(String endpointPrefix, Long logicalNormaId) {
        Long normaId = TestContext.get("normaId_" + logicalNormaId, Long.class);
        if (normaId == null) {
            // Caso o logicalNormaId não tenha sido salvo no contexto (ex: para testar ID inexistente)
            // Usa o ID diretamente do feature file.
            normaId = logicalNormaId;
            System.out.println("WARN: ID da norma com logicalId " + logicalNormaId + " não encontrado no TestContext. Usando o ID literal do feature file para DELETE.");
        }
        super.euFacoUmaRequisicaoParaOEndpoint("DELETE", endpointPrefix + normaId);
    }

    // --- Steps de Verificação (Then/And) ---

    @Then("o status da resposta deve ser {int} para norma")
    public void oStatusDaRespostaDeveSerParaNorma(int statusCode) {

        super.oStatusDaRespostaDeveSer(statusCode);
    }

    @And("o corpo da resposta da norma deve conter {string}")
    public void oCorpoDaRespostaDaNormaDeveConter(String jsonPath) {
        // Reutiliza o metodo do CommonApiSteps
        super.oCorpoDaRespostaDeveConter(jsonPath);
        if ("id".equals(jsonPath)) {

            Object idObject = JsonPath.read(latestResponse.getBody(), jsonPath);
            if (idObject instanceof Number) {
                Long id = ((Number) idObject).longValue();
                TestContext.set("normaId_lastCreated", id);
            } else {
                throw new IllegalStateException("O campo '" + jsonPath + "' não é um número: " + idObject);
            }
        }
    }

    @And("eu salvo o {string} da resposta da norma como {string}")
    public void euSalvoODaRespostaDaNormaComo(String jsonPath, String contextKey) {
        super.euSalvoODaRespostaComo(jsonPath, contextKey);
    }

    @And("o corpo da resposta da norma deve conter {string}: {string}")
    public void oCorpoDaRespostaDaNormaDeveConterValorString(String jsonPath, String expectedValue) {
        super.oCorpoDaRespostaDeveConterValorString(jsonPath, expectedValue);
    }

    @And("o corpo da resposta da norma deve conter {string}: {int}")
    public void oCorpoDaRespostaDaNormaDeveConterValorInt(String jsonPath, int expectedValue) {

        super.oCorpoDaRespostaDeveConterValorInt(jsonPath, expectedValue);
    }

    @And("o corpo da resposta da norma deve conter a mensagem {string}")
    public void oCorpoDaRespostaDaNormaDeveConterAMensagem(String expectedMessage) {

        super.oCorpoDaRespostaDeveConterAMensagem(expectedMessage);
    }

    @And("o corpo da resposta da norma deve ser uma lista")
    public void oCorpoDaRespostaDaNormaDeveSerUmaLista() throws IOException {

        super.oCorpoDaRespostaDeveSerUmaLista();
    }

    @And("a lista de normas deve conter um item com {string}: {string}")
    public void aListaDeNormasDeveConterUmItemCom(String jsonPath, String expectedValue) throws IOException {

        super.aListaDeveConterUmItemCom(jsonPath, expectedValue);
    }

    @And("o corpo da resposta da norma deve conter {string}: {{string}}")
    public void oCorpoDaRespostaDaNormaDeveConterObjetoJson(String jsonPath, String expectedJsonFragment) throws IOException {

        super.oCorpoDaRespostaDeveConterObjetoJson(jsonPath, expectedJsonFragment);
    }
}