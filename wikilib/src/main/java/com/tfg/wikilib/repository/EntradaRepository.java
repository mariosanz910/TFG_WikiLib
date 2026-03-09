package com.tfg.wikilib.repository;

import com.tfg.wikilib.model.Entrada;
import com.tfg.wikilib.model.Categoria;
import com.tfg.wikilib.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {

    // Todas las entradas publicadas, más recientes primero
    List<Entrada> findByEstadoOrderByFechaPublicacionDesc(Entrada.Estado estado);

    // Entradas publicadas de una categoría concreta
    List<Entrada> findByEstadoAndCategoriaOrderByFechaPublicacionDesc(Entrada.Estado estado, Categoria categoria);

    // Buscar entradas publicadas por título (ignora mayúsculas)
    List<Entrada> findByEstadoAndTituloContainingIgnoreCaseOrderByFechaPublicacionDesc(Entrada.Estado estado,
            String titulo);

    // Todas las entradas de un usuario, ordenadas por fecha de creación
    List<Entrada> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
}