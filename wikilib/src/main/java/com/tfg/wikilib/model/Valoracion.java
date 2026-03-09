package com.tfg.wikilib.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "valoracion", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "entrada_id", "usuario_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la entrada (antes era con Coleccion)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_id", nullable = false)
    private Entrada entrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public enum Tipo {
        LIKE, DISLIKE
    }
}