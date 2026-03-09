package com.tfg.wikilib.service;

import com.tfg.wikilib.model.Categoria;
import com.tfg.wikilib.model.Entrada;
import com.tfg.wikilib.model.Usuario;
import com.tfg.wikilib.repository.EntradaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EntradaService {

    private final EntradaRepository entradaRepository;
    private final CategoriaService categoriaService;

    public EntradaService(EntradaRepository entradaRepository, CategoriaService categoriaService) {
        this.entradaRepository = entradaRepository;
        this.categoriaService = categoriaService;
    }

    // Obtener todas las entradas publicadas
    public List<Entrada> obtenerPublicadas() {
        return entradaRepository.findByEstadoOrderByFechaPublicacionDesc(Entrada.Estado.PUBLICADO);
    }

    // Buscar entradas publicadas por título
    public List<Entrada> buscarPorTitulo(String titulo) {
        return entradaRepository.findByEstadoAndTituloContainingIgnoreCaseOrderByFechaPublicacionDesc(
                Entrada.Estado.PUBLICADO, titulo);
    }

    // Obtener entradas publicadas de una categoría
    public List<Entrada> obtenerPorCategoria(Long categoriaId) {
        Categoria categoria = categoriaService.obtenerPorId(categoriaId);
        return entradaRepository.findByEstadoAndCategoriaOrderByFechaPublicacionDesc(
                Entrada.Estado.PUBLICADO, categoria);
    }

    // Obtener todas las entradas de un usuario
    public List<Entrada> obtenerDeUsuario(Usuario usuario) {
        return entradaRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
    }

    // Obtener entrada por ID
    public Entrada obtenerPorId(Long id) {
        return entradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada con ID: " + id));
    }

    // Crear entrada y publicarla directamente
    @Transactional
    public Entrada crearEntrada(Entrada entrada) {
        if (entrada.getTitulo() == null || entrada.getTitulo().trim().isEmpty()) {
            throw new RuntimeException("El titulo es obligatorio");
        }
        if (entrada.getContenido() == null || entrada.getContenido().trim().isEmpty()) {
            throw new RuntimeException("El contenido es obligatorio");
        }
        if (entrada.getCategoria() == null) {
            throw new RuntimeException("La categoria es obligatoria");
        }
        // Se publica directamente al crear
        entrada.publicar();
        return entradaRepository.save(entrada);
    }

    // Eliminar entrada (redactor: solo la suya)
    @Transactional
    public void eliminarEntradaDeUsuario(Long id, Usuario usuario) {
        Entrada entrada = obtenerPorId(id);
        if (!entrada.esPropietario(usuario)) {
            throw new RuntimeException("No tienes permiso para eliminar esta entrada");
        }
        entradaRepository.delete(entrada);
    }

    // Eliminar entrada (admin: cualquiera)
    @Transactional
    public void eliminarEntrada(Long id) {
        Entrada entrada = obtenerPorId(id);
        entradaRepository.delete(entrada);
    }
}