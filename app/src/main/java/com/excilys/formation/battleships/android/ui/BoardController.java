package com.excilys.formation.battleships.android.ui;

import com.excilys.formation.battleships.android.ui.ships.DrawableShip;

import java.util.List;

import battleships.Hit;
import battleships.IBoard;
import battleships.ShipException;
import battleships.formation.excilys.com.battleships.R;
import battleships.ship.AbstractShip;

public class BoardController implements IBoard {

    /* ***
     * Public constants
     */
    public static final int SHIPS_FRAGMENT = 0;
    public static final int HITS_FRAGMENT = 1;

    /* ***
     * Attributes
     */
    private final IBoard mBoard;
    private final BoardGridFragment[] mFragments;
    private final BoardGridFragment mHitsFragment;
    private final BoardGridFragment mShipsFragment;



    public BoardController(IBoard board) {
        mBoard = board;
        mShipsFragment = BoardGridFragment.newInstance(SHIPS_FRAGMENT, mBoard.getSize(), R.drawable.ships_bg, R.string.board_ships_title);
        mHitsFragment = BoardGridFragment.newInstance(HITS_FRAGMENT, mBoard.getSize(), R.drawable.hits_bg, R.string.board_hits_title);

        mFragments = new BoardGridFragment[] {
            mShipsFragment, mHitsFragment
        };
    }

    public BoardGridFragment[] getFragments() {
        return mFragments;
    }

    public void displayHitInShipBoard(boolean hit, int x, int y) {
        mShipsFragment.putDrawable(hit ? R.drawable.hit : R.drawable.miss, x, y);
    }


    @Override
    public Hit sendHit(int x, int y) {
        // TODO decor me
        Hit hit = mBoard.sendHit(x, y);

        return hit;
        //return mBoard.sendHit(x, y);
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public void putShip(AbstractShip ship, int x, int y) throws ShipException {
        if (!(ship instanceof DrawableShip)) {
            throw new IllegalArgumentException("Cannot put a Ship that does not implement DrawableShip.");
        }

        // TODO Retrieve ship orientation
        AbstractShip.Orientation orientation = ship.getOrientation();
        int endX = x;
        int endY = y;
        // TODO this may be useful
        switch (orientation) {
            case NORTH:
                endY = y-ship.getLength()+1;
                break;
            case SOUTH:
                endY = y;
                break;
            case EAST:
                endX = x;
                break;
            case WEST:
                endX = x-ship.getLength()+1;
                break;
        }
        mBoard.putShip(ship, x, y);
        mShipsFragment.putDrawable(((DrawableShip) ship).getDrawable(), endX, endY);
    }

    @Override
    public boolean hasShip(int x, int y) {
        // TODO
        mBoard.hasShip(x, y);
        return false;
    }

    @Override
    public void setHit(Boolean hit, int x, int y) {
        // TODO decore me
        mBoard.setHit(hit, x, y);
        mHitsFragment.putDrawable(hit ? R.drawable.hit : R.drawable.miss, x, y);
    }

    @Override
    public Boolean getHit(int x, int y) {
        // TODO
        return mBoard.getHit(x, y);
    }

    @Override
    public List<int[]> surroundShipWithMiss(IBoard board, int x, int y) {
        return mBoard.surroundShipWithMiss(board, x, y);
    }
}
