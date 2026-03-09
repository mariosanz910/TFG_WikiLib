package com.tfg.wikilib.service;

import com.tfg.wikilib.model.Valoracion;
import com.tfg.wikilib.model.Entrada;
import com.tfg.wikilib.model.Usuario;
import com.tfg.wikilib.repository.ValoracionRepository;
import com.tfg.wikilib.repository.EntradaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final EntradaRepository entradaRepository;

    public ValoracionService(ValoracionRepository valoracionRepository,
            EntradaRepository entradaRepository) {
        this.valoracionRepository = valoracionRepository;
        this.entradaRepository = entradaRepository;
    }

    // Dar like a una entrada
    @Transactional
    public void darLike(Long entradaId, Usuario usuario) {
        Entrada entrada = entradaRepository.findById(entradaId)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada"));

        // No se puede votar en una entrada propia
        if (entrada.esPropietario(usuario)) {
            throw new RuntimeException("No puedes votar tu propia entrada");
        }

        Optional<Valoracion> valoracionExistente = valoracionRepository.findByEntradaAndUsuario(entrada, usuario);

        if (valoracionExistente.isPresent()) {
            Valoracion valoracion = valoracionExistente.get();

            if (valoracion.getTipo() == Valoracion.Tipo.LIKE) {
                // Ya dio like, no hacer nada
                return;
            } else {
                // Cambiar de DISLIKE a LIKE (+2: quitar -1 y añadir +1)
                valoracion.setTipo(Valoracion.Tipo.LIKE);
                valoracionRepository.save(valoracion);
                entrada.setValoracion(entrada.getValoracion() + 2);
                entradaRepository.save(entrada);
            }
        } else {
            // Nuevo voto LIKE
            Valoracion nuevaValoracion = Valoracion.builder()
                    .entrada(entrada)
                    .usuario(usuario)
                    .tipo(Valoracion.Tipo.LIKE)
                    .build();
            valoracionRepository.save(nuevaValoracion);
            entrada.setValoracion(entrada.getValoracion() + 1);
            entradaRepository.save(entrada);
        }
    }

    // Dar dislike a una entrada
    @Transactional
    public void darDislike(Long entradaId, Usuario usuario) {
        Entrada entrada = entradaRepository.findById(entradaId)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada"));

        // No se puede votar en una entrada propia
        if (entrada.esPropietario(usuario)) {
            throw new RuntimeException("No puedes votar tu propia entrada");
        }

        Optional<Valoracion> valoracionExistente = valoracionRepository.findByEntradaAndUsuario(entrada, usuario);

        if (valoracionExistente.isPresent()) {
            Valoracion valoracion = valoracionExistente.get();

            if (valoracion.getTipo() == Valoracion.Tipo.DISLIKE) {
                // Ya dio dislike, no hacer nada
                return;
            } else {
                // Cambiar de LIKE a DISLIKE (-2: quitar +1 y añadir -1)
                valoracion.setTipo(Valoracion.Tipo.DISLIKE);
                valoracionRepository.save(valoracion);
                entrada.setValoracion(entrada.getValoracion() - 2);
                entradaRepository.save(entrada);
            }
        } else {
            // Nuevo voto DISLIKE
            Valoracion nuevaValoracion = Valoracion.builder()
                    .entrada(entrada)
                    .usuario(usuario)
                    .tipo(Valoracion.Tipo.DISLIKE)
                    .build();
            valoracionRepository.save(nuevaValoracion);
            entrada.setValoracion(entrada.getValoracion() - 1);
            entradaRepository.save(entrada);
        }
    }

    // Obtener la valoración del usuario en una entrada
    public Optional<Valoracion> obtenerValoracionUsuario(Long entradaId, Usuario usuario) {
        Entrada entrada = entradaRepository.findById(entradaId)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada"));
        return valoracionRepository.findByEntradaAndUsuario(entrada, usuario);
    }
}