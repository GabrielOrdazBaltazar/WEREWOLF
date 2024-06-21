package com.example.werewolf.controller;

import com.example.werewolf.model.Jugador;
import com.example.werewolf.model.Partida;
import com.example.werewolf.model.Personaje;
import com.example.werewolf.repository.JugadorRepository;
import com.example.werewolf.repository.PartidaRepository;
import com.example.werewolf.repository.PersonajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private PersonajeRepository personajeRepository;

    @Autowired
    private PartidaRepository partidaRepository;

    @PostMapping("/partidas")
    public Partida createPartida(@RequestBody List<String> nombres) {
        List<Jugador> jugadores = new ArrayList<>();
        List<Personaje> personajes = asignarRoles(nombres.size());

        for (int i = 0; i < nombres.size(); i++) {
            Jugador jugador = new Jugador();
            jugador.setName(nombres.get(i));
            jugador.setPersonaje(personajes.get(i));
            jugadores.add(jugadorRepository.save(jugador));
        }

        Partida partida = new Partida();
        partida.setJugadores(jugadores);
        partida.setFase("Noche");
        return partidaRepository.save(partida);
    }

    @GetMapping("/partidas/{id}")
    public Partida getPartida(@PathVariable Long id) {
        return partidaRepository.findById(id).orElse(null);
    }

    @PostMapping("/partidas/{id}/fase")
    public Partida cambiarFase(@PathVariable Long id) {
        Partida partida = partidaRepository.findById(id).orElse(null);
        if (partida != null) {
            if ("Noche".equals(partida.getFase())) {
                partida.setFase("Día");
            } else {
                partida.setFase("Noche");
            }
            partidaRepository.save(partida);
        }
        return partida;
    }

    private List<Personaje> asignarRoles(int numeroDeJugadores) {
        List<Personaje> personajes = new ArrayList<>();
        int numeroDeLobos = 2;
        int numeroDeVidentes = 1;
        int numeroDeAldeanos = numeroDeJugadores - numeroDeLobos - numeroDeVidentes;

        for (int i = 0; i < numeroDeLobos; i++) {
            Personaje lobo = new Personaje();
            lobo.setTipo("Lobo");
            personajes.add(personajeRepository.save(lobo));
        }

        for (int i = 0; i < numeroDeVidentes; i++) {
            Personaje vidente = new Personaje();
            vidente.setTipo("Vidente");
            personajes.add(personajeRepository.save(vidente));
        }

        for (int i = 0; i < numeroDeAldeanos; i++) {
            Personaje aldeano = new Personaje();
            aldeano.setTipo("Aldean");
            personajes.add(personajeRepository.save(aldeano));
        }

        return personajes;
    }
}
