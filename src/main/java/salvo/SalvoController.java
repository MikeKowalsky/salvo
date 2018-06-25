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

    @Autowired
    SalvoTempRepository salvoTempRepo;

    @Autowired
    ScoreRepository scoreRepo;

    //main method to create JSON with all data for gameViewPage
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

    //
    //authentication methods
    //

    // return name of logged in user
    public Player loggedInPlayer(Authentication authentication){
        return playerRepo.findByUserName(authentication.getName());
    }


    // get enemy's GamePlayer from player's one
    public GamePlayer GetEnemyGamePlayer (GamePlayer gamePlayerOwner){
        Optional<GamePlayer> enemy = gamePlayerOwner.getGame().getGamePlayerSet()
                .stream()
                .filter(oneGamePlayer -> oneGamePlayer.getId() != gamePlayerOwner.getId())
                .findFirst();

        return (enemy.isPresent()) ? enemy.get() : gamePlayerOwner;
    }


    //
    // games page request - returning data to build list of games with scores
    //
    @RequestMapping("/games")
    public Map<String, Object> test(Authentication authentication) {
        Map<String, Object> newGameDTO = new LinkedHashMap<String, Object>();
        if (isGuest(authentication)) {
            newGameDTO.put("player", null);
        } else {
            newGameDTO.put("player", MakePlayerDTO(loggedInPlayer(authentication)));
        }
        newGameDTO.put("games", GameIDs());
        return newGameDTO;
    }


    public List<Object> GameIDs(){
        return gameRepo
                .findAll()
                .stream()
                .map(oneGame -> MakeGameDTO(oneGame))
                .collect(Collectors.toList());
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

    private Set<Object> MakeScoreSetDTO (Set<Score> scoreSet){
        return scoreSet
                .stream()
                .map(oneScore -> MakeScoreDTO(oneScore))
                .collect(Collectors.toSet());
    }

    private Map<String, Number> MakeScoreDTO(Score score){
        Map<String, Number> scoreDTO = new LinkedHashMap<String, Number>();
        scoreDTO.put("playerId", score.getPlayer().getId());
        scoreDTO.put("score", score.getScore());
        return scoreDTO;
    }


    //
    // special request to get data to build leaderboard
    //
    @RequestMapping("/leaderboard")
    public List<Object> Leaderboard(){
        return playerRepo
                .findAll()
                .stream()
                .map(onePlayer -> MakeLbDTO(onePlayer))
                .collect(Collectors.toList());
    }

    // building leaderboard - object for one player
    private Map<String, Object> MakeLbDTO(Player player){
        Map<String, Object> lbDTO = new LinkedHashMap<String, Object>();
        lbDTO.put("playerId", player.getId());
        lbDTO.put("userName", player.getUserName());
        lbDTO.put("results", CountDifferentResultsDTO(player));
        return lbDTO;
    }
    // building leaderboard - obejct for different game results for that particular player
    private Map<String, Object> CountDifferentResultsDTO(Player player){
        Map<String, Object> countWinsDTO = new LinkedHashMap<String, Object>();
        countWinsDTO.put("won", CountCertainResults(1.0, player));
        countWinsDTO.put("tied", CountCertainResults(0.5, player));
        countWinsDTO.put("lost", CountCertainResults(0.0, player));
        countWinsDTO.put("sumOfPoints", CountSum(player));
        return countWinsDTO;
    }
    // building leaderboard - counting that type of results
    private Long CountCertainResults(Double result, Player player){
        return player.getScoreSet()
                .stream()
                .filter(oneScore -> oneScore.getScore().equals(result))
                .count();
    }
    // building leaderboard - when have all different results counting total sum
    private Double CountSum (Player player){
        return player.getScoreSet()
                .stream()
                .mapToDouble(oS -> oS.getScore())
                .sum();
    }


    //
    // GamePlayer DTO
    //
    private Set<Object> MakeGamePlayerSetDTO(Set<GamePlayer> gamePlayerSet){
        return gamePlayerSet
                .stream()
                .map(oneGamePlayer -> MakeGamePlayerDTO(oneGamePlayer))
                .collect(Collectors.toSet());
    }
    // GamePlayer DTO
    private Map<String, Object> MakeGamePlayerDTO(GamePlayer gamePlayer){
        Map<String, Object> gamePlayerDTO = new LinkedHashMap<String, Object>();
        gamePlayerDTO.put("id", gamePlayer.getId());
        gamePlayerDTO.put("player", MakePlayerDTO(gamePlayer.getPlayer()));
        return gamePlayerDTO;
    }
    // GamePlayer DTO
    private Map<String, Object> MakePlayerDTO(Player player){
        Map<String, Object> playerDTO = new LinkedHashMap<String, Object>();
        playerDTO.put("id", player.getId());
        playerDTO.put("email", player.getUserName());
        return playerDTO;
    }

    //
    // ship data for gameViewPage
    //
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


    //
    // salvo data for gameViewPage
    //
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

    //
    // hitsAndSink data for gameViewPage
    //
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
        currentShipInfo.put("hitsTillNow", currentShip.getHits().size());
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
        }

        Game newGame = gameRepo.save(new Game());
        Player currentPlayer = playerRepo.findByUserName(authentication.getName());
        GamePlayer newGamePlayer = new GamePlayer(currentPlayer, newGame);
        newGamePlayer.setStatus(GamePlayer.GameStatus.WaitingForShips);
        gamePlayerRepo.save(newGamePlayer);

        return new ResponseEntity<>(makeMap("GamePlayerID", newGamePlayer.getId()), HttpStatus.CREATED);
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
        GamePlayer newGamePlayer = new GamePlayer(currentPlayer, currentGame);
        newGamePlayer.setStatus(GamePlayer.GameStatus.WaitingForShips);
        gamePlayerRepo.save(newGamePlayer);

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
        Game currentGame = currentGamePlayer.getGame();
        Map<Object,Object> result = new HashMap<>();

        shipArray.forEach(ship -> {
            currentGamePlayer.addShip(ship);
            shipRepo.save(ship);
            result.put(ship.getId(), ship.getLocations());
        });

        if (!currentGame.isFull()){
            currentGamePlayer.setStatus(GamePlayer.GameStatus.WaitingForSecondPlayer);
        } else if (GetEnemyGamePlayer(currentGamePlayer).getStatus() == GamePlayer.GameStatus.WaitingForShips){
            currentGamePlayer.setStatus(GamePlayer.GameStatus.WaitingForSecondPlayer);
        } else {
            currentGamePlayer.setStatus(GamePlayer.GameStatus.WaitingForSalvoes);
            whenNeededChangeEnemysStatus(currentGamePlayer);
        }
        gamePlayerRepo.save(currentGamePlayer);
        return new ResponseEntity<>(makeMap("Added Ships", result), HttpStatus.CREATED);
    }

    // add salvoes
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
        Long currentTurnNo = whichTurnIsIt(currentGamePlayer);

        if (!isTurnCorrect(newSalvo, currentTurnNo)){
            return new ResponseEntity<>(makeMap("error", "Salvos are already located in this turn"), HttpStatus.FORBIDDEN);
        }

        Map<Object,Object> result = new HashMap<>();
        GamePlayer enemysGamePlayer = GetEnemyGamePlayer(currentGamePlayer);

        currentGamePlayer.addSalvo(newSalvo);

        if(enemysGamePlayer.getSalvosTemp().size() > 0){

            // save players salvo
            currentGamePlayer.addSalvo(newSalvo);
            salvoRepo.save(newSalvo);
            result.put(newSalvo.getId(), newSalvo.getLocations());

            // create and save enemys salvo
            SalvoTemp enemysSalvoTemp = getEnemySalvoTemp(enemysGamePlayer);

            List<String> tempLocations = new ArrayList<String>(enemysSalvoTemp.getLocations());

            Salvo enemysSalvo = new Salvo(enemysSalvoTemp.getTurnNumber(), tempLocations);

            enemysGamePlayer.addSalvo(enemysSalvo);
            salvoRepo.save(enemysSalvo);
            result.put(enemysSalvo.getId(), enemysSalvo.getLocations());

            //change player status
            currentGamePlayer.setStatus(GamePlayer.GameStatus.WaitingForSalvoes);

            // change enemys status
            whenNeededChangeEnemysStatus(currentGamePlayer);

            // delete used SalvoTemp from repo
            salvoTempRepo.delete(getEnemySalvoTemp(enemysGamePlayer).getId());

        } else { // there is not salvo from enemy, so player's salvo to temp

            List<String> tempLocations = new ArrayList<String>(newSalvo.getLocations());

            SalvoTemp newSalvoTemp = new SalvoTemp(newSalvo.getTurnNumber(), tempLocations);

            currentGamePlayer.addSalvoTemp(newSalvoTemp);
            salvoTempRepo.save(newSalvoTemp);

            // change status
            currentGamePlayer.setStatus(GamePlayer.GameStatus.WaitingForEnemy);
        }

        // save currentGamePlayer
        gamePlayerRepo.save(currentGamePlayer);

        return new ResponseEntity<>(makeMap("Added Salvo", result), HttpStatus.CREATED);
    }

    // method for addShips and addSalvoes to change enemy's game status
    public void whenNeededChangeEnemysStatus(GamePlayer currentGamePlayer){
        GamePlayer enemyGamePlayer = GetEnemyGamePlayer(currentGamePlayer);
        Game currentGame = currentGamePlayer.getGame();

        if(currentGame.isFull()
                && (enemyGamePlayer.getStatus() == GamePlayer.GameStatus.WaitingForEnemy
                || enemyGamePlayer.getStatus() == GamePlayer.GameStatus.WaitingForSecondPlayer)){
            enemyGamePlayer.setStatus(GamePlayer.GameStatus.WaitingForSalvoes);
            gamePlayerRepo.save(enemyGamePlayer);
        }
    }

    // return enemySalvoTemp
    private SalvoTemp getEnemySalvoTemp(GamePlayer enemysGamePlayer){
        Optional<SalvoTemp> optional = enemysGamePlayer
                .getSalvosTemp()
                .stream()
//                    .filter(salvoTemp -> salvoTemp.getTurnNumber() == currentTurnNo)
                .findFirst();
        return optional.get();
    }

    // add salvoes
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

    // add salvoes
    private boolean isTurnCorrect(Salvo salvo, Long currentTurnNo){
        Long turnNoInReceivedData = salvo.getTurnNumber();
        return turnNoInReceivedData.equals(currentTurnNo);
    }

    // creating Map objects for responses
    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    //
    // Game status
    //
    private Map<String, Object> MakeGameStatusDTO(GamePlayer gp){

        Map<String, Object> gameStatusDTO = new LinkedHashMap<String, Object>();

        if(isGameOver(gp)){
            gp.setStatus(GamePlayer.GameStatus.GameOver);
            GetEnemyGamePlayer(gp).setStatus(GamePlayer.GameStatus.GameOver);

            gamePlayerRepo.save(gp);
            gamePlayerRepo.save(GetEnemyGamePlayer(gp));
        }

        gameStatusDTO.put("status", gp.getStatus());
        gameStatusDTO.put("isGameOver", isGameOver(gp));
        if(isGameOver(gp)){
            gameStatusDTO.put("whoWon", whoWon(gp));
        }
        return gameStatusDTO;
    }

    // game status && end of the game
    private boolean isGameOver(GamePlayer gp){
        return ((noPlayersSinkedShips(gp) == 5) || noPlayersSinkedShips(GetEnemyGamePlayer(gp)) == 5);
    }

    // game status && end of the game
    private long whoWon(GamePlayer gp){
        if (noPlayersSinkedShips(gp) < noPlayersSinkedShips(GetEnemyGamePlayer(gp))){
            changeScores(gp, "smbWon");
            return gp.getPlayer().getId();
        } else if (noPlayersSinkedShips(gp) > noPlayersSinkedShips(GetEnemyGamePlayer(gp))){
            changeScores(GetEnemyGamePlayer(gp), "smbWon");
            return GetEnemyGamePlayer(gp).getPlayer().getId();
        } else {
            changeScores(gp, "tie");
            return -1;
        }
    }

    // game status && end of the game
    private long noPlayersSinkedShips(GamePlayer gp){
        return gp.getShips().stream()
                .filter(ship -> ship.isSink())
                .count();
    }

    // game status && end of the game
    private void changeScores(GamePlayer gp, String tie){
        if(!gp.getGame().hasScore()){
            if(tie == "tie"){
                Score newScore1 = new Score(gp.getPlayer(), gp.getGame(), 0.5);
                Score newScore2 = new Score(GetEnemyGamePlayer(gp).getPlayer(), gp.getGame(), 0.5);
                scoreRepo.save(newScore1);
                scoreRepo.save(newScore2);
            } else {
                Score newScore1 = new Score(gp.getPlayer(), gp.getGame(), 1.0);
                Score newScore2 = new Score(GetEnemyGamePlayer(gp).getPlayer(), gp.getGame(), 0.0);
                scoreRepo.save(newScore1);
                scoreRepo.save(newScore2);
            }

        }
    }
}
