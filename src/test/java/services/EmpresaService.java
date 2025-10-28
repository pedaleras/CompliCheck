package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import model.EmpresaModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;

public class EmpresaService extends APIService {

    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers()
            .create();

    public Response response;
    public EmpresaModel empresaModel;

    public void setEmpresaValida() {
        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        String timestamp = agora.format(formatter);

        empresaModel = new EmpresaModel();
        empresaModel.setNome("Empresa Teste_"+timestamp);
        empresaModel.setCnpj(timestamp);
        empresaModel.setSetor("indústria");
    }

    public void createEmpresa(String endpoint) {
        String bodyToSend = gson.toJson(empresaModel);
        response = post(endpoint, bodyToSend);
    }

    public void getEmpresas(String endpoint) {
        response = get(endpoint);
    }
}
