package com.excilys.formation.battleships.android.ui.ships;

import java.util.HashMap;
import java.util.Map;

import battleships.formation.excilys.com.battleships.R;
import battleships.ship.Carrier;

public class DrawableCarrier extends Carrier implements DrawableShip {
    static final Map<Orientation, Integer> DRAWABLES = new HashMap<>();

    static {
        DRAWABLES.put(Orientation.NORTH, R.drawable.carrier_n);
        DRAWABLES.put(Orientation.SOUTH, R.drawable.carrier_s);
        DRAWABLES.put(Orientation.EAST, R.drawable.carrier_e);
        DRAWABLES.put(Orientation.WEST, R.drawable.carrier_w);
    }

    @Override
    public int getDrawable() {
        return DRAWABLES.get(this.getOrientation());
    }
}
