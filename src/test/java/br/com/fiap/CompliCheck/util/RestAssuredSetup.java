package br.com.fiap.CompliCheck.util;

import io.restassured.RestAssured;
import io.cucumber.java.BeforeAll;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RestAssuredSetup {

    @BeforeAll
    public static void setup() {
        Properties prop = new Properties();
        try (InputStream input = RestAssuredSetup.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Arquivo config.properties não encontrado. Usando defaults.");
                RestAssured.baseURI = "http://localhost:8080"; // Assumindo que sua API roda em 8080
            } else {
                prop.load(input);
                RestAssured.baseURI = prop.getProperty("api.base.uri", "http://localhost:8080");
                // Exemplo de como pegar um path base da API, se não for /api diretamente
                RestAssured.basePath = prop.getProperty("api.base.path", "/api"); // Adiciona /api como base path
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            RestAssured.baseURI = "http://localhost:8080"; // Fallback em caso de erro
            RestAssured.basePath = "/api";
        }
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(); // Útil para depuração
    }
}