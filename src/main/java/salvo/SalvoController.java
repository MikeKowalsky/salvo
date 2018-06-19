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

    @Autowired
    ShipRepository shipRepo;

    @Autowired
    SalvoRepository salvoRepo;

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

    // ship data for gameViewPage
    private Map<String, Object> MakeShipDTO (Ship ship){
        Map<String, Object> shipDTO = new LinkedHashMap<String, Object>();
        shipDTO.put("shipType", ship.getShipType());
        shipDTO.put("locations", ship.getLocations());
        return shipDTO;
    }

    // ship data for gameViewPage
    private Set<Object> MakeShipSetDTO (Set<Ship> ships){
        return ships
                .stream()
                .map(ship -> MakeShipDTO(ship))
                .collect(Collectors.toSet());
    }

    public GamePlayer GetEnemyGamePlayer (GamePlayer gamePlayerOwner){
        Optional<GamePlayer> enemy = gamePlayerOwner.getGame().getGamePlayerSet()
                .stream()
                .filter(oneGamePlayer -> oneGamePlayer.getId() != gamePlayerOwner.getId())
                .findFirst();

        return (enemy.isPresent()) ? enemy.get() : gamePlayerOwner;
    }

    // salvo data for gameViewPage
    private Map<String, Object> MakeSalvoDTO(Salvo salvo){
        Map<String, Object> salvoDTO = new LinkedHashMap<String, Object>();
        salvoDTO.put("playerId", salvo.getGamePlayer().getPlayer().getId());
        salvoDTO.put("turnNo", salvo.getTurnNumber());
        salvoDTO.put("locations", salvo.getLocations());
        return salvoDTO;
    }

    // salvo data for gameViewPage
    private Set<Object> MakeSalvoSetDTO (Set<Salvo> playersSalvosSet, Set<Salvo> enemysSalvoesSet){

        Set<Salvo> doubleSalvoSet = new HashSet<Salvo>();
        doubleSalvoSet.addAll(playersSalvosSet);
        doubleSalvoSet.addAll(enemysSalvoesSet);

        return doubleSalvoSet
                .stream()
                .map(salvo -> MakeSalvoDTO(salvo))
                .collect(Collectors.toSet());
    }

    // hitsAndSink data for gameViewPage
    private Map<String, Object> MakeShipInfoForHits(Ship currentShip, Set<Salvo> currentSalvosFromCurrentTurn){

        List<String> currentShipLocations = currentShip.getLocations();
        ArrayList<String> hits = new ArrayList<>();

        currentSalvosFromCurrentTurn.forEach(salvo -> {
            salvo.getLocations().forEach(singleShot -> {
                if (currentShipLocations.contains(singleShot)){
                    hits.add(singleShot);
                    currentShip.addHits(singleShot);
                }
            });
        });

        Map<String, Object> currentShipInfo = new LinkedHashMap<String, Object>();
        currentShipInfo.put("size", currentShipLocations.size());
        currentShipInfo.put("hits", hits);
        currentShipInfo.put("isSink", currentShip.isSink());
        return currentShipInfo;
    }

    // hitsAndSink data for gameViewPage
    private Map<Object, Object> MakeHitsOnGivenPlayer(GamePlayer givenGP, int currentTurn){

        Set<Ship> givenPlayerShips = givenGP.getShips();
        Set<Salvo> enemySalvosFromCurrentTurn = GetEnemyGamePlayer(givenGP).getSalvos()
                .stream()
                .filter(salvo -> salvo.getTurnNumber() == currentTurn)
                .collect(Collectors.toSet());

        Map<Object, Object> hitsOnGivenPlayer = new LinkedHashMap<Object, Object>();

        givenPlayerShips.stream().forEach((ship) -> {
            hitsOnGivenPlayer.put(ship.getShipType(), MakeShipInfoForHits(ship, enemySalvosFromCurrentTurn));
        });
        return hitsOnGivenPlayer;
    }

    // hitsAndSink data for gameViewPage
    private Map<String, Object> MakeHitsAndSinks(int currentTurn, GamePlayer gp){

        Map<String, Object> hitsAndSinks = new LinkedHashMap<String, Object>();
        hitsAndSinks.put("turnNo", currentTurn);
        hitsAndSinks.put("hitsOnPlayer", MakeHitsOnGivenPlayer(gp, currentTurn));
        hitsAndSinks.put("hitsOnEnemy", MakeHitsOnGivenPlayer(GetEnemyGamePlayer(gp), currentTurn));
        return hitsAndSinks;
    }

    // hitsAndSink data for gameViewPage
    private Set<Object> MakeHitsAndSinksSet (GamePlayer gp){

         Long lastTurnNo = whichTurnIsIt(gp) - 1;
         if (lastTurnNo == 0){ return null; }

         Set<Object> hitsAndSinksSet = new HashSet<Object>();
         for (int i = 1; i <= lastTurnNo; i++){
             hitsAndSinksSet.add(MakeHitsAndSinks(i, gp));
         }
         return hitsAndSinksSet;
    }

    //main method to create JSON with all date for gameViewPage
    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> singleGameView (Authentication authentication, @PathVariable Long gamePlayerId) throws UserIsNotAuthorized, NoLoggedInUser{

        GamePlayer gp = gamePlayerRepo.findOne(gamePlayerId);

        Map<String, Object> gameWithUserMap = new LinkedHashMap<String, Object>();
        gameWithUserMap.put("gameId", gp.getGame().getId());
        gameWithUserMap.put("created", gp.getGame().getCreationDate());
        gameWithUserMap.put("gamePlayers", MakeGamePlayerSetDTO(gp.getGame().getGamePlayerSet()));
        gameWithUserMap.put("ships", MakeShipSetDTO(gp.getShips()));
        gameWithUserMap.put("salvoes", MakeSalvoSetDTO(gp.getSalvos(), GetEnemyGamePlayer(gp).getSalvos()));
        gameWithUserMap.put("hAS", MakeHitsAndSinksSet(gp));
        gameWithUserMap.put("gameStatus", MakeGameStatusDTO(gp));


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

    private boolean isGamePlayerExist(Long gamePlayerId){
        return gamePlayerRepo.existsById(gamePlayerId);
    }

    private boolean isPlayerInThisGame(Authentication authentication, Long gamePlayerId){
        Player currentPlayer = playerRepo.findByUserName(authentication.getName());
        return (currentPlayer.gamePlayerSet
                                        .stream()
                                        .filter(gamePlayer -> gamePlayer.getId() == gamePlayerId)
                                        .count() > 0);
    }

    private boolean areShipsPlaced(Long gamePlayerId){
        return (gamePlayerRepo.findOne(gamePlayerId).getShips().size() > 0);
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


    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication){

        if (isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "You need to be logged in"), HttpStatus.UNAUTHORIZED);
        } else {
            Game newGame = gameRepo.save(new Game());
            Player currentPlayer = playerRepo.findByUserName(authentication.getName());
            GamePlayer newGamePlayer = gamePlayerRepo.save(new GamePlayer(currentPlayer, newGame));
            newGamePlayer.setStatus(GamePlayer.GameStatus.WaitingForShips);

            return new ResponseEntity<>(makeMap("GamePlayerID", newGamePlayer.getId()), HttpStatus.CREATED);
        }

    }


    @RequestMapping(path = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable Long gameId){

        if (isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "You need to be logged in"), HttpStatus.UNAUTHORIZED);
        }
        if(!gameRepo.existsById(gameId)){
            return new ResponseEntity<>(makeMap("error", "No such a game"), HttpStatus.FORBIDDEN);
        }

        Game currentGame = gameRepo.findOne(gameId);

        if(currentGame.isFull()) {
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }

        Player currentPlayer = playerRepo.findByUserName(authentication.getName());
        GamePlayer newGamePlayer = gamePlayerRepo.save(new GamePlayer(currentPlayer, currentGame));
        return new ResponseEntity<>(makeMap("GamePlayerID", newGamePlayer.getId()), HttpStatus.CREATED);
    }


    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShips(Authentication authentication,
                                                        @PathVariable Long gamePlayerId,
                                                        @RequestBody ArrayList<Ship> shipArray){

        if (isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "You need to be logged in"), HttpStatus.UNAUTHORIZED);
        }
        if (!isGamePlayerExist(gamePlayerId)){
            return new ResponseEntity<>(makeMap("error", "That gamePlayerId dosnt exist"), HttpStatus.UNAUTHORIZED);
        }

        if (!isPlayerInThisGame(authentication, gamePlayerId)){
            return new ResponseEntity<>(makeMap("error", "This Player is not in this game"), HttpStatus.UNAUTHORIZED);
        }

        if (areShipsPlaced(gamePlayerId)){
            return new ResponseEntity<>(makeMap("error", "Ships are already located"), HttpStatus.FORBIDDEN);
        }

        GamePlayer currentGamePlayer = gamePlayerRepo.findOne(gamePlayerId);
        Map<Object,Object> result = new HashMap<>();

        shipArray.forEach(ship -> {
            currentGamePlayer.addShip(ship);
            shipRepo.save(ship);
            result.put(ship.getId(), ship.getLocations());
        });

        return new ResponseEntity<>(makeMap("Added Ships", result), HttpStatus.CREATED);
    }


    @RequestMapping(path = "/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvos(Authentication authentication,
                                                        @PathVariable Long gamePlayerId,
                                                        @RequestBody Salvo newSalvo){

        if (isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "You need to be logged in"), HttpStatus.UNAUTHORIZED);
        }
        if (!isGamePlayerExist(gamePlayerId)){
            return new ResponseEntity<>(makeMap("error", "That gamePlayerId dosnt exist"), HttpStatus.UNAUTHORIZED);
        }

        if (!isPlayerInThisGame(authentication, gamePlayerId)){
            return new ResponseEntity<>(makeMap("error", "This Player is not in this game"), HttpStatus.UNAUTHORIZED);
        }

        GamePlayer currentGamePlayer = gamePlayerRepo.findOne(gamePlayerId);
        Long turnNo = whichTurnIsIt(currentGamePlayer);

        if (!isTurnCorrect(newSalvo, turnNo)){
            return new ResponseEntity<>(makeMap("error", "Salvos are already located in this turn"), HttpStatus.FORBIDDEN);
        }

        Map<Object,Object> result = new HashMap<>();

        currentGamePlayer.addSalvo(newSalvo);
        salvoRepo.save(newSalvo);
        result.put(newSalvo.getId(), newSalvo.getLocations());

        return new ResponseEntity<>(makeMap("Added Salvo", result), HttpStatus.CREATED);
    }

    private Long whichTurnIsIt(GamePlayer gamePlayer){
        Comparator<Long> comparator = Comparator.comparing(Long::intValue);

        Long turnNo = new Long(1);

        if (gamePlayer.getSalvos().size() > 0){
            turnNo = (gamePlayer
                    .getSalvos()
                    .stream()
                    .map(salvo -> salvo.getTurnNumber())
                    .max(comparator)
                    .get() + 1
            );
        }
        return turnNo;
    }

    private boolean isTurnCorrect(Salvo salvo, Long turnNo){
        Long turnNoInReceivedData = salvo.getTurnNumber();
        return turnNoInReceivedData.equals(turnNo);
    }


    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    //end of the game
    private Map<String, Object> MakeGameStatusDTO(GamePlayer gp){

        Map<String, Object> gameStatusDTO = new LinkedHashMap<String, Object>();
        gameStatusDTO.put("status", gp.getStatus());
        gameStatusDTO.put("isGameOver", isGameOver(gp));
        if(isGameOver(gp)){
            gameStatusDTO.put("whoWon", whoWon(gp));
        }
        return gameStatusDTO;
    }

    // end of the game
    private boolean isGameOver(GamePlayer gp){
        return ((noPlayersSinkedShips(gp) == 5) || noPlayersSinkedShips(GetEnemyGamePlayer(gp)) == 5);
    }

    // end of the game
    private long whoWon(GamePlayer gp){
        if (noPlayersSinkedShips(gp) < noPlayersSinkedShips(GetEnemyGamePlayer(gp))){
            return gp.getPlayer().getId();
        } else if (noPlayersSinkedShips(gp) > noPlayersSinkedShips(GetEnemyGamePlayer(gp))){
            return GetEnemyGamePlayer(gp).getPlayer().getId();
        } else {
            return -1;
        }
    }

    // end of the game
    private long noPlayersSinkedShips(GamePlayer gp){
        return gp.getShips().stream()
                .filter(ship -> ship.isSink())
                .count();
    }




}
