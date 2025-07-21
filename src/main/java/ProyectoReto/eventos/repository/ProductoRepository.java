package ProyectoReto.eventos.repository;

import ProyectoReto.eventos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByStockLessThanEqual(int cantidad);

}
