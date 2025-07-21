package ProyectoReto.eventos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.Pattern;
@Entity
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;


    @Pattern(regexp = "^09\\d{8}$", message = "Número de teléfono ecuatoriano inválido")
    private String telefono;

    @Pattern(regexp = "^\\d{10}$", message = "La cédula debe tener 10 dígitos")
    private String cedula;
}