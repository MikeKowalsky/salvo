package salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    PlayerRepository playerRepo;

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

    private Map<String, Number> MakeScoreDTO(Score score){
        Map<String, Number> scoreDTO = new LinkedHashMap<String, Number>();
        scoreDTO.put("playerId", score.getPlayer().getId());
        scoreDTO.put("score", score.getScore());
        return scoreDTO;
    }

    private Set<Object> MakeScoreSetDTO (Set<Score> scoreSet){
        return scoreSet
                .stream()
                .map(oneScore -> MakeScoreDTO(oneScore))
                .collect(Collectors.toSet());
    }

    private Map<String, Object> MakeGameDTO(Game game) {
        Map<String, Object> gameDTO = new LinkedHashMap<String, Object>();
        gameDTO.put("id", game.getId());
        gameDTO.put("created", game.getCreationDate());
        gameDTO.put("gamePlayers", MakeGamePlayerSetDTO(game.getGamePlayerSet()));
        if (game.hasScore()){
            gameDTO.put("scores", MakeScoreSetDTO(game.getScoreSet()));
        }
        return gameDTO;
    }

    public List<Object> GameIDs(){
        return gameRepo
                .findAll()
                .stream()
                .map(oneGame -> MakeGameDTO(oneGame))
                .collect(Collectors.toList());
    }

    @RequestMapping("/games")
    public Map<String, Object> test(Authentication authentication){
        Map<String, Object> newGameDTO = new LinkedHashMap<String, Object>();
        if (isGuest(authentication)){
            newGameDTO.put("player", null);
        } else {
            newGameDTO.put("player", MakePlayerDTO(loggedInPlayer(authentication)));
        }
        newGameDTO.put("games", GameIDs());
        return newGameDTO;
    }

    public Player loggedInPlayer(Authentication authentication){
        return playerRepo.findByUserName(authentication.getName());
    }

    private Double CountSum (Player player){
        return player.getScoreSet()
                .stream()
                .mapToDouble(oS -> oS.getScore())
                .sum();
    }

    private Long CountCertainResults(Double result, Player player){
        return player.getScoreSet()
                .stream()
                .filter(oneScore -> oneScore.getScore().equals(result))
                .count();
    }

    private Map<String, Object> CountDifferentResultsDTO(Player player){
        Map<String, Object> countWinsDTO = new LinkedHashMap<String, Object>();
        countWinsDTO.put("won", CountCertainResults(1.0, player));
        countWinsDTO.put("tied", CountCertainResults(0.5, player));
        countWinsDTO.put("lost", CountCertainResults(0.0, player));
        countWinsDTO.put("sumOfPoints", CountSum(player));
        return countWinsDTO;
    }

    private Map<String, Object> MakeLbDTO(Player player){
        Map<String, Object> lbDTO = new LinkedHashMap<String, Object>();
        lbDTO.put("playerId", player.getId());
        lbDTO.put("userName", player.getUserName());
        lbDTO.put("results", CountDifferentResultsDTO(player));
        return lbDTO;
    }

    @RequestMapping("/leaderboard")
    public List<Object> Leaderboard(){
        return playerRepo
                .findAll()
                .stream()
                .map(onePlayer -> MakeLbDTO(onePlayer))
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

    public Set<Salvo> GetEnemySalvoSet (GamePlayer gamePlayer){
        return gamePlayer.getSalvos();
    }

    public GamePlayer GetEnemyGamePlayer (GamePlayer gamePlayerOwner){
        Optional<GamePlayer> enemy = gamePlayerOwner.getGame().getGamePlayerSet()
                .stream()
                .filter(oneGamePlayer -> oneGamePlayer.getId() != gamePlayerOwner.getId())
                .findFirst();

        return (enemy.isPresent()) ? enemy.get() : gamePlayerOwner;
    }

    private Map<String, Object> MakeSalvoDTO(Salvo salvo){
        Map<String, Object> salvoDTO = new LinkedHashMap<String, Object>();
        salvoDTO.put("playerId", salvo.getGamePlayer().getPlayer().getId());
        salvoDTO.put("turnNo", salvo.getTurnNumber());
        salvoDTO.put("locations", salvo.getLocations());
        return salvoDTO;
    }

    private Set<Object> MakeSalvoSetDTO (Set<Salvo> salvos, Set<Salvo> enemysSet){
        salvos.addAll(enemysSet);
        return salvos
                .stream()
                .map(salvo -> MakeSalvoDTO(salvo))
                .collect(Collectors.toSet());
    }


    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> singleGameView (Authentication authentication, @PathVariable Long gamePlayerId) throws UserIsNotAuthorized, NoLoggedInUser{

        GamePlayer gp = gamePlayerRepo.findOne(gamePlayerId);

        Map<String, Object> gameWithUserMap = new LinkedHashMap<String, Object>();
        gameWithUserMap.put("gameId", gp.getGame().getId());
        gameWithUserMap.put("created", gp.getGame().getCreationDate());
        gameWithUserMap.put("gamePlayers", MakeGamePlayerSetDTO(gp.getGame().getGamePlayerSet()));
        gameWithUserMap.put("ships", MakeShipSetDTO(gp.getShips()));
        gameWithUserMap.put("salvoes", MakeSalvoSetDTO(gp.getSalvos(), GetEnemySalvoSet(GetEnemyGamePlayer(gp))));
//        if(!isGuest(authentication)) {
//            gameWithUserMap.put("loggedIn", authentication.getName());
//        } else {
//            gameWithUserMap.put("loggedIn", null);
//        }

        if (!isGuest(authentication)){
            Player loggedInPlayer = playerRepo.findByUserName(authentication.getName());
            if(loggedInPlayer == gp.getPlayer()){
                gameWithUserMap.put("loggedInName", gp.getPlayer().getUserName());
                return gameWithUserMap;
            } else {
                throw new UserIsNotAuthorized ("Unauthorized user");
            }
        } else {
            throw new NoLoggedInUser("Log in first");
        }

//        return gameWithUserMap;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    private class UserIsNotAuthorized extends Exception{
        public UserIsNotAuthorized (String message){
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    private class NoLoggedInUser extends Exception{
        public NoLoggedInUser (String message){
            super(message);
        }
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createPlayer(String name, String pwd) {
//        if (username.isEmpty()) {
//            return new ResponseEntity<>(makeMap("error", "Invalid name"), HttpStatus.FORBIDDEN);
//        }

        Player player = playerRepo.findByUserName(name);
        if (player != null) {
            return new ResponseEntity<>(makeMap("error", "Username already exists"), HttpStatus.FORBIDDEN);
        }

        Player newPlayer = playerRepo.save(new Player(name, pwd));
        return new ResponseEntity<>(makeMap("Username", newPlayer.getUserName()), HttpStatus.CREATED);
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}
