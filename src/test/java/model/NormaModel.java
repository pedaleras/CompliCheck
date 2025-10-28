package model;

import com.google.gson.annotations.Expose;
import lombok.Data;

@Data
public class NormaModel {

    @Expose
    private String descricao;

    @Expose
    private String categoria;

    @Expose
    private String dataLimite; // formato "YYYY-MM-DD"

    @Expose
    private Long empresaId;
}
