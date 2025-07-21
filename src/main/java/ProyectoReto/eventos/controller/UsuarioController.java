package ProyectoReto.eventos.controller;

import ProyectoReto.eventos.model.Usuario;
import ProyectoReto.eventos.repository.RolRepository;
import ProyectoReto.eventos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Solo ADMIN puede acceder
    @Secured("ROLE_ADMIN")
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolRepo.findAll());
        return "usuarios/formulario";
    }

    // Solo ADMIN puede guardar
    @Secured("ROLE_ADMIN")
    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute Usuario usuario, BindingResult result, Model model) {
        if (usuarioRepo.findByUsername(usuario.getUsername()) != null) {
            result.rejectValue("username", "error.usuario", "El nombre de usuario ya existe");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", rolRepo.findAll());
            return "usuarios/formulario";
        }

        usuario.setHabilitado(true);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // encriptar
        usuarioRepo.save(usuario);
        return "redirect:/usuarios/lista";
    }

    // Cualquier usuario autenticado puede ver la lista
    @GetMapping("/lista")
    public String mostrarListaUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepo.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista";
    }
    @Secured("ROLE_ADMIN")
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Integer id) {
        usuarioRepo.deleteById(id);
        return "redirect:/usuarios/lista";
    }

}
