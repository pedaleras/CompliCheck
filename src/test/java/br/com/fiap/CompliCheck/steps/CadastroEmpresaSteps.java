package br.com.fiap.CompliCheck.steps;
import br.com.fiap.CompliCheck.model.Empresa;
import br.com.fiap.CompliCheck.repository.EmpresaRepository;
import br.com.fiap.CompliCheck.util.TestContext;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
/**
 * Definições de passos para gerenciar o cadastro e consulta de empresas.
 * Estende CommonApiSteps para reutilizar a lógica de requisições HTTP e verificação de respostas.
 */
public class CadastroEmpresaSteps extends CommonApiSteps {

    @Autowired
    private EmpresaRepository empresaRepository; // Para criar dados diretamente no banco de dados em steps 'Given'

    private final ObjectMapper objectMapper = new ObjectMapper(); // Reutiliza o ObjectMapper


    // Variáveis como jsonBody (para o JSON a ser enviado) podem ser armazenadas no TestContext se precisarem
    // ser passadas entre métodos @Given e @When que não aceitam o JSON diretamente.
    // Para este caso, o JSON será passado diretamente para o metodo herdado.

    private String generateUniqueCnpj() {
        Random random = new Random();
        StringBuilder cnpj = new StringBuilder();
        // Gerar 14 dígitos numéricos aleatórios
        for (int i = 0; i < 14; i++) {
            cnpj.append(random.nextInt(10));
        }
        return cnpj.toString();
    }


    @Given("que eu desejo cadastrar uma nova empresa com os seguintes dados:")
    public void queEuDesejoCadastrarUmaNovaEmpresaComOsSeguintesDados(DataTable dataTable) {
    }

    @When("eu faço uma requisição POST para o endpoint {string} com o seguinte JSON para empresa:")
    public void euFacoUmaRequisicaoPOSTParaOEndpointComOSeguinteJSONParaEmpresa(String endpoint, String requestBody) throws IOException {
        ObjectNode rootNode = (ObjectNode) objectMapper.readTree(requestBody);


        String cnpjInput = rootNode.has("cnpj") ? rootNode.get("cnpj").asText() : null;
        String cnpjToUse = cnpjInput;

        if (cnpjInput != null && "12.345.678/0001-23".equals(cnpjInput)) {
            cnpjToUse = generateUniqueCnpj();
            rootNode.put("cnpj", cnpjToUse); // Define o CNPJ gerado no JSON
        }
        // Armazenar o CNPJ usado no TestContext para futuras referências em asserções ou outros passos
        if (cnpjToUse != null) {
            TestContext.set("empresaCnpj_lastCreated", cnpjToUse);
        }

        String finalJsonBody = objectMapper.writeValueAsString(rootNode); // Converte o JSON modificado de volta para string

        // Usa o metodo herdado do CommonApiSteps para fazer a requisição
        super.euFacoUmaRequisicaoParaOEndpointComOSeguinteJSON("POST", endpoint, finalJsonBody);
    }

    @Then("o status da resposta deve ser {int} para empresa")
    public void oStatusDaRespostaDeveSerParaEmpresa(int statusCode) {
        // Reutiliza o metodo do CommonApiSteps
        super.oStatusDaRespostaDeveSer(statusCode);
    }

    @And("o corpo da resposta da empresa deve conter o campo {string}")
    public void oCorpoDaRespostaDaEmpresaDeveConterOCampo(String fieldName) {
        super.oCorpoDaRespostaDeveConter(fieldName);
        if ("id".equals(fieldName)) {
            // Guarda o ID da empresa recém-criada no TestContext para uso em outros cenários
            // Importante: JsonPath.read espera um String no segundo argumento para identificar o caminho.
            // Para obter o valor de um campo simples como "id", a propriedade do JsonPath é o próprio nome do campo.
            // O tipo de retorno de JsonPath.read é um Object, que precisa ser cast para o tipo esperado.
            // `JsonPath.read` retorna `Object`. Se o ID for um número pequeno, pode ser lido como `Integer`.
            // Para garantir que seja um `Long` se necessário, é melhor ler como `Number` e depois converter.
            Object idObject = JsonPath.read(latestResponse.getBody(), "$." + fieldName);
            if (idObject instanceof Number) {
                Long id = ((Number) idObject).longValue();
                TestContext.set("empresaId_lastCreated", id);
            } else {
                throw new IllegalStateException("O campo 'id' não é um número: " + idObject);
            }
        }
    }

