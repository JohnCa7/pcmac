package ProyectoReto.eventos.repository;

import ProyectoReto.eventos.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    @Query("SELECT f FROM Factura f " +
            "WHERE LOWER(CONCAT(f.cliente.nombre, ' ', f.cliente.apellido)) LIKE LOWER(CONCAT('%', :filtro, '%')) " +
            "OR str(f.id) LIKE %:filtro%")
    List<Factura> buscarPorClienteONumero(@Param("filtro") String filtro);
}

