package com.tfg.wikilib.controller;

import com.tfg.wikilib.model.Entrada;
import com.tfg.wikilib.model.Usuario;
import com.tfg.wikilib.service.EntradaService;
import com.tfg.wikilib.service.CategoriaService;
import com.tfg.wikilib.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EntradaService entradaService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;

    public AdminController(EntradaService entradaService, CategoriaService categoriaService,
            UsuarioService usuarioService) {
        this.entradaService = entradaService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;
    }

    // Redirigir /admin al listado de entradas
    @GetMapping
    public String adminRoot() {
        return "redirect:/admin/entradas";
    }

    // Listado de TODAS las entradas publicadas con filtros
    @GetMapping("/entradas")
    public String listarEntradas(@RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false, defaultValue = "recientes") String orden,
            Model model) {

        List<Entrada> entradas;

        if (categoriaId != null) {
            entradas = entradaService.obtenerPorCategoria(categoriaId);
            model.addAttribute("categoriaSeleccionada", categoriaId);
        } else {
            entradas = entradaService.obtenerPublicadas();
        }

        if ("mas-valoradas".equals(orden)) {
            entradas = entradas.stream()
                    .sorted(Comparator.comparingInt(Entrada::getValoracion).reversed())
                    .toList();
        } else if ("menos-valoradas".equals(orden)) {
            entradas = entradas.stream()
                    .sorted(Comparator.comparingInt(Entrada::getValoracion))
                    .toList();
        }

        model.addAttribute("entradas", entradas);
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        model.addAttribute("orden", orden);

        return "admin/entradas";
    }

    // Eliminar cualquier entrada (acción de admin)
    @PostMapping("/entrada/{id}/eliminar")
    public String eliminarEntrada(@PathVariable Long id) {
        try {
            entradaService.eliminarEntrada(id);
        } catch (Exception e) {
            System.out.println("Error al eliminar entrada: " + e.getMessage());
        }
        return "redirect:/admin/entradas";
    }

    // Mostrar formulario para registrar un nuevo administrador
    @GetMapping("/registro")
    public String mostrarRegistroAdmin(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin/registro-admin";
    }

    // Procesar el registro de un nuevo administrador
    @PostMapping("/registro")
    public String registrarAdmin(@ModelAttribute Usuario usuario, Model model) {
        try {
            // Se fuerza el rol a ADMIN para evitar manipulación externa
            usuario.setRol(Usuario.Rol.ADMIN);
            usuarioService.registrarUsuario(usuario);
            return "redirect:/admin/entradas";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/registro-admin";
        }
    }

}