    @And("o campo {string} da empresa deve ser {string}")
    public void oCampoDaEmpresaDeveSer(String fieldName, String expectedValue) {
        // Reutiliza o metodo do CommonApiSteps, resolvendo placeholders no valor esperado
        super.oCorpoDaRespostaDeveConterValorString(fieldName, expectedValue);
    }

    @Given("que já existe uma empresa cadastrada com CNPJ {string}")
    public void queJaExisteUmaEmpresaCadastradaComCNPJ(String cnpj) {
        String uniqueCnpj = generateUniqueCnpj();

        Empresa empresa = new Empresa();
        empresa.setNome("Empresa para Duplicidade - Setup " + uniqueCnpj);
        empresa.setCnpj(uniqueCnpj);
        empresa.setSetor("Serviços de Teste");

        Empresa savedEmpresa = empresaRepository.save(empresa);
        assertNotNull(savedEmpresa.getId(), "Empresa deveria ter um ID após ser salva.");

        // Armazena o CNPJ e ID reais da empresa criada no TestContext
        TestContext.set("empresaCnpj_forDuplicateTest", uniqueCnpj);
        TestContext.set("empresaId_forDuplicateTest", savedEmpresa.getId());
    }

    @When("eu faço uma requisição POST para o endpoint {string} com os seguintes dados de uma empresa que já existe:")
    public void euFacoUmaRequisicaoPOSTParaOEndpointComOsSeguintesDadosDeUmaEmpresaQueJaExiste(String endpoint, String requestBody) throws IOException {
        ObjectNode rootNode = (ObjectNode) objectMapper.readTree(requestBody);

        // Substitui o CNPJ do JSON pelo CNPJ que foi criado no passo "Given que já existe uma empresa cadastrada com CNPJ"
        String cnpjParaDuplicidade = TestContext.get("empresaCnpj_forDuplicateTest", String.class);
        if (cnpjParaDuplicidade == null) {
            throw new IllegalStateException("CNPJ para teste de duplicidade não encontrado no TestContext.");
        }
        rootNode.put("cnpj", cnpjParaDuplicidade);

        String finalJsonBody = objectMapper.writeValueAsString(rootNode);

        // Usa o metodo herdado do CommonApiSteps para fazer a requisição
        super.euFacoUmaRequisicaoParaOEndpointComOSeguinteJSON("POST", endpoint, finalJsonBody);
    }

    @And("o corpo da resposta da empresa deve conter a mensagem {string}")
    public void oCorpoDaRespostaDaEmpresaDeveConterAMensagem(String expectedMessage) {
        // Reutiliza o metodo do CommonApiSteps
        super.oCorpoDaRespostaDeveConterAMensagem(expectedMessage);
    }

    @Given("que eu envio um JSON sem o campo {string} da empresa")
    public void queEuEnvioUmJSONSemOCampoDaEmpresa(String fieldName) {
        // Este passo Gherkin é mais para documentação, o JSON real é fornecido no 'When'.
        // Nenhuma ação é necessária aqui.
    }

    // --- Cenários de Consulta ---

    @Given("que existem empresas cadastradas no sistema")
    public void queExistemEmpresasCadastradasNoSistema() {
        // Garante que pelo menos uma empresa existe para o teste de listagem.
        // Verifica se já existe uma empresa criada que possa ser usada.
        Long existingId = TestContext.get("empresaId_lastCreated", Long.class);
        if (existingId == null || !empresaRepository.existsById(existingId)) { // Adiciona verificação de existência no DB
            String uniqueCnpj = generateUniqueCnpj();
            Empresa empresa = new Empresa();
            empresa.setNome("Empresa para Listagem " + uniqueCnpj);
            empresa.setCnpj(uniqueCnpj);
            empresa.setSetor("Setor de Listagem");
            Empresa savedEmpresa = empresaRepository.save(empresa);
            TestContext.set("empresaId_lastCreated", savedEmpresa.getId());
            TestContext.set("empresaCnpj_lastCreated", savedEmpresa.getCnpj());
        }
    }

