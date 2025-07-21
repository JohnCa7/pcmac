package ProyectoReto.eventos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 20, message = "Debe tener entre 3 y 20 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private boolean habilitado;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;
}

