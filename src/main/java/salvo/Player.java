package salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String name;
    private String userName;
    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayerSet;

    public Player(){ }

    public Player(String name, String userName, String password){

        this.name = name;
        this.userName = userName;
        this.password = password;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public void addGamePlayer(GamePlayer gameplayer){
        gameplayer.setPlayer(this);
        gamePlayerSet.add(gameplayer);
    }

    @JsonIgnore
    public List<Game> getGames(){
        return gamePlayerSet.stream().map(game -> game.getGame()).collect(toList());
    }

    @Override
    public String toString() {
        return userName;
    }
}
