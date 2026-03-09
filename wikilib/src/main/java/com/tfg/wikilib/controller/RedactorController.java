package com.tfg.wikilib.controller;

import com.tfg.wikilib.model.Entrada;
import com.tfg.wikilib.model.Usuario;
import com.tfg.wikilib.service.EntradaService;
import com.tfg.wikilib.service.UsuarioService;
import com.tfg.wikilib.service.CategoriaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/redactor")
public class RedactorController {

    private final EntradaService entradaService;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;

    public RedactorController(EntradaService entradaService,
            UsuarioService usuarioService,
            CategoriaService categoriaService) {
        this.entradaService = entradaService;
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
    }

    // Página de normas
    @GetMapping("/normas")
    public String verNormas() {
        return "redactor/normas";
    }

    // Panel principal: lista de entradas del redactor
    @GetMapping("/panel")
    public String panel(Authentication authentication, Model model) {
        Usuario usuario = usuarioService.buscarPorNombreUsuario(authentication.getName());
        List<Entrada> entradas = entradaService.obtenerDeUsuario(usuario);

        model.addAttribute("entradas", entradas);
        model.addAttribute("usuario", usuario);

        return "redactor/panel";
    }

    // Formulario para crear nueva entrada
    @GetMapping("/entrada/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("entrada", new Entrada());
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        return "redactor/crear-entrada";
    }

    // Procesar creación de entrada (se publica directamente)
    @PostMapping("/entrada/crear")
    public String crearEntrada(@ModelAttribute Entrada entrada,
            @RequestParam Long categoriaId,
            Authentication authentication) {
        try {
            Usuario usuario = usuarioService.buscarPorNombreUsuario(authentication.getName());
            entrada.setUsuario(usuario);
            entrada.setCategoria(categoriaService.obtenerPorId(categoriaId));
            entradaService.crearEntrada(entrada);
        } catch (Exception e) {
            System.out.println("Error al crear entrada: " + e.getMessage());
        }
        return "redirect:/redactor/panel";
    }

    // Eliminar entrada propia
    @PostMapping("/entrada/{id}/eliminar")
    public String eliminarEntrada(@PathVariable Long id, Authentication authentication) {
        try {
            Usuario usuario = usuarioService.buscarPorNombreUsuario(authentication.getName());
            entradaService.eliminarEntradaDeUsuario(id, usuario);
        } catch (Exception e) {
            System.out.println("Error al eliminar entrada: " + e.getMessage());
        }
        return "redirect:/redactor/panel";
    }
}