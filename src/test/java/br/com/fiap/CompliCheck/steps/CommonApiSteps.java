package br.com.fiap.CompliCheck.steps;
import br.com.fiap.CompliCheck.util.TestContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Component
/**
 * Classe com definições de passos genéricos para interação com APIs REST.
 * Contém métodos para fazer requisições HTTP, verificar status e conteúdo JSON.
 * Também gerencia o token de autenticação e a resolução de placeholders.
 */
public class CommonApiSteps {

    // TestRestTemplate é uma alternativa do Spring para RestTemplate,
    // ideal para testes de integração com a aplicação rodando em uma porta aleatória.
    @Autowired
    protected TestRestTemplate restTemplate;

    // latestResponse armazena a resposta da última requisição HTTP.
    // É protected para que classes filhas possam acessá-la.
    protected ResponseEntity<String> latestResponse; // Alterado para String para consistência com o uso do corpo

    // ObjectMapper para lidar com JSON, útil para validações mais complexas.
    protected ObjectMapper objectMapper = new ObjectMapper();

    // Padrão para encontrar placeholders como "$chave" (dentro ou fora de aspas)
    // Ex: "{"id": "$idNormaCriada"}" ou "/api/normas/$idNormaCriada"
    // CORREÇÃO: As aspas duplas e o '$' devem ser escapados dentro da regex
    private static final Pattern PLACEHOLDER_PATTERN;

    static {
        PLACEHOLDER_PATTERN = Pattern.compile("""
                ("""\);
    }

