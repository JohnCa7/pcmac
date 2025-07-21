package ProyectoReto.eventos.repository;

import ProyectoReto.eventos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}
