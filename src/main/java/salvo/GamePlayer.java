package salvo;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private Date enterDate;
    private GameStatus status;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<Salvo> salvos = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER)
    Set<SalvoTemp> salvosTemp = new HashSet<>();

    public GamePlayer(){
        enterDate = new Date();
    }

    public GamePlayer(Player player, Game game){
        enterDate = new Date();
        this.player = player;
        this.game = game;
    }

    public long getId() {
        return id;
    }

    public Date getEnterDate() {
        return enterDate;
    }

    public void setEnterDate(Date enterDate) {
        this.enterDate = enterDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalvos() {
        return salvos;
    }

    public Set<SalvoTemp> getSalvosTemp() {
        return salvosTemp;
    }

    public void addShip (Ship ship) {
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvo (Salvo salvo) {
        salvo.setGamePlayer(this);
        salvos.add(salvo);
    }

    public void addSalvoTemp (SalvoTemp salvoTemp) {
        salvoTemp.setGamePlayer(this);
        salvosTemp.add(salvoTemp);
    }

    public enum GameStatus{
        WaitingForShips,
        WaitingForSalvoes,
        WaitingForEnemy
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}
