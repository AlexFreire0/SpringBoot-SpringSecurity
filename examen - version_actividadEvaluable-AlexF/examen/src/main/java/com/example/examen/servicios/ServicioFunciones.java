package com.example.examen.servicios;

import com.example.examen.modelo.Funcion;
import com.example.examen.repositorios.FuncionesRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioFunciones {

    private final FuncionesRepositorio funcionesRepositorio;

    // Aquí la inyección del repositorio es automática.
    // no hace falta @Autowired
    public ServicioFunciones(FuncionesRepositorio funcionesRepositorio) {
        this.funcionesRepositorio = funcionesRepositorio;
    }

    public Optional<Funcion> buscarPorId(int id) {
        return funcionesRepositorio.findById(id);
    }

    public List<Funcion> listarFunciones() {
        return funcionesRepositorio.findAll();
    }

    public List<Funcion> listarFuncionesPosteriores() {
        return funcionesRepositorio.listarFuncionesPosteriores();
    }


    public Funcion guardarFuncion(Funcion funcion) {
        return funcionesRepositorio.save(funcion);
    }

    public int obtenerSiguienteId() {
        return funcionesRepositorio.obtenerSiguienteId();
    }

    public List<Object[]> totalEntradasVendidasPorFuncion() {
        return funcionesRepositorio.totalEntradasVendidasPorFuncion();
    }

    public void eliminarFuncion(int id) {
        funcionesRepositorio.deleteById(id);
    }

    public List<Object[]> resumenPorObra() {
        return funcionesRepositorio.resumenPorObra();
    }


}
