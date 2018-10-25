package battleships;


import java.util.List;

import battleships.ship.AbstractShip;

public interface IBoard {

    /**
     * Sends a hit at the given position
     *
     * @param x
     * @param y
     * @return status for the hit (eg : strike or miss)
     */
    Hit sendHit(int x, int y);

    /**
     * @return the size of the Board
     */
    int getSize();

    /**
     * put the given ship at the given coord
     *
     * @param ship The ship to place on the board
     * @param x
     * @param y
     */
    void putShip(AbstractShip ship, int x, int y) throws ShipException;

    /**
     * @param x
     * @param y
     * @return true if a ship is located at the given coords
     */
    boolean hasShip(int x, int y);

    /**
     * Set the state of the hit at a given position
     *
     * @param hit true if the hit must be set to successful
     * @param x
     * @param y
     */
    void setHit(Boolean hit, int x, int y);

    /**
     * Get the state of a hit at the given position
     *
     * @param x
     * @param y
     * @return
     */
    Boolean getHit(int x, int y);

    List<int[]> surroundShipWithMiss(IBoard board, int x, int y);
}