    /**
     * Resolve placeholders ($chave) em uma string usando o TestContext.
     * Ex: "{"id": "$idNormaCriada"}" -> "{"id": 123}"
     * Ex: "/api/normas/$idNormaCriada" -> "/api/normas/123"
     * @param text A string contendo placeholders.
     * @return A string com os placeholders resolvidos.
     */
    protected String resolvePlaceholders(String text) {
        if (text == null || !text.contains("$")) {
            return text;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key;
            boolean isQuotedPlaceholder = (matcher.group(1) != null); // Indica se o placeholder original incluía aspas (ex: "$key")

            // Extrai a chave real do TestContext.
            // group(2) para o placeholder com aspas (ex: de "$chave" pega "chave")
            // group(4) para o placeholder sem aspas (ex: de $chave pega "chave")
            if (isQuotedPlaceholder) {
                key = matcher.group(2);
            } else {
                key = matcher.group(4);
            }

            Object value = TestContext.get(key);
            if (value != null) {
                String replacement;
                if (value instanceof String) {
                    replacement = Matcher.quoteReplacement((String) value);
                    if (isQuotedPlaceholder) {
                        // Se o placeholder original era "$KEY", o valor String deve virar "VALOR"
                        // CORREÇÃO: Sintaxe correta para adicionar aspas duplas em uma String.
                        replacement = """ + replacement + """;
                    }
                } else {
                    // Se o valor não é String (número, booleano), ele nunca deve ter aspas extras no JSON.
                    replacement = Matcher.quoteReplacement(value.toString());
                }
                matcher.appendReplacement(sb, replacement);
            } else {
                // Mantém o placeholder original (com ou sem aspas) se o valor não for encontrado no TestContext
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Realiza uma requisição HTTP sem corpo de requisição.
     * Exemplo: "When eu faço uma requisição GET para o endpoint "/api/minha-entidade""
     * @param method O metodo HTTP (GET, DELETE, etc.)
     * @param endpoint O endpoint da API.
     */
    @When("eu faço uma requisição {word} para o endpoint {string}")
    public void euFacoUmaRequisicaoParaOEndpoint(String method, String endpoint) {
        // Obtém os cabeçalhos, incluindo o token de autenticação, se houver.
        HttpEntity<String> requestEntity = new HttpEntity<>(getHeaders()); // Especifique o tipo para HttpEntity
        performRequest(method, resolvePlaceholders(endpoint), requestEntity);
    }

    /**
     * Realiza uma requisição HTTP com corpo de requisição JSON.
     * Exemplo: "When eu faço uma requisição POST para o endpoint "/api/minha-entidade" com o seguinte JSON:"
     * @param method O metodo HTTP (POST, PUT, etc.)
     * @param endpoint O endpoint da API.
     * @param jsonBody O corpo da requisição em formato JSON.
     */
    @When("eu faço uma requisição {word} para o endpoint {string} com o seguinte JSON:")
    public void euFacoUmaRequisicaoParaOEndpointComOSeguinteJSON(String method, String endpoint, String jsonBody) {
        HttpHeaders headers = getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Define o tipo de conteúdo como JSON.
        String processedJsonBody = resolvePlaceholders(jsonBody); // Resolve placeholders no corpo JSON.
        HttpEntity<String> requestEntity = new HttpEntity<>(processedJsonBody, headers); // Especifique o tipo para HttpEntity
        performRequest(method, resolvePlaceholders(endpoint), requestEntity);
    }

    /**
     * Metodo auxiliar para executar a requisição HTTP real.
     * @param method O metodo HTTP.
     * @param endpoint O endpoint.
     * @param requestEntity A entidade HTTP contendo headers e corpo.
     */
    private void performRequest(String method, String endpoint, HttpEntity<String> requestEntity) { // Especifique o tipo para HttpEntity
        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase()); // Converte a string do metodo para enum.
        latestResponse = restTemplate.exchange(endpoint, httpMethod, requestEntity, String.class);
    }

    /**
     * Constrói os cabeçalhos HTTP, incluindo o token JWT do TestContext, se disponível.
     * @return HttpHeaders com as configurações necessárias.
     */
    protected HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String authToken = TestContext.get("authToken", String.class); // Recupera o token do TestContext.
        if (authToken != null && !authToken.isEmpty()) {
            // Adiciona o token como "Bearer" se estiver presente.
            // A substituição ".replace("Bearer ", "")" é para garantir que não haja "Bearer Bearer "
            headers.setBearerAuth(authToken.replace("Bearer ", ""));
        }
        return headers;
    }

    /**
     * Verifica se o status da resposta HTTP é o esperado.
     * Exemplo: "Then o status da resposta deve ser 201"
     * @param expectedStatus O código de status HTTP esperado.
     */
    @Then("o status da resposta deve ser {int}")
    public void oStatusDaRespostaDeveSer(int expectedStatus) {
        assertNotNull(latestResponse, "A requisição não foi feita. 'latestResponse' é nulo.");
        assertEquals(expectedStatus, latestResponse.getStatusCode().value(),
                "Status da resposta incorreto. Esperado: " + expectedStatus +
                        ", Recebido: " + latestResponse.getStatusCode().value() +
                        ". Corpo da resposta: " + latestResponse.getBody());

    }

    /**
     * Verifica se o corpo da resposta JSON contém uma propriedade específica.
     * Exemplo: "And o corpo da resposta deve conter "id""
     * @param jsonPath O caminho JSON da propriedade (e.g., "id", "data[0].nome").
     */
    @And("o corpo da resposta deve conter {string}")
    public void oCorpoDaRespostaDeveConter(String jsonPath) {
        assertNotNull(latestResponse.getBody(), "Corpo da resposta é nulo.");
        try {
            // Tenta ler o JSON Path; se não conseguir, significa que a propriedade não existe.
            Object result = JsonPath.read(latestResponse.getBody(), jsonPath);
            assertNotNull(result,
                    "JsonPath '" + jsonPath + "' encontrado, mas seu valor é nulo no corpo da resposta: " + latestResponse.getBody());
        } catch (Exception e) {
            throw new AssertionError("Erro ao ler JsonPath '" + jsonPath + "' ou propriedade não encontrada. " +
                    "Corpo da resposta: " + latestResponse.getBody() + ". Erro: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se uma propriedade no corpo da resposta JSON tem um valor de string específico.
     * Exemplo: "And o corpo da resposta deve conter "descricao": "Norma ISO 14001 para Meio Ambiente""
     * @param jsonPath O caminho JSON da propriedade.
     * @param expectedValue O valor de string esperado.
     */
    @And("o corpo da resposta deve conter {string}: {string}")
    public void oCorpoDaRespostaDeveConterValorString(String jsonPath, String expectedValue) {
        assertNotNull(latestResponse.getBody(), "Corpo da resposta é nulo.");
        try {
            String resolvedExpectedValue = resolvePlaceholders(expectedValue); // Resolve placeholders no valor esperado.
            String actualValue = JsonPath.read(latestResponse.getBody(), jsonPath).toString();
            assertEquals(resolvedExpectedValue, actualValue,
                    "Valor incorreto para JsonPath '" + jsonPath + "'. Esperado: '" + resolvedExpectedValue +
                            "', Recebido: '" + actualValue + "'. Corpo da resposta: " + latestResponse.getBody());
        } catch (Exception e) {
            throw new AssertionError("Erro ao ler JsonPath '" + jsonPath + "': " + e.getMessage() +
                    ". Corpo da resposta: " + latestResponse.getBody(), e);
        }
    }

    /**
     * Verifica se uma propriedade no corpo da resposta JSON tem um valor inteiro específico.
     * Exemplo: "And o corpo da resposta deve conter "id": 1"
     * @param jsonPath O caminho JSON da propriedade.
     * @param expectedValue O valor inteiro esperado.
     */
    @And("o corpo da resposta deve conter {string}: {int}")
    public void oCorpoDaRespostaDeveConterValorInt(String jsonPath, int expectedValue) {
        assertNotNull(latestResponse.getBody(), "Corpo da resposta é nulo.");
        try {
            // Se o JSON Path retornar um Long (ex: IDs de banco de dados), precisamos converter para Integer
            // para a comparação com 'expectedValue' que é 'int'.
            Object actualValueObject = JsonPath.read(latestResponse.getBody(), jsonPath);
            Integer actualValue = null;
            if (actualValueObject instanceof Number) {
                actualValue = ((Number) actualValueObject).intValue();
            } else {
                throw new ClassCastException("O valor retornado pelo JsonPath não é um número: " + actualValueObject.getClass().getName());
            }

            assertEquals(expectedValue, actualValue,
                    "Valor incorreto para JsonPath '" + jsonPath + "'. Esperado: '" + expectedValue +
                            "', Recebido: '" + actualValue + "'. Corpo da resposta: " + latestResponse.getBody());
        } catch (Exception e) {
            throw new AssertionError("Erro ao ler JsonPath '" + jsonPath + "': " + e.getMessage() +
                    ". Corpo da resposta: " + latestResponse.getBody(), e);
        }
    }
    // TODO: Adicionar metodos semelhantes para double, boolean, long, etc. se necessário.

    /**
     * Verifica se o corpo da resposta JSON contém uma mensagem de erro ou validação específica.
     * Procura a mensagem em diferentes locais comuns (campo "message", direto no corpo, ou em listas de erros).
     * Exemplo: "And o corpo da resposta deve conter a mensagem "A descrição da norma é obrigatória""
     * @param expectedMessage A mensagem esperada.
     */
    @And("o corpo da resposta deve conter a mensagem {string}")
    public void oCorpoDaRespostaDeveConterAMensagem(String expectedMessage) {
        assertNotNull(latestResponse.getBody(), "Corpo da resposta é nulo.");
        String resolvedExpectedMessage = resolvePlaceholders(expectedMessage);
        try {
            JsonNode root = objectMapper.readTree(latestResponse.getBody());
            String actualMessage = null;
            if (root.has("message")) { // Padrão comum para mensagens de erro
                actualMessage = root.get("message").asText();
            } else if (root.isTextual()) { // Se a resposta for apenas uma string de erro
                actualMessage = root.asText();
            } else if (root.isArray()) { // Para erros de validação que retornam uma lista de objetos com "message"
                Optional<String> matchedError = root.findValuesAsText("message").stream()
                        .filter(msg -> msg.contains(resolvedExpectedMessage))
                        .findFirst();
                if (matchedError.isPresent()) {
                    actualMessage = matchedError.get();
                } else {
                    actualMessage = root.toPrettyString(); // Fallback para depuração
                }
            }

            assertThat(actualMessage)
                    .withFailMessage("Mensagem esperada '%s' não encontrada. Corpo da resposta: %s",
                            resolvedExpectedMessage, latestResponse.getBody())
                    .contains(resolvedExpectedMessage);
        } catch (IOException e) {
            // Se não for um JSON válido, tenta encontrar a mensagem diretamente no corpo bruto.
            assertThat(latestResponse.getBody())
                    .withFailMessage("Mensagem esperada '%s' não encontrada no corpo da resposta: %s",
                            resolvedExpectedMessage, latestResponse.getBody())
                    .contains(resolvedExpectedMessage);
        }

    }

    /**
     * Salva um valor do corpo da resposta JSON no TestContext para uso posterior.
     * Exemplo: "And eu salvo o "id" da resposta como "idNormaCriada""
     * @param jsonPath O caminho JSON da propriedade a ser salva.
     * @param contextKey A chave sob a qual o valor será armazenado no TestContext.
     */
    @And("eu salvo o {string} da resposta como {string}")
    public void euSalvoODaRespostaComo(String jsonPath, String contextKey) {
        assertNotNull(latestResponse.getBody(), "Corpo da resposta é nulo.");
        Object value = JsonPath.read(latestResponse.getBody(), jsonPath);
        assertNotNull(value, "O valor do JsonPath '" + jsonPath + "' é nulo e não pode ser salvo no contexto.");
        TestContext.set(contextKey, value); // Armazena o valor no TestContext.
    }

    /**
     * Verifica se o corpo da resposta é uma lista (array JSON).
     * Exemplo: "And o corpo da resposta deve ser uma lista"
     */
    @And("o corpo da resposta deve ser uma lista")
    public void oCorpoDaRespostaDeveSerUmaLista() throws IOException {
        assertNotNull(latestResponse.getBody(), "Corpo da resposta é nulo.");
        JsonNode root = objectMapper.readTree(latestResponse.getBody());
        assertTrue(root.isArray(), "O corpo da resposta não é uma lista: " + latestResponse.getBody());
    }


    @And("a lista deve conter um item com {string}: {string}")
    public void aListaDeveConterUmItemCom(String jsonPath, String expectedValue) throws IOException {
        assertNotNull(latestResponse.getBody(), "Corpo da resposta é nulo.");
        JsonNode root = objectMapper.readTree(latestResponse.getBody());
        assertTrue(root.isArray(), "O corpo da resposta não é uma lista: " + latestResponse.getBody());

        String resolvedExpectedValue = resolvePlaceholders(expectedValue);

        boolean found = false;
        for (JsonNode item : root) {
            try {
                Object actualValue = JsonPath.read(item.toString(), jsonPath);
                if (resolvedExpectedValue.equals(actualValue.toString())) {
                    found = true;
                    break;
                }
            } catch (Exception e) {
                // Ignora exceções (e.g., JsonPath não encontrado neste item),
                // pois estamos procurando um item específico na lista.
            }
        }
        assertTrue(found, "Nenhum item na lista contém '" + jsonPath + "': '" + resolvedExpectedValue +
                "'. Corpo da resposta: " + latestResponse.getBody());

    }

    /**
     * Verifica se uma propriedade no corpo da resposta contém um fragmento JSON específico.
     * Útil para verificar objetos aninhados (ex: "empresa": {"id":1,"nome":"Empresa Teste"}).
     * O fragmento JSON é passado como string e resolvido para comparação.
     * Exemplo: "And o corpo da resposta deve conter "empresa": {id:$empresaId_1,nome:"Empresa Teste"}"
     * (Note que chaves de string no fragmento JSON são opcionais se o valor for um placeholder)
     * @param jsonPath O caminho JSON da propriedade que deve conter o fragmento.
     * @param expectedJsonFragment A string do fragmento JSON esperado, com ou sem chaves envolventes.
     */
    @And("o corpo da resposta deve conter {string}: {{string}}") // regex para {string} dentro de chaves literais
    public void oCorpoDaRespostaDeveConterObjetoJson(String jsonPath, String expectedJsonFragment) throws IOException {
        assertNotNull(latestResponse.getBody(), "Corpo da resposta é nulo.");

        String resolvedExpectedJsonFragment = resolvePlaceholders("{" + expectedJsonFragment + "}");
        JsonNode expectedNode = objectMapper.readTree(resolvedExpectedJsonFragment);


        String actualJsonString = JsonPath.read(latestResponse.getBody(), jsonPath).toString();
        JsonNode actualNode = objectMapper.readTree(actualJsonString);

        assertEquals(expectedNode, actualNode,
                "Fragmento JSON incorreto para JsonPath '" + jsonPath + "'. Esperado: '" + expectedNode.toPrettyString() +
                        "', Recebido: '" + actualNode.toPrettyString() + "'. Corpo da resposta: " + latestResponse.getBody());

    }
}