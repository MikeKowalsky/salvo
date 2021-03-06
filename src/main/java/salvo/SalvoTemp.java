package salvo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SalvoTemp {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private long turnNumber;


    @ElementCollection
    @Column(name="location")
    private List<String> locations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    public SalvoTemp() { }

    public SalvoTemp(long turnNumber, List<String> locations) {
        this.turnNumber = turnNumber;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

    public long getTurnNumber() {
        return turnNumber;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

}
