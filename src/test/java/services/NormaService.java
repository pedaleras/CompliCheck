package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import model.NormaModel;

public class NormaService extends APIService {
    NormaModel normaModel = new NormaModel();

    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers()
            .create();

    public Response response;


    public void criarNormaIncompleta() {
        // deixando descrição vazia para gerar erro
        normaModel.setCategoria("ambiental");
        normaModel.setDataLimite("2025-12-31");
        normaModel.setEmpresaId(1L);
    }

    public void createNorma(String endpoint) {
        String bodyToSend = gson.toJson(normaModel);
        response = post(endpoint, bodyToSend);
    }
}
