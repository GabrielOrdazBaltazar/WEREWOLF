package com.example.werewolf.model;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Jugador> jugadores;

    private String fase; // "Día" o "Noche"

    @ElementCollection
    private Map<Long, Integer> votosAldeanos = new HashMap<>(); // Jugador ID -> Número de votos

    @ElementCollection
    private Map<Long, Integer> votosLobos = new HashMap<>(); // Jugador ID -> Número de votos

    @Transient
    private Long videnteVoto; // Jugador ID a quien la vidente quiere ver el rol

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public Map<Long, Integer> getVotosAldeanos() {
        return votosAldeanos;
    }

    public void setVotosAldeanos(Map<Long, Integer> votosAldeanos) {
        this.votosAldeanos = votosAldeanos;
    }

    public Map<Long, Integer> getVotosLobos() {
        return votosLobos;
    }

    public void setVotosLobos(Map<Long, Integer> votosLobos) {
        this.votosLobos = votosLobos;
    }

    public Long getVidenteVoto() {
        return videnteVoto;
    }

    public void setVidenteVoto(Long videnteVoto) {
        this.videnteVoto = videnteVoto;
    }
}
