package com.example.werewolf.controller;

import com.example.werewolf.model.Player;
import com.example.werewolf.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private PlayerRepository playerRepository;

    @PostMapping("/players")
    public Player addPlayer(@RequestBody Player player) {
        return playerRepository.save(player);
    }

    @GetMapping("/players")
    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable Long id) {
        playerRepository.deleteById(id);
    }

    @PutMapping("/players/{id}")
    public Player updatePlayer(@PathVariable Long id, @RequestBody Player updatedPlayer) {
        return playerRepository.findById(id)
            .map(player -> {
                player.setName(updatedPlayer.getName());
                player.setRole(updatedPlayer.getRole());
                player.setAlive(updatedPlayer.isAlive());
                return playerRepository.save(player);
            })
            .orElseGet(() -> {
                updatedPlayer.setId(id);
                return playerRepository.save(updatedPlayer);
            });
    }
}
