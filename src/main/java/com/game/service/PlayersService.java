package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class PlayersService {

    private final PlayersRepository playersRepository;

    public PlayersService(PlayersRepository playersRepository) {
        this.playersRepository = playersRepository;
    }

    public List<Player> getFilteredList(String name, String title, Race race, Profession profession, Long after,
                                        Long before, Boolean banned, Integer minExperience, Integer maxExperience,
                                        Integer minLevel, Integer maxLevel) {
        List<Player> list = new ArrayList<>();
        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);

        playersRepository.findAll().forEach(player -> {
            if (name != null && !player.getName().contains(name)) return;
            if (title != null && !player.getTitle().contains(title)) return;
            if (race != null && player.getRace() != race) return;
            if (profession != null && player.getProfession() != profession) return;
            if (after != null && player.getBirthday().before(afterDate)) return;
            if (before != null && player.getBirthday().after(beforeDate)) return;
            if (banned != null && player.getBanned() != banned) return;
            if (minExperience != null && player.getExperience().compareTo(minExperience) < 0) return;
            if (maxExperience != null && player.getExperience().compareTo(maxExperience) > 0) return;
            if (minLevel != null && player.getLevel().compareTo(minLevel) < 0) return;
            if (maxLevel != null && player.getLevel().compareTo(maxLevel) > 0) return;
            list.add(player);
        });
        return list;
    }

    public List<Player> getSortedList(List<Player> filteredList, PlayerOrder order, Integer page, Integer pageCounter) {
        int pageNumber = page + 1;
        List<Player> sortedList = new ArrayList<>();
        if (order.equals(PlayerOrder.NAME))
            filteredList.sort(Comparator.comparing(Player::getName));
        else if (order.equals(PlayerOrder.EXPERIENCE))
            filteredList.sort(Comparator.comparing(Player::getExperience));
        else if (order.equals(PlayerOrder.BIRTHDAY))
            filteredList.sort(Comparator.comparing(Player::getBirthday));
        for (int i = pageNumber * pageCounter - (pageCounter - 1) - 1; i < pageCounter * pageNumber && i < filteredList.size(); i++) {
            sortedList.add(filteredList.get(i));
        }
        return sortedList;
    }

    public Player createPlayer(Player player) {
        if (player.getName() == null
                || player.getTitle() == null
                || player.getRace() == null
                || player.getProfession() == null
                || player.getBirthday() == null
                || player.getExperience() == null
                || player.getName().length() > 12
                || player.getTitle().length() > 30
                || player.getName().equals("")
                || player.getExperience() < 0
                || player.getExperience() > 10000000
                || player.getBirthday().getTime() < 0
                || player.getBirthday().before(new Date(100, Calendar.JANUARY, 1))
                || player.getBirthday().after(new Date(1100, Calendar.DECEMBER, 31))
        )
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        player.setLevel((int) (Math.sqrt((double) 2500 + 200 * player.getExperience()) - 50) / 100);
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
        return playersRepository.save(player);
    }

    public Player getPlayer(String id) {
        long i;
        try {
            i = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (i <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (playersRepository.existsById(i)) {
            return playersRepository.findById(i).get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Player updatePlayer(Player newVersion, Player oldVersion) {

        if (newVersion.getName() != null)
            oldVersion.setName(newVersion.getName());

        if (newVersion.getTitle() != null)
            oldVersion.setTitle(newVersion.getTitle());

        if (newVersion.getRace() != null)
            oldVersion.setRace(newVersion.getRace());

        if (newVersion.getProfession() != null)
            oldVersion.setProfession(newVersion.getProfession());

        if (newVersion.getBirthday() != null) {
            if (newVersion.getBirthday() == null
                    || newVersion.getBirthday().getTime() < 0
                    || newVersion.getBirthday().before(new Date(100, Calendar.JANUARY, 1))
                    || newVersion.getBirthday().after(new Date(1100, Calendar.DECEMBER, 31)))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            oldVersion.setBirthday(newVersion.getBirthday());
        }

        if (newVersion.getBanned() != null)
            oldVersion.setBanned(newVersion.getBanned());

        if (newVersion.getExperience() != null) {
            if (newVersion.getExperience() <= 0 || newVersion.getExperience() >= 10000000)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            oldVersion.setExperience(newVersion.getExperience());
        }
        oldVersion.setLevel((int) ((Math.sqrt(2500 + 200 * oldVersion.getExperience()) - 50) / 100));
        oldVersion.setUntilNextLevel(50 * (oldVersion.getLevel() + 1) * (oldVersion.getLevel() + 2) - oldVersion.getExperience());

        return playersRepository.save(oldVersion);
    }

    public void deletePlayer(Player player) {
        if (player.getId() > 0)
            playersRepository.delete(player);
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
