package battleships.ship;

public class Frigate extends AbstractShip {

    public Frigate() {
        this(Orientation.NORTH);
    }

    public Frigate(Orientation o) {
        super('D', "Frégate", 1, o);
    }
}
