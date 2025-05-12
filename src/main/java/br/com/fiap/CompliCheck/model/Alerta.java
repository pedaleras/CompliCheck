package br.com.fiap.CompliCheck.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "TBL_CC_ALERTA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    private LocalDate dataVerificacao;

    @ManyToOne
    @JoinColumn(name = "norma_id")
    private Norma norma;
}

