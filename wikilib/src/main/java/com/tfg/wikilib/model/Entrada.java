package com.tfg.wikilib.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "entrada")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String contenido;

    // Relación directa con el usuario que creó la entrada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Relación con la categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Estado de la entrada (borrador o publicada)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Estado estado = Estado.BORRADOR;

    // Contador de likes - dislikes
    @Column(nullable = false)
    @Builder.Default
    private Integer valoracion = 0;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    public enum Estado {
        BORRADOR, PUBLICADO
    }

    // Publica la entrada y registra la fecha
    public void publicar() {
        this.estado = Estado.PUBLICADO;
        this.fechaPublicacion = LocalDateTime.now();
    }

    // Verifica si un usuario es el propietario de esta entrada
    public boolean esPropietario(Usuario otroUsuario) {
        return this.usuario != null && this.usuario.getId().equals(otroUsuario.getId());
    }
}