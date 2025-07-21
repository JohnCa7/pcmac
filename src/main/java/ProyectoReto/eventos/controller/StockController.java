package ProyectoReto.eventos.controller;

import ProyectoReto.eventos.model.Producto;
import ProyectoReto.eventos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/ventas/stock")
public class StockController {

    @Autowired
    private ProductoRepository productoRepo;

    @GetMapping
    public String verStock(Model model) {
        model.addAttribute("productos", productoRepo.findAll());
        return "ventas/stock";
    }

    @GetMapping("/editar/{id}")
    public String editarStock(@PathVariable Integer id, Model model) {
        Producto producto = productoRepo.findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/ventas/stock";
        }
        model.addAttribute("producto", producto);
        return "ventas/formulario_stock";
    }

    @PostMapping("/actualizar")
    public String actualizarStock(@ModelAttribute Producto productoActualizado) {
        Producto producto = productoRepo.findById(productoActualizado.getId()).orElse(null);
        if (producto != null) {
            producto.setStock(productoActualizado.getStock());
            productoRepo.save(producto);
        }
        return "redirect:/ventas/stock";
    }
}