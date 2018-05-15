package salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mike on 03.05.18.
 */

@RequestMapping("/api")
@RestController
public class SalvoController {

    @Autowired
    GameRepository gameRepo;

    @Autowired
    GamePlayerRepository gamePlayerRepo;

    private Map<String, Object> MakePlayerDTO(Player player){
        Map<String, Object> playerDTO = new LinkedHashMap<String, Object>();
        playerDTO.put("id", player.getId());
        playerDTO.put("email", player.getUserName());
        return playerDTO;
    }

    private Map<String, Object> MakeGamePlayerDTO(GamePlayer gamePlayer){
        Map<String, Object> gamePlayerDTO = new LinkedHashMap<String, Object>();
        gamePlayerDTO.put("id", gamePlayer.getId());
        gamePlayerDTO.put("player", MakePlayerDTO(gamePlayer.getPlayer()));
        return gamePlayerDTO;
    }

    private Set<Object> MakeGamePlayerSetDTO(Set<GamePlayer> gamePlayerSet){
        return gamePlayerSet
                .stream()
                .map(oneGamePlayer -> MakeGamePlayerDTO(oneGamePlayer))
                .collect(Collectors.toSet());
    }

    private Map<String, Object> MakeGameDTO(Game game) {
        Map<String, Object> gameDTO = new LinkedHashMap<String, Object>();
        gameDTO.put("id", game.getId());
        gameDTO.put("created", game.getCreationDate());
        gameDTO.put("gamePlayers", MakeGamePlayerSetDTO(game.getGamePlayerSet()));
        return gameDTO;
    }


    @RequestMapping("/games")
    public List<Object> GameIDs(){
        return gameRepo
                .findAll()
                .stream()
                .map(oneGame -> MakeGameDTO(oneGame))
                .collect(Collectors.toList());
    }


    private Map<String, Object> MakeShipDTO (Ship ship){
        Map<String, Object> shipDTO = new LinkedHashMap<String, Object>();
        shipDTO.put("type", ship.getShipType());
        shipDTO.put("locations", ship.getLocations());
        return shipDTO;
    }


    private Set<Object> MakeShipSetDTO (Set<Ship> ships){
        return ships
                .stream()
                .map(ship -> MakeShipDTO(ship))
                .collect(Collectors.toSet());
    }

    private Map<Long, Object> MakeSalvoTurnsDTO(Salvo salvo){
        Map<Long, Object> salvoTurnsDTO = new LinkedHashMap<Long, Object>();
        salvoTurnsDTO.put(salvo.getTurnNumber(), salvo.getLocations());
        return salvoTurnsDTO;
    }

    private Map<Long, Object> MakeSalvoDTO(Salvo salvo){
        Map<Long, Object> salvoDTO = new LinkedHashMap<Long, Object>();
        salvoDTO.put(salvo.getGamePlayer().getPlayer().getId(), MakeSalvoTurnsDTO(salvo));
        return salvoDTO;
    }

    private Set<Object> MakeSalvoSetDTO (Set<Salvo> salvos){
        return salvos
                .stream()
                .map(salvo -> MakeSalvoDTO(salvo))
                .collect(Collectors.toSet());
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> singleGameView (@PathVariable Long gamePlayerId){

        GamePlayer gp = gamePlayerRepo.findOne(gamePlayerId);

        Map<String, Object> gameWithUserMap = new LinkedHashMap<String, Object>();
        gameWithUserMap.put("gameId", gp.getGame().getId());
        gameWithUserMap.put("created", gp.getGame().getCreationDate());
        gameWithUserMap.put("gamePlayers", MakeGamePlayerSetDTO(gp.getGame().getGamePlayerSet()));
        gameWithUserMap.put("ships", MakeShipSetDTO(gp.getShips()));
        gameWithUserMap.put("salvoes", MakeSalvoSetDTO(gp.getSalvos()));

        return gameWithUserMap;
    }

}