    @When("eu faço uma requisição GET para o endpoint {string} de empresas")
    public void euFacoUmaRequisicaoGETParaOEndpointDeEmpresas(String endpoint) {
        // Reutiliza o metodo do CommonApiSteps
        super.euFacoUmaRequisicaoParaOEndpoint("GET", endpoint);
    }

    @And("o corpo da resposta deve conter uma lista de empresas")
    public void oCorpoDaRespostaDeveConterUmaListaDeEmpresas() throws IOException {
        // Reutiliza o metodo do CommonApiSteps para verificar se é uma lista
        super.oCorpoDaRespostaDeveSerUmaLista();
        // Adiciona a asserção específica de que a lista não deve estar vazia
        JsonNode root = objectMapper.readTree(latestResponse.getBody());
        // CORREÇÃO: Para verificar isEmpty de um JSON array, não precisa converter para List<Object>
        // Basta verificar se o array possui elementos.
        assertTrue(root.isArray() && root.size() > 0, "A lista de empresas não deve estar vazia e deve ser um array.");
    }

    @Given("que existe uma empresa cadastrada com ID {long}")
    public void queExisteUmaEmpresaCadastradaComID(Long logicalId) {
        // Cria uma empresa diretamente no banco para garantir sua existência
        String uniqueCnpj = generateUniqueCnpj();
        Empresa empresa = new Empresa();
        empresa.setNome("Empresa ID " + logicalId + " para Consulta " + uniqueCnpj);
        empresa.setCnpj(uniqueCnpj);
        empresa.setSetor("Consultoria");

        Empresa savedEmpresa = empresaRepository.save(empresa);
        assertNotNull(savedEmpresa.getId(), "Empresa deveria ter um ID após ser salva.");

        // Armazena o ID e CNPJ reais da empresa criada no TestContext usando o logicalId
        TestContext.set("empresaId_" + logicalId, savedEmpresa.getId());
        TestContext.set("empresaCnpj_" + logicalId, savedEmpresa.getCnpj());
    }

    @When("eu faço uma requisição GET para o endpoint {string} com ID {long}")
    public void euFacoUmaRequisicaoGETParaOEndpointComID(String endpointPrefix, Long logicalId) {
        // Recupera o ID real da empresa do TestContext
        Long idToFetch = TestContext.get("empresaId_" + logicalId, Long.class);
        if (idToFetch == null) {
            throw new IllegalStateException("ID da empresa com logicalId " + logicalId + " não encontrado no TestContext.");
        }
        // Usa o metodo herdado do CommonApiSteps com o endpoint resolvido
        super.euFacoUmaRequisicaoParaOEndpoint("GET", endpointPrefix + idToFetch);
    }

    @And("o corpo da resposta deve conter a empresa com ID {long}")
    public void oCorpoDaRespostaDeveContarAEmpresaComID(Long logicalId) { // Renomeado o método para consistência (Contar -> Conter)
        Long expectedId = TestContext.get("empresaId_" + logicalId, Long.class);
        if (expectedId == null) {
            throw new IllegalStateException("ID da empresa com logicalId " + logicalId + " não encontrado no TestContext.");
        }
        // Reutiliza o metodo do CommonApiSteps para verificar o ID
        super.oCorpoDaRespostaDeveConterValorInt("id", expectedId.intValue()); // Note: Assumindo int para o id no JSON.
    }

    @Given("que não existe uma empresa cadastrada com ID {long}")
    public void queNaoExisteUmaEmpresaCadastradaComID(Long nonExistentId) {
        // Armazena um ID que sabemos que não existe para este teste específico no TestContext.
        TestContext.set("nonExistentEmpresaId", nonExistentId);
    }


