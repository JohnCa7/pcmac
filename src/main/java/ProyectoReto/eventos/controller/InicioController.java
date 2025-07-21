package ProyectoReto.eventos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/eventos")
    public String eventos() {
        return "eventos/index";
    }
}
