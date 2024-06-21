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
import java.util.Optional;

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
                procesarVotosLobos(partida);
                partida.setFase("Día");
            } else if ("Día".equals(partida.getFase())) {
                procesarVotosAldeanos(partida);
                partida.setFase("Noche");
            }
            partidaRepository.save(partida);
        }
        return partida;
    }

    @PostMapping("/partidas/{id}/voto-aldeano")
    public Partida votarAldeano(@PathVariable Long id, @RequestBody VotoRequest votoRequest) {
        Partida partida = partidaRepository.findById(id).orElse(null);
        if (partida != null && "Día".equals(partida.getFase())) {
            Optional<Jugador> jugadorOpt = jugadorRepository.findById(votoRequest.getJugadorId());
            if (jugadorOpt.isPresent()) {
                Jugador jugador = jugadorOpt.get();
                if (jugador.isAlive()) {
                    jugador.setVotos(jugador.getVotos() + 1);
                    jugadorRepository.save(jugador);
                }
            }
        }
        return partida;
    }

    @PostMapping("/partidas/{id}/voto-lobo")
    public Partida votarLobo(@PathVariable Long id, @RequestBody VotoRequest votoRequest) {
        Partida partida = partidaRepository.findById(id).orElse(null);
        if (partida != null && "Noche".equals(partida.getFase())) {
            Optional<Jugador> jugadorOpt = jugadorRepository.findById(votoRequest.getJugadorId());
            if (jugadorOpt.isPresent()) {
                Jugador jugador = jugadorOpt.get();
                if (jugador.isAlive()) {
                    jugador.setVotos(jugador.getVotos() + 1);
                    jugadorRepository.save(jugador);
                }
            }
        }
        return partida;
    }

    @PostMapping("/partidas/{id}/voto-vidente")
    public String votarVidente(@PathVariable Long id, @RequestBody VotoRequest votoRequest) {
        Partida partida = partidaRepository.findById(id).orElse(null);
        if (partida != null && "Noche".equals(partida.getFase())) {
            Optional<Jugador> jugadorOpt = jugadorRepository.findById(votoRequest.getJugadorId());
            if (jugadorOpt.isPresent()) {
                Jugador jugador = jugadorOpt.get();
                if (jugador.isAlive()) {
                    return jugador.getPersonaje().getTipo();
                } else {
                    return "El jugador está muerto";
                }
            }
        }
        return "Voto no válido";
    }

    @PostMapping("/partidas/{id}/procesar-votos")
    public Partida procesarVotos(@PathVariable Long id) {
        Partida partida = partidaRepository.findById(id).orElse(null);
        if (partida != null) {
            procesarVotosAldeanos(partida);
            procesarVotosLobos(partida);
        }
        return partida;
    }

    private void procesarVotosLobos(Partida partida) {
        List<Jugador> jugadores = partida.getJugadores();
        Jugador jugadorEliminado = jugadores.stream()
                .filter(Jugador::isAlive)
                .max((j1, j2) -> Integer.compare(j1.getVotos(), j2.getVotos()))
                .orElse(null);
        if (jugadorEliminado != null) {
            jugadorEliminado.setAlive(false);
            jugadorEliminado.setVotos(0); // Reiniciar votos después de la eliminación
            jugadorRepository.save(jugadorEliminado);
        }
        // Reiniciar votos de todos los jugadores después de procesar
        jugadores.forEach(j -> {
            j.setVotos(0);
            jugadorRepository.save(j);
        });
    }

    private void procesarVotosAldeanos(Partida partida) {
        List<Jugador> jugadores = partida.getJugadores();
        Jugador jugadorEliminado = jugadores.stream()
                .filter(Jugador::isAlive)
                .max((j1, j2) -> Integer.compare(j1.getVotos(), j2.getVotos()))
                .orElse(null);
        if (jugadorEliminado != null) {
            jugadorEliminado.setAlive(false);
            jugadorEliminado.setVotos(0); // Reiniciar votos después de la eliminación
            jugadorRepository.save(jugadorEliminado);
        }
        // Reiniciar votos de todos los jugadores después de procesar
        jugadores.forEach(j -> {
            j.setVotos(0);
            jugadorRepository.save(j);
        });
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

class VotoRequest {
    private Long jugadorId;

    public Long getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(Long jugadorId) {
        this.jugadorId = jugadorId;
    }
}
