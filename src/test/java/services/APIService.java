package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.AuthModel;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class APIService {

    private static final String BASE_URL = "http://localhost:8080";
    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private String token;
    public Response response;

    String schemasPath = "src/test/resources/schemas/";
    JSONObject jsonSchema;
    private final ObjectMapper mapper = new ObjectMapper();


    public void authenticate() {
        AuthModel admin = new AuthModel();
        admin.setEmail("pedro@email.com");
        admin.setSenha("123456");

        String url = BASE_URL + "/auth/login";
        String bodyToSend = gson.toJson(admin);

        response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(bodyToSend)
                .when()
                .post(url)
                .then()
                .extract()
                .response();

        if (response.statusCode() == 200 && response.jsonPath().get("token") != null) {
            token = response.jsonPath().getString("token");
        } else {
            throw new RuntimeException("Falha ao autenticar: " + response.asPrettyString());
        }
    }

    public String getToken() {
        if (token == null) {
            authenticate();
        }
        return token;
    }

    public Response post(String endpoint, Object body) {
        String url = BASE_URL + endpoint;

        response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken())
                .body(body)
                .when()
                .post(url)
                .then()
                .extract()
                .response();

        return response;
    }

    public Response get(String endpoint) {
        String url = BASE_URL + endpoint;

        response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + getToken())
                .when()
                .get(url)
                .then()
                .extract()
                .response();

        return response;
    }

    public JSONObject loadJsonFromFile(String filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            JSONTokener tokener = new org.json.JSONTokener(inputStream);
            return new JSONObject(tokener);
        }
    }

    public Set<ValidationMessage> validateResponseAgainstSchema() throws IOException {
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema schema = schemaFactory.getSchema(jsonSchema.toString());
        JsonNode jsonResponseNode = mapper.readTree(jsonResponse.toString());
        return schema.validate(jsonResponseNode);
    }
}
