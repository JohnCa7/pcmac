package ProyectoReto.eventos.controller;

import ProyectoReto.eventos.model.Cliente;
import ProyectoReto.eventos.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepo;

    @GetMapping
    public String listarClientes(Model model) {
        model.addAttribute("clientes", clienteRepo.findAll());
        return "clientes/index";
    }

    @GetMapping("/nuevo")
    public String nuevoClienteForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@Valid @ModelAttribute Cliente cliente, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "clientes/formulario";
        }
        clienteRepo.save(cliente);
        return "redirect:/clientes";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Integer id) {
        clienteRepo.deleteById(id);
        return "redirect:/clientes";
    }
}