    // --- Cenários de Atualização ---

    @Given("que os seguintes dados da empresa precisam ser atualizados:")
    public void queOsSeguintesDadosDaEmpresaPrecisamSerAtualizados(DataTable dataTable) {
        // Apenas para documentação no Gherkin, o JSON do 'When' será o usado.
    }

    @When("eu faço uma requisição PUT para o endpoint {string} com o seguinte JSON de atualização:")
    public void euFacoUmaRequisicaoPUTParaOEndpointComOSeguinteJSONDeAtualizacao(String endpoint, String requestBody) throws IOException {
        ObjectNode rootNode = (ObjectNode) objectMapper.readTree(requestBody);

        // Para garantir que o teste de atualização funcione, usamos o ID da empresa criada para o cenário.
        Long idToUpdate = TestContext.get("empresaId_lastCreated", Long.class);
        if (idToUpdate == null) {
            // Se não houver 'empresaId_lastCreated', tenta um ID específico para atualização se o cenário o configurou.
            idToUpdate = TestContext.get("empresaId_forUpdate", Long.class);
        }

        if (idToUpdate != null) {
            rootNode.put("id", idToUpdate); // Define o ID real da empresa no JSON

            // Ajusta o CNPJ para ser o mesmo da empresa existente, para evitar erro de CNPJ duplicado
            String existingCnpj = TestContext.get("empresaCnpj_lastCreated", String.class);
            if (existingCnpj == null) {
                existingCnpj = TestContext.get("empresaCnpj_forUpdate", String.class);
            }

            if (existingCnpj != null) {
                rootNode.put("cnpj", existingCnpj);
            } else {
                System.err.println("WARN: CNPJ da empresa para atualização não encontrado no contexto. Usando um CNPJ genérico.");
                rootNode.put("cnpj", generateUniqueCnpj()); // Fallback
            }
        } else {
            System.err.println("WARN: ID da empresa para atualização é nulo no TestContext. O teste pode falhar.");
            rootNode.put("cnpj", generateUniqueCnpj()); // Garantir que o CNPJ esteja no payload
        }
        String finalJsonBody = objectMapper.writeValueAsString(rootNode);

        // Usa o metodo herdado do CommonApiSteps para fazer a requisição
        super.euFacoUmaRequisicaoParaOEndpointComOSeguinteJSON("PUT", endpoint, finalJsonBody);
    }

    // --- Cenários de Exclusão ---

    @When("eu faço uma requisição DELETE para o endpoint {string} com ID {long}")
    public void euFacoUmaRequisicaoDELETEParaOEndpointComID(String endpointPrefix, Long logicalId) {
        // Tenta pegar o ID do TestContext que foi salvo para este cenário ou um ID genérico.
        // CORREÇÃO: O ID para exclusão deve vir do TestContext, se um ID específico foi configurado.
        // Caso contrário, usa o `logicalId` direto, como no seu código original.
        Long idToDelete = TestContext.get("empresaId_" + logicalId, Long.class);

        if (idToDelete == null) {
            // Se o ID não foi salvo explicitamente no contexto com esse `logicalId`,
            // tenta usar o `logicalId` como ID direto ou o último ID criado se existir e for relevante.
            System.out.println("WARN: ID da empresa com logicalId " + logicalId + " não encontrado no TestContext. Tentando usar 'empresaId_lastCreated'.");
            idToDelete = TestContext.get("empresaId_lastCreated", Long.class);
        }

        if (idToDelete == null) {
            // Se ainda assim não encontrar, usa o logicalId do Gherkin diretamente.
            idToDelete = logicalId;
            System.out.println("WARN: Usando o ID literal " + logicalId + " do feature file para DELETE.");
        }


        // Usa o metodo herdado do CommonApiSteps para fazer a requisição
        super.euFacoUmaRequisicaoParaOEndpoint("DELETE", String.format("%s%d", endpointPrefix, idToDelete));
    }
}