package com.excilys.formation.battleships.android.ui.ships;

import java.util.HashMap;
import java.util.Map;

import battleships.formation.excilys.com.battleships.R;
import battleships.ship.AbstractShip;
import battleships.ship.BattleShip;

/**
 * Created by scaltot on 12/09/17.
 */

public class DrawableBattleship extends BattleShip implements DrawableShip {

    //TODO insert map declaration
    static final Map<Orientation, Integer> DRAWABLES = new HashMap<>();

    //TODO add static block using myMap.put(key, value) to initialize map.
    //TODO key = the ship orientation and value = the drawable corresponding
    static {
        DRAWABLES.put(Orientation.NORTH, R.drawable.battleship_n);
        DRAWABLES.put(Orientation.SOUTH, R.drawable.battleship_s);
        DRAWABLES.put(Orientation.EAST, R.drawable.battleship_e);
        DRAWABLES.put(Orientation.WEST, R.drawable.battleship_w);
    }

    // TODO return the right drawable according to ship's orientation using the map


    @Override
    public int getDrawable() {
        return DRAWABLES.get(this.getOrientation());
    }
}
