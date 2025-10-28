package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.AuthModel;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthService extends APIService {
    final AuthModel authModel = new AuthModel();
    public final Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers()
            .create();
    public Response response;

    public void setNewUser() {
        authModel.setNome("Teste automatizado");
        authModel.setEmail("teste" + System.currentTimeMillis() + "@email.com");
        authModel.setSenha("123456");
        authModel.setRole("USER");
    }

    public void setInvalidUser() {
        authModel.setEmail("user@email.com");
        authModel.setSenha("senha_errada");
    }

    public void createAuthentication(String endpoint) {
        response = post(endpoint, authModel);
    }

    public void createUser(Map<String, String> dadosUsuario) {
        authModel.setNome(dadosUsuario.get("nome"));
        authModel.setEmail(dadosUsuario.get("email"));
        authModel.setSenha(dadosUsuario.get("senha"));
        authModel.setRole(dadosUsuario.get("role"));
    }

    public void setContract(String contract) throws IOException {
        switch (contract) {
            case "Cadastro ok de usuário" -> jsonSchema = loadJsonFromFile(schemasPath + "cadastro-ok-usuario.json");
            default -> throw new IllegalStateException("Unexpected contract" + contract);
        }
    }
}
