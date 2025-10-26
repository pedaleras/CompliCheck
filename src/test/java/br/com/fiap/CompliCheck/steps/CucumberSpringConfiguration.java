package br.com.fiap.CompliCheck.steps;

import br.com.fiap.CompliCheck.CompliCheckApplication;
import br.com.fiap.CompliCheck.repository.EmpresaRepository;
import br.com.fiap.CompliCheck.repository.NormaRepository;
import br.com.fiap.CompliCheck.util.TestContext;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Classe de configuração para integrar o Cucumber com o contexto do Spring Boot.
 * Esta classe garante que a aplicação Spring Boot seja carregada antes da execução dos testes Cucumber,
 * permitindo que os Step Definitions acessem os beans do Spring.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = CompliCheckApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 2. Configura o Spring Boot para testes, referenciando a classe principal
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @Autowired
    private NormaRepository normaRepository;
    @Autowired
    private EmpresaRepository empresaRepository;


    /**
     * Hook Cucumber @Before: Este metodo será executado antes de CADA cenário Cucumber.
     * Ele é crucial para garantir o isolamento dos testes, limpando o banco de dados
     * e o contexto de variáveis entre um cenário e outro.
     */
    @Before
    public void setup() {
        System.out.println("----- Iniciando cenário Cucumber com contexto Spring carregado e dados limpos -----");
        // Limpa o banco de dados para garantir isolamento entre os cenários
        normaRepository.deleteAll();
        empresaRepository.deleteAll();
        // Limpa o TestContext ThreadLocal para evitar "vazamento" de dados entre cenários
        TestContext.clear();
    }
}