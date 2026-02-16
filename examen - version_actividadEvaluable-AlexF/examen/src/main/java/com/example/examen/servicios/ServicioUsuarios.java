package com.example.examen.servicios;

import com.example.examen.modelo.Usuario;
import com.example.examen.repositorios.UsuariosRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioUsuarios {

    private final UsuariosRepositorio usuariosRepositorio;

    // Aquí la inyección del repositorio es automática.
    // no hace falta @Autowired
    public ServicioUsuarios(UsuariosRepositorio usuariosRepositorio) {
        this.usuariosRepositorio = usuariosRepositorio;
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuariosRepositorio.findById(email);
    }

    public List<Usuario> listarUsuarios() {
        return usuariosRepositorio.findAll();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuariosRepositorio.save(usuario);
    }
}
