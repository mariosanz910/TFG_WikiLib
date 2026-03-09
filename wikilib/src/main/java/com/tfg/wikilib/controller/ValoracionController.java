package com.tfg.wikilib.controller;

import com.tfg.wikilib.model.Usuario;
import com.tfg.wikilib.service.ValoracionService;
import com.tfg.wikilib.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/valoracion")
public class ValoracionController {

    private final ValoracionService valoracionService;
    private final UsuarioService usuarioService;

    public ValoracionController(ValoracionService valoracionService, UsuarioService usuarioService) {
        this.valoracionService = valoracionService;
        this.usuarioService = usuarioService;
    }

    // Dar like a una entrada
    @PostMapping("/like/{entradaId}")
    public String darLike(@PathVariable Long entradaId, Authentication authentication) {
        try {
            Usuario usuario = usuarioService.buscarPorNombreUsuario(authentication.getName());
            valoracionService.darLike(entradaId, usuario);
        } catch (Exception e) {
            System.out.println("Error al dar like: " + e.getMessage());
        }
        return "redirect:/entrada/" + entradaId;
    }

    // Dar dislike a una entrada
    @PostMapping("/dislike/{entradaId}")
    public String darDislike(@PathVariable Long entradaId, Authentication authentication) {
        try {
            Usuario usuario = usuarioService.buscarPorNombreUsuario(authentication.getName());
            valoracionService.darDislike(entradaId, usuario);
        } catch (Exception e) {
            System.out.println("Error al dar dislike: " + e.getMessage());
        }
        return "redirect:/entrada/" + entradaId;
    }
}