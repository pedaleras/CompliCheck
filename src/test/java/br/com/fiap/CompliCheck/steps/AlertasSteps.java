package br.com.fiap.CompliCheck.steps;

import br.com.fiap.CompliCheck.dto.AlertaCadastroDto;
import br.com.fiap.CompliCheck.dto.AlertaExibicaoDto;
import br.com.fiap.CompliCheck.model.Alerta;
import br.com.fiap.CompliCheck.model.Empresa;
import br.com.fiap.CompliCheck.model.Norma;
import br.com.fiap.CompliCheck.repository.AlertaRepository;
import br.com.fiap.CompliCheck.repository.EmpresaRepository;
import br.com.fiap.CompliCheck.repository.NormaRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Garante que o profile de test seja usado, com um banco de dados em memória
public class AlertasSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private AlertaRepository alertaRepository;

    @Autowired
    private NormaRepository normaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    private Response response;
    private AlertaCadastroDto alertaCadastroDto;
    private Norma normaCadastrada;
    private Empresa empresaCadastrada;
    private ObjectMapper objectMapper = new ObjectMapper(); // Para converter JSON strings em objetos e vice-versa

    @Before
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        alertaRepository.deleteAll(); // Limpa dados para cada cenário de teste
        normaRepository.deleteAll();
        empresaRepository.deleteAll(); // Garante que as empresas também sejam limpas
        empresaCadastrada = null;
        normaCadastrada = null;
    }

    @After
    public void teardown() {
        alertaRepository.deleteAll();
        normaRepository.deleteAll();
        empresaRepository.deleteAll();
    }

    // --- TRANSFORMERS ---
    @DataTableType
    public AlertaCadastroDto alertaCadastroDtoEntry(Map<String, String> entry) {
        return new AlertaCadastroDto(
                entry.containsKey("id") ? Long.valueOf(entry.get("id")) : null,
                entry.get("status"),
                LocalDate.parse(entry.get("dataVerificacao")),
                Long.valueOf(entry.get("normaId"))
        );
    }

    // --- GIVEN STEPS ---
    @Given("que existe uma norma cadastrada com ID {long}")
    public void que_existe_uma_norma_cadastrada_com_id(Long id) {
        // Garante que uma empresa exista para a norma ser criada
        if (empresaCadastrada == null) {
            Empresa novaEmpresa = new Empresa();
            // novaEmpresa.setId(null); // ID é gerado automaticamente pelo DB
            novaEmpresa.setNome("Empresa Padrão para Norma " + id);
            empresaCadastrada = empresaRepository.save(novaEmpresa);
        }

        Norma novaNorma = new Norma();
        // novaNorma.setId(id); // ID é gerado automaticamente pelo DB se for null
        novaNorma.setDescricao("Norma " + id);
        novaNorma.setCategoria("Categoria A");
        novaNorma.setDataLimite(LocalDate.now().plusMonths(6));
        novaNorma.setEmpresa(empresaCadastrada);
        normaCadastrada = normaRepository.save(novaNorma);
    }

    @Given("que eu quero criar um alerta para uma norma que não existe")
    public void que_eu_quero_criar_um_alerta_para_uma_norma_que_nao_existe() {
    }

    @Given("que eu envio um JSON sem o campo {string}")
    public void que_eu_envio_um_json_sem_o_campo(String campoAusente) {
    }

    @Given("que existem alertas cadastrados no sistema")
    public void que_existem_alertas_cadastrados_no_sistema() {
        if (empresaCadastrada == null) {
            Empresa novaEmpresa = new Empresa();
            novaEmpresa.setNome("Empresa Base para Alertas");
            empresaCadastrada = empresaRepository.save(novaEmpresa);
        }

        Norma norma1 = new Norma();
        norma1.setDescricao("Norma A");
        norma1.setCategoria("Ambiental");
        norma1.setDataLimite(LocalDate.now().plusYears(1));
        norma1.setEmpresa(empresaCadastrada);
        normaRepository.save(norma1);

        Norma norma2 = new Norma();
        norma2.setDescricao("Norma B");
        norma2.setCategoria("Financeira");
        norma2.setDataLimite(LocalDate.now().plusYears(1));
        norma2.setEmpresa(empresaCadastrada);
        normaRepository.save(norma2);

        Alerta alerta1 = new Alerta();
        alerta1.setStatus("Pendente");
        alerta1.setDataVerificacao(LocalDate.now().plusDays(30));
        alerta1.setNorma(norma1);
        alertaRepository.save(alerta1);

        Alerta alerta2 = new Alerta();
        alerta2.setStatus("Concluido");
        alerta2.setDataVerificacao(LocalDate.now().plusDays(60));
        alerta2.setNorma(norma2);
        alertaRepository.save(alerta2);
    }

    @Given("que existe um alerta cadastrado com ID {long}")
    public void que_existe_um_alerta_cadastrado_com_id(Long id) {
        if (empresaCadastrada == null) {
            Empresa novaEmpresa = new Empresa();
            novaEmpresa.setNome("Empresa para Alerta " + id);
            empresaCadastrada = empresaRepository.save(novaEmpresa);
        }

        normaCadastrada = new Norma();
        normaCadastrada.setDescricao("Norma para Alerta " + id);
        normaCadastrada.setCategoria("Regulatória");
        normaCadastrada.setDataLimite(LocalDate.now().plusDays(100));
        normaCadastrada.setEmpresa(empresaCadastrada);
        normaRepository.save(normaCadastrada);

        Alerta alerta = new Alerta();
        alerta.setId(id); // Definindo ID explicitamente, se a estratégia de geração permitir sobrescrever
        alerta.setStatus("Pendente");
        alerta.setDataVerificacao(LocalDate.now().plusMonths(1));
        alerta.setNorma(normaCadastrada);
        alertaRepository.save(alerta);
    }

    @Given("que não existe um alerta cadastrado com ID {long}")
    public void que_nao_existe_um_alerta_cadastrado_com_id(Long id) {
    }

    @Given("que existe um alerta cadastrado com ID {long} e os seguintes dados precisam ser atualizados:")
    public void que_existe_um_alerta_cadastrado_com_id_e_os_seguintes_dados_precisam_ser_atualizados(Long id, Map<String, String> dadosAtualizacao) {
        if (empresaCadastrada == null) {
            Empresa novaEmpresa = new Empresa();
            novaEmpresa.setNome("Empresa para Atualizacao");
            empresaCadastrada = empresaRepository.save(novaEmpresa);
        }

        normaCadastrada = new Norma();
        normaCadastrada.setDescricao("Norma para Atualizacao");
        normaCadastrada.setCategoria("Legal");
        normaCadastrada.setDataLimite(LocalDate.now().plusYears(2));
        normaCadastrada.setEmpresa(empresaCadastrada);
        normaRepository.save(normaCadastrada);

        Alerta alertaOriginal = new Alerta();
        alertaOriginal.setId(id); // Definindo ID explicitamente
        alertaOriginal.setStatus("Pendente");
        alertaOriginal.setDataVerificacao(LocalDate.now().minusDays(10));
        alertaOriginal.setNorma(normaCadastrada);
        alertaRepository.save(alertaOriginal);

        // Prepara o DTO de atualização com o ID e os novos dados
        String status = dadosAtualizacao.getOrDefault("status", alertaOriginal.getStatus());
        LocalDate dataVerificacao = dadosAtualizacao.containsKey("dataVerificacao") ? LocalDate.parse(dadosAtualizacao.get("dataVerificacao")) : alertaOriginal.getDataVerificacao();
        Long normaId = dadosAtualizacao.containsKey("normaId") ? Long.valueOf(dadosAtualizacao.get("normaId")) : alertaOriginal.getNorma().getId();

        this.alertaCadastroDto = new AlertaCadastroDto(id, status, dataVerificacao, normaId);
    }


    // --- WHEN STEPS ---
    @When("eu faço uma requisição POST para o endpoint {string} com o seguinte JSON:")
    public void eu_faco_uma_requisicao_post_para_o_endpoint_com_o_seguinte_json(String endpoint, String jsonBody) throws JsonProcessingException {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(endpoint);
    }

    @When("eu faço uma requisição GET para o endpoint {string}")
    public void eu_faco_uma_requisicao_get_para_o_endpoint(String endpoint) {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get(endpoint);
    }

    @When("eu faço uma requisição PUT para o endpoint {string}")
    public void eu_faco_uma_requisicao_put_para_o_endpoint(String endpoint) {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(alertaCadastroDto) // Usa o DTO preparado no Given
                .when()
                .put(endpoint);
    }

    @When("eu faço uma requisição DELETE para o endpoint {string}")
    public void eu_faco_uma_requisicao_delete_para_o_endpoint(String endpoint) {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete(endpoint);
    }

    // --- THEN STEPS ---
    @Then("o status da resposta deve ser {int}")
    public void o_status_da_resposta_deve_ser(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @Then("o corpo da resposta deve conter o campo {string}")
    public void o_corpo_da_resposta_deve_conter_o_campo(String campo) {
        assertNotNull(response.jsonPath().get(campo), "O campo '" + campo + "' não deve ser nulo na resposta.");
    }

    @Then("o campo {string} deve ser {string}")
    public void o_campo_deve_ser(String campo, String valorEsperado) {
        // Para campos de data, a comparação pode precisar de um ajuste
        if (campo.equals("dataVerificacao")) {
            // Converte ambos para String no formato ISO (yyyy-MM-dd) para comparação
            assertEquals(valorEsperado, LocalDate.parse(response.jsonPath().getString(campo)).toString(), "O campo '" + campo + "' não corresponde ao valor esperado.");
        } else {
            assertEquals(valorEsperado, response.jsonPath().get(campo).toString(), "O campo '" + campo + "' não corresponde ao valor esperado.");
        }
    }

    @Then("o corpo da resposta deve conter a mensagem {string}")
    public void o_corpo_da_resposta_deve_conter_a_mensagem(String mensagem) {
        assertTrue(response.asString().contains(mensagem), "A mensagem esperada '" + mensagem + "' não foi encontrada no corpo da resposta.");
    }

    @Then("o corpo da resposta deve conter uma lista de alertas")
    public void o_corpo_da_resposta_deve_conter_uma_lista_de_alertas() {
        List<AlertaExibicaoDto> alertas = response.jsonPath().getList(".", AlertaExibicaoDto.class);
        assertFalse(alertas.isEmpty(), "A lista de alertas não deveria estar vazia.");
    }

    @Then("o corpo da resposta deve conter o alerta com ID {long}")
    public void o_corpo_da_resposta_deve_conter_o_alerta_com_id(Long id) {
        AlertaExibicaoDto alerta = response.as(AlertaExibicaoDto.class);
        assertEquals(id, alerta.id(), "O ID do alerta na resposta não corresponde ao ID esperado.");
    }
}