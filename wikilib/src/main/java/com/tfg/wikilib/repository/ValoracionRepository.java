package com.tfg.wikilib.repository;

import com.tfg.wikilib.model.Valoracion;
import com.tfg.wikilib.model.Entrada;
import com.tfg.wikilib.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    // Buscar la valoración de un usuario en una entrada concreta
    Optional<Valoracion> findByEntradaAndUsuario(Entrada entrada, Usuario usuario);

    // Verificar si un usuario ya votó una entrada
    boolean existsByEntradaAndUsuario(Entrada entrada, Usuario usuario);
}