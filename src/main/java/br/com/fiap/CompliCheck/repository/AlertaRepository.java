package br.com.fiap.CompliCheck.repository;


import br.com.fiap.CompliCheck.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
}

