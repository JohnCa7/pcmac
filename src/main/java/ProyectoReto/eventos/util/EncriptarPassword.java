package ProyectoReto.eventos.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncriptarPassword {

    public static void main(String[] args) {
        var password = "admin123"; // Cambia esta contraseña si quieres otra
        System.out.println("Contraseña original: " + password);
        System.out.println("Contraseña encriptada: " + encriptarPassword(password));
    }

    public static String encriptarPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
