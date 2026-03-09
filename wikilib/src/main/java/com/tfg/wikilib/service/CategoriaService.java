package com.tfg.wikilib.service;

import com.tfg.wikilib.model.Categoria;
import com.tfg.wikilib.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Obtener todas las categorías
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    // Obtener categoría por ID
    public Categoria obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
    }
}