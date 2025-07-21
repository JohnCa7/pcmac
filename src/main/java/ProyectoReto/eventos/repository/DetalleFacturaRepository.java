package ProyectoReto.eventos.repository;

import ProyectoReto.eventos.model.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Integer> {
}
