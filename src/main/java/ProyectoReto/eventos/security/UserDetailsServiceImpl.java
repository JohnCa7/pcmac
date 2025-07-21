package ProyectoReto.eventos.security;

import ProyectoReto.eventos.model.Usuario;
import ProyectoReto.eventos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username);
        if (usuario == null || !usuario.isHabilitado()) {
            throw new UsernameNotFoundException("Usuario no encontrado o deshabilitado");
        }


        String rol = usuario.getRol().getNombre();

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(rol))
        );
    }
}
