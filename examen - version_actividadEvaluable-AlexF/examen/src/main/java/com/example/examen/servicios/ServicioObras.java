package com.example.examen.servicios;

import com.example.examen.modelo.Obra;
import com.example.examen.repositorios.ObrasRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioObras {

    private final ObrasRepositorio obrasRepositorio;

    // Aquí la inyección del repositorio es automática.
    // no hace falta @Autowired
    public ServicioObras(ObrasRepositorio obrasRepositorio) {
        this.obrasRepositorio = obrasRepositorio;
    }

    public Optional<Obra> buscarPorId(int id) {
        return obrasRepositorio.findById(id);
    }

    public List<Obra> listarObras() {
        return obrasRepositorio.findAll();
    }

    public Obra guardarObra(Obra obra) {
        return obrasRepositorio.save(obra);
    }

    public int obtenerSiguienteId() {
        return obrasRepositorio.obtenerSiguienteId();
    }

    public void eliminarObra(int id) {
        obrasRepositorio.deleteById(id);
    }
}
