package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

@Data
public class EmpresaModel {

    @Expose
    private String nome;

    @Expose
    private String cnpj;

    @Expose
    private String setor;
}
