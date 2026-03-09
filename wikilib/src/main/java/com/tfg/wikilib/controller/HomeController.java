package com.tfg.wikilib.controller;

import com.tfg.wikilib.model.Entrada;
import com.tfg.wikilib.model.Entrada.Estado;
import com.tfg.wikilib.model.Usuario;
import com.tfg.wikilib.model.Valoracion;
import com.tfg.wikilib.service.EntradaService;
import com.tfg.wikilib.service.CategoriaService;
import com.tfg.wikilib.service.ValoracionService;
import com.tfg.wikilib.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final EntradaService entradaService;
    private final CategoriaService categoriaService;
    private final ValoracionService valoracionService;
    private final UsuarioService usuarioService;

    public HomeController(EntradaService entradaService,
            CategoriaService categoriaService,
            ValoracionService valoracionService,
            UsuarioService usuarioService) {
        this.entradaService = entradaService;
        this.categoriaService = categoriaService;
        this.valoracionService = valoracionService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/catalogo";
    }

    // Catálogo público de entradas publicadas
    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String buscar,
            Model model) {
        List<Entrada> entradas;

        if (buscar != null && !buscar.trim().isEmpty()) {
            entradas = entradaService.buscarPorTitulo(buscar);
            model.addAttribute("buscar", buscar);
        } else if (categoriaId != null) {
            entradas = entradaService.obtenerPorCategoria(categoriaId);
            model.addAttribute("categoriaSeleccionada", categoriaId);
        } else {
            entradas = entradaService.obtenerPublicadas();
        }

        model.addAttribute("entradas", entradas);
        model.addAttribute("categorias", categoriaService.obtenerTodas());

        return "catalogo";
    }

    // Ver una entrada individual
    @GetMapping("/entrada/{id}")
    public String verEntrada(@PathVariable Long id,
            Authentication authentication,
            Model model) {
        try {
            Entrada entrada = entradaService.obtenerPorId(id);

            if (entrada.getEstado() != Estado.PUBLICADO) {
                return "redirect:/catalogo";
            }

            model.addAttribute("entrada", entrada);

            if (authentication != null && authentication.isAuthenticated()
                    && !authentication.getName().equals("anonymousUser")) {

                Usuario usuario = usuarioService.buscarPorNombreUsuario(authentication.getName());
                Optional<Valoracion> valoracionUsuario = valoracionService.obtenerValoracionUsuario(id, usuario);

                model.addAttribute("valoracionUsuario", valoracionUsuario.orElse(null));
                model.addAttribute("esPropia", entrada.esPropietario(usuario));
            }

            return "entrada/ver";

        } catch (Exception e) {
            return "redirect:/catalogo";
        }
    }
}