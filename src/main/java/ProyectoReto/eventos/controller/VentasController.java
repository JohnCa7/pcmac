package ProyectoReto.eventos.controller;

import ProyectoReto.eventos.model.Producto;
import ProyectoReto.eventos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
@Controller
@RequestMapping("/ventas")
public class VentasController {

    @Autowired
    private ProductoRepository productoRepo;

    //  Dashboard de ventas
    @GetMapping
    public String vistaVentas() {
        return "ventas/index"; // Mostrará el panel con botones
    }

    //  Listado de productos
    @GetMapping("/productos")
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoRepo.findAll());
        return "ventas/productos"; // Vista nueva con tabla de productos
    }

    //  Formulario para nuevo producto
    @GetMapping("/productos/nuevo")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "ventas/formulario";
    }

    //  Guardar producto (nuevo o editado)
    @PostMapping("/productos/guardar")
    public String guardarProducto(@Valid @ModelAttribute Producto producto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "ventas/formulario";
        }
        productoRepo.save(producto);
        return "redirect:/ventas/productos";
    }

    //  Eliminar producto
    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id) {
        productoRepo.deleteById(id);
        return "redirect:/ventas/productos";
    }




}

