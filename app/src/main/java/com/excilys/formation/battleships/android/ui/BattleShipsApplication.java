package com.excilys.formation.battleships.android.ui;

import android.app.Application;
import android.content.Intent;

import com.excilys.formation.battleships.android.ui.ships.DrawableBattleship;
import com.excilys.formation.battleships.android.ui.ships.DrawableCarrier;
import com.excilys.formation.battleships.android.ui.ships.DrawableDestroyer;
import com.excilys.formation.battleships.android.ui.ships.DrawableFrigate;
import com.excilys.formation.battleships.android.ui.ships.DrawableSubmarine;

import java.util.Arrays;
import java.util.List;

import battleships.AIPlayer;
import battleships.Board;
import battleships.Player;
import battleships.ship.AbstractShip;
import battleships.ship.BattleShip;

public class BattleShipsApplication extends Application {

    /* ***
     * Attributes
     */
    private static BoardController mBoard;
    private static Board mOpponentBoard;
    private static Player[] mPlayers;
    private static Game mGame;


    /* ***
     * Lifecycle
     */

    @Override
    public void onCreate() {
        super.onCreate();
        mGame = new Game();
    }


    /* ***
     * Methods
     */

    public static Player[] getPlayers() {
        return mPlayers;
    }

    public static BoardController getBoard() {
        return mBoard;
    }

    public static Board getOpponentBoard() {
        return mOpponentBoard;
    }

    public static Game getGame() { return mGame; }
    /* ***
     * Nested classes
     */


    //TODO copy paste Game class
    public class Game {
        /* ***
         * Attributes
         */
        private Player mPlayer1;
        private Player mPlayer2;
        /* ***
         * Methods
         */
        public Game() {
        }

        public Game init(String playerName) {

            Board b = new Board(playerName);
            mBoard = new BoardController(b);
            mOpponentBoard = new Board("IA");

            mPlayer1 = new AndroidPlayer(playerName, b, mOpponentBoard, createModifiedShips());
            mPlayer2 = new AIPlayer(playerName, mOpponentBoard, b, createModifiedShips());

            // place player ships
            mPlayer1.putShips();
            mPlayer2.putShips();
            mPlayers = new Player[] {mPlayer1, mPlayer2};

            return this;
        }

        private List<AbstractShip> createDefaultShips() {
            AbstractShip[] ships = new AbstractShip[0];

            // TODO uncomment me
            ships = new AbstractShip[]{new DrawableDestroyer(), new DrawableSubmarine(), new DrawableSubmarine(), new DrawableBattleship(), new DrawableCarrier()};
            return Arrays.asList(ships);
        }

        private List<AbstractShip> createModifiedShips() {
            AbstractShip[] ships = new AbstractShip[0];

            // TODO uncomment me
            ships = new AbstractShip[]{new DrawableFrigate(), new DrawableDestroyer(), new DrawableSubmarine(), new DrawableBattleship(), new DrawableCarrier()};
            return Arrays.asList(ships);
        }
    }


    // TODO inherit from Player
    public class AndroidPlayer extends Player {

        public AndroidPlayer(String name, Board board, Board opponentBoard, List<AbstractShip> ships) {
            //TODO call Players's constructor
            super(name, board, opponentBoard, ships);
        }

        //TODO insert putShips() here by overriding player's method
        @Override
        public void putShips() {
            Intent intent = new Intent(getBaseContext(), PutShipsActivity.class);
            startActivity(intent);
        }

    }

}
