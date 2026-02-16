package com.example.examen.repositorios;

import com.example.examen.modelo.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EntradasRepositorio extends JpaRepository<Entrada, Integer> {

    // COALESCE evita NULL cuando la tabla está vacía y permite empezar en 1
    @Query("SELECT COALESCE(MAX(e.id), 0) + 1 FROM Entrada e")
    int obtenerSiguienteId();
}