package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayersController {
    private final PlayersService playersService;

    public PlayersController(PlayersService playersService) {
        this.playersService = playersService;
    }

    @GetMapping("/players")
    public List<Player> getPlayersList(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "title", required = false) String title,
                                       @RequestParam(value = "race", required = false) Race race,
                                       @RequestParam(value = "profession", required = false) Profession profession,
                                       @RequestParam(value = "after", required = false) Long after,
                                       @RequestParam(value = "before", required = false) Long before,
                                       @RequestParam(value = "banned", required = false) Boolean banned,
                                       @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                       @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                       @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                       @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                       @RequestParam(defaultValue = "ID", value = "order") PlayerOrder order,
                                       @RequestParam(defaultValue = "0", value = "pageNumber") Integer pageNumber,
                                       @RequestParam(defaultValue = "3", value = "pageSize") Integer pageSize) {
        List<Player> allList = playersService.getFilteredList(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel);
        return playersService.getSortedList(allList, order, pageNumber, pageSize);
    }

    @GetMapping("/players/count")
    public Integer getPlayersCount(@RequestParam(value = "name", required = false) String name,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "race", required = false) Race race,
                                   @RequestParam(value = "profession", required = false) Profession profession,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "banned", required = false) Boolean banned,
                                   @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                   @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                   @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                   @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {
        List<Player> countList = playersService.getFilteredList(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel);
        return countList.size();
    }

    @PostMapping("/players")
    public Player createPlayer(@RequestBody Player player) {
        return playersService.createPlayer(player);
    }

    @GetMapping("/players/{id}")
    public Player getPlayerById(@PathVariable String id) {
        return playersService.getPlayer(id);
    }

    @PostMapping("/players/{id}")
    public Player updatePlayer(@PathVariable String id,
                               @RequestBody Player player) {
        if (player.getName() == null
                && player.getTitle() == null
                && player.getRace() == null
                && player.getProfession() == null
                && player.getBirthday() == null
                && player.getExperience() == null)
            return playersService.getPlayer(id);
        return playersService.updatePlayer(player,playersService.getPlayer(id));
    }

    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable String id){
        playersService.deletePlayer(playersService.getPlayer(id));
    }
}


