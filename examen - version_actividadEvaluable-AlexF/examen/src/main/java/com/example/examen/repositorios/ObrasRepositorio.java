package com.example.examen.repositorios;

import com.example.examen.modelo.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ObrasRepositorio extends JpaRepository<Obra, Integer> {

    @Query("SELECT COALESCE(MAX(o.id), 0) + 1 FROM Obra o")
    int obtenerSiguienteId();

}
