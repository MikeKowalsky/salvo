package salvo;

import javax.persistence.*;
//import java.text.Format;
//import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;


@Entity
public class Game{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private Date creationDate;
    private boolean over;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayerSet;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> scoreSet;

    public Game(){
        creationDate = new Date();
    }

    public Game(long change){
        Date currentDate = new Date();
        creationDate = Date.from(currentDate.toInstant().plusSeconds(change));
    }

    public void addGamePlayer(GamePlayer gameplayer){
        gameplayer.setGame(this);
        gamePlayerSet.add(gameplayer);
    }

    public void addScore(Score score){
        score.setGame(this);
        scoreSet.add(score);
    }

    public Set<GamePlayer> getGamePlayerSet() {
        return gamePlayerSet;
    }

    public Set<Score> getScoreSet() {
        return scoreSet;
    }

    public boolean hasScore () {
        return (!getScoreSet().isEmpty()) ? true : false;
    }

    public List<Player> getPlayers(){
        return gamePlayerSet.stream().map(player -> player.getPlayer()).collect(toList());
    }

    public long getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public boolean isFull() {
        return this.getGamePlayerSet().size() > 1;
    }

    public void setOver(boolean over) {
        this.over = over;
    }

    public boolean isOver() {
        return over;
    }
}

