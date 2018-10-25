package com.excilys.formation.battleships.android.ui.ships;

import java.util.HashMap;
import java.util.Map;

import battleships.formation.excilys.com.battleships.R;
import battleships.ship.Frigate;

public class DrawableFrigate extends Frigate implements DrawableShip {

    //TODO insert map declaration
    static final Map<Orientation, Integer> DRAWABLES = new HashMap<>();

    //TODO add static block using myMap.put(key, value) to initialize map.
    //TODO key = the ship orientation and value = the drawable corresponding
    static {
        DRAWABLES.put(Orientation.NORTH, R.drawable.frigate_n);
        DRAWABLES.put(Orientation.SOUTH, R.drawable.frigate_s);
        DRAWABLES.put(Orientation.EAST, R.drawable.frigate_e);
        DRAWABLES.put(Orientation.WEST, R.drawable.frigate_w);
    }

    // TODO return the right drawable according to ship's orientation using the map


    @Override
    public int getDrawable() {
        return DRAWABLES.get(this.getOrientation());
    }
}
