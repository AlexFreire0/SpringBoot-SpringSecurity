package com.example.examen.repositorios;

import com.example.examen.modelo.Funcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuncionesRepositorio extends JpaRepository<Funcion, Integer> {

    // COALESCE evita NULL cuando la tabla está vacía y permite empezar en 1
    @Query("SELECT COALESCE(MAX(f.id), 0) + 1 FROM Funcion f")
    int obtenerSiguienteId();

    @Query("""
            SELECT f.fecha, f.hora, f.precio, f.obra.titulo, sum(e.cantidad), f.id
            FROM Funcion f LEFT JOIN Entrada e ON f.id = e.funcion.id
            GROUP BY f.id, f.fecha, f.hora, f.precio, f.obra.titulo
            """)
    List<Object[]> totalEntradasVendidasPorFuncion();

    @Query("""
    SELECT f
    FROM Funcion f
    JOIN f.obra o
    WHERE f.fecha > CURRENT_DATE
    ORDER BY f.fecha, f.hora
""")

    List<Funcion> listarFuncionesPosteriores();
    @Query("""
    SELECT o.titulo, SUM(e.cantidad), SUM(e.cantidad * f.precio)
    FROM Obra o 
    LEFT JOIN Funcion f ON o.id = f.obra.id
    LEFT JOIN Entrada e ON f.id = e.funcion.id
    GROUP BY o.id, o.titulo
    ORDER BY o.titulo
    """)
    List<Object[]> resumenPorObra();


}
