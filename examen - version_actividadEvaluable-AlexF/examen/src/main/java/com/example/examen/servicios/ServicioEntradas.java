package com.example.examen.servicios;

import com.example.examen.modelo.Entrada;
import com.example.examen.repositorios.EntradasRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioEntradas {

    private final EntradasRepositorio entradasRepositorio;

    // Aquí la inyección del repositorio es automática.
    // no hace falta @Autowired
    public ServicioEntradas(EntradasRepositorio entradasRepositorio) {
        this.entradasRepositorio = entradasRepositorio;
    }

    public Optional<Entrada> buscarPorId(int id) {
        return entradasRepositorio.findById(id);
    }

    public List<Entrada> listarEntradas() {
        return entradasRepositorio.findAll();
    }

    public Entrada guardarEntrada(Entrada entrada) {
        return entradasRepositorio.save(entrada);
    }

    public int obtenerSiguienteId() {
        return entradasRepositorio.obtenerSiguienteId();
    }

    public void eliminarFuncion(int id) {
        entradasRepositorio.deleteById(id);
    }

}
