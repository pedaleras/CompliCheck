package br.com.fiap.CompliCheck.runner;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ConfigurationParameters;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest; //Anotação do Spring Boot para carregar o contexto
import org.springframework.test.context.ActiveProfiles; //Para ativar perfis específicos de teste
import org.springframework.test.context.ContextConfiguration; //boa prática para especificar a classe principal
import br.com.fiap.CompliCheck.CompliCheckApplication; // Assumindo que a classe principal da aplicação está aqui

@Suite
@IncludeEngines("cucumber")

@SelectClasspathResource("features") // Especifica onde encontrar os arquivos .feature no classpath
@ConfigurationParameters({
        @ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-reports/cucumber-html-report.html,json:target/cucumber-reports/cucumber.json"),
        @ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "br.com.fiap.CompliCheck.steps,br.com.fiap.CompliCheck.util"), // Pacote dos steps e utilitários/hooks, ajustado para sua estrutura
        @ConfigurationParameter(key = "cucumber.monochrome", value = "true"), // Usando a string literal "cucumber.monochrome"

})
// ADIÇÕES ESSENCIAIS PARA INTEGRAÇÃO COM SPRING BOOT
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Indica que é um teste Spring Boot.
// RANDOM_PORT inicia o servidor em uma porta aleatória, isolando testes.
@ActiveProfiles("test") // Ativa o perfil "test", garantindo que application-test.properties seja usado.
@ContextConfiguration(classes = CompliCheckApplication.class) // Especifica qual é a classe principal da aplicação.
// Isso garante que o contexto correto seja carregado.
public class CucumberTestRunner {
}