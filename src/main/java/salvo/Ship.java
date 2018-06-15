package salvo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String shipType;

    @ElementCollection
    @Column(name="location")
    private List<String> locations = new ArrayList<>();

    @ElementCollection
    @Column(name="hit")
    private List<String> hits = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship (){}

    public Ship (String type) {
        this.shipType = type;
    }

    public Ship (String type, List<String> locations) {
        this.shipType = type;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public String getShipType() {
        return shipType;
    }

    public List<String> getHits() {
        return hits;
    }

    public void addHits(String hit) {
        this.getHits().add(hit);
    }

    public boolean isSink(){
        return (this.getLocations().size() == this.getHits().size());
    }

    @Override
    public String toString() {
        return "ID: " + this.getId() + " / type: " + this.shipType + " / location: " + this.getLocations();
    }
}


