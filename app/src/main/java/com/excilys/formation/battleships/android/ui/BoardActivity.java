package com.excilys.formation.battleships.android.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import battleships.Board;
import battleships.Hit;
import battleships.Player;
import battleships.formation.excilys.com.battleships.R;
import battleships.ship.AbstractShip;


public class BoardActivity extends AppCompatActivity implements BoardGridFragment.BoardGridFragmentListener {
    private static final String TAG = BoardActivity.class.getSimpleName();

    private SharedPreferences mPreferences;

    @Override
    public void onTileClick(int id, int x, int y) {
        if (id == BoardController.HITS_FRAGMENT && mPlayerTurn) {
            doPlayerTurn(x, y);
        }
    }

    private static class Default {
        private static final int TURN_DELAY = 5000; // ms
    }

    /* ***
     * Widgets
     */
    /** contains BoardFragments to display ships & hits grids */
    private CustomViewPager mViewPager;
    private TextView mInstructionTextView;
    private CoordinatorLayout mCoordinatorLayout;

    /* ***
     * Attributes
     */
    private BoardController mBoardController;
    private Board mOpponentBoard;
    private Player mOpponent;
    private boolean mDone = false;
    private boolean mPlayerTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup layout
        setContentView(R.layout.activity_game_session);

        // init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.board_viewpager);
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(BoardController.HITS_FRAGMENT);

        mInstructionTextView = (TextView) findViewById(R.id.instruction_textview);

        // Init the Board Controller (to create BoardGridFragments)
        mBoardController = BattleShipsApplication.getBoard();
        mOpponentBoard = BattleShipsApplication.getOpponentBoard();
        mOpponent = BattleShipsApplication.getPlayers()[1];
    }

    // TODO  call me maybe
    private void doPlayerTurn(int x, int y) {

        mPlayerTurn = false;
        Hit hit = mOpponentBoard.sendHit(x, y);
        System.out.println(hit);
        if (hit == Hit.ALREADY_STRIKE) {
            mPlayerTurn = true;
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Already hit", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }
        if (mBoardController.getHit(x,y) != null && !mBoardController.getHit(x,y)) {
            mPlayerTurn = true;
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Already missed", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }
        boolean strike = hit != Hit.MISS;

        mBoardController.setHit(strike, x, y);

        if (strike) {
            mPlayerTurn = true;
            mDone = updateScore();
            if (mDone) {
                gotoScoreActivity();
            }
            else {
                mOpponentBoard.surroundShipWithMiss(mBoardController, x, y);
            }

        } else {
            // TODO sleep a while...
            sleep(Default.TURN_DELAY);
            mViewPager.setEnableSwipe(false);
            doOpponentTurn();
            mViewPager.setCurrentItem(BoardController.SHIPS_FRAGMENT);
        }
        String msgToLog = String.format(Locale.US, "Hit (%d, %d) : %s", x, y, strike);
        Log.d(TAG, msgToLog);

        showMessage(makeHitMessage(false, new int[] {x,y}, hit));
    }

    private void doOpponentTurn() {
        new AsyncTask<Void, String, Boolean>() {
            private String DISPLAY_TEXT = "0", DISPLAY_HIT = "1";

            @Override
            protected Boolean doInBackground(Void... params) {
                Hit hit;
                boolean strike;
                do {
                    sleep(Default.TURN_DELAY);
                    publishProgress("...");
                    sleep(Default.TURN_DELAY);

                    int[] coordinate = new int[2];
                    hit = mOpponent.sendHit(coordinate);
                    strike = hit != Hit.MISS;

                    List<int[]> coordinates = mBoardController.surroundShipWithMiss(mOpponentBoard, coordinate[0], coordinate[1]);
                    for (int[] coord : coordinates) {
                        publishProgress(DISPLAY_HIT, String.valueOf(false), String.valueOf(coord[0]), String.valueOf(coord[1]));
                    }

                    publishProgress(DISPLAY_TEXT, makeHitMessage(true, coordinate, hit));
                    publishProgress(DISPLAY_HIT, String.valueOf(strike), String.valueOf(coordinate[0]), String.valueOf(coordinate[1]));

                    mDone = updateScore();
                    sleep(Default.TURN_DELAY);

                } while(strike && !mDone);
                return mDone;
            }

            @Override
            protected void onPostExecute(Boolean done) {
                if (!done) {
                    mViewPager.setEnableSwipe(true);
                    mViewPager.setCurrentItem(BoardController.HITS_FRAGMENT);
                    mPlayerTurn = true;
                } else {
                    gotoScoreActivity();
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                if (values[0].equals(DISPLAY_TEXT)) {
                    showMessage(values[1]);
                } else if (values[0].equals(DISPLAY_HIT)) {
                    mBoardController.displayHitInShipBoard(Boolean.parseBoolean(values[1]), Integer.parseInt(values[2]), Integer.parseInt(values[3]));
                }
            }


        }.execute();

    }

    private void gotoScoreActivity() {
        mPlayerTurn = false;
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra(ScoreActivity.Extra.WIN, mOpponent.lose);
        startActivity(intent);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a BoardGridFragment
            switch (position) {
                case BoardController.SHIPS_FRAGMENT:
                case BoardController.HITS_FRAGMENT:
                    return mBoardController.getFragments()[position];

                default:
                    throw new IllegalStateException("BoardController doesn't support fragment position : " + position);
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case BoardController.SHIPS_FRAGMENT:
                case BoardController.HITS_FRAGMENT:
                    return mBoardController.getFragments()[position].getName();
            }

            return null;
        }
    }

    private boolean updateScore() {
        for (Player player : BattleShipsApplication.getPlayers()) {
            int destroyed = 0;
            for (AbstractShip ship : player.getShips())
                if (ship.isSunk()) {
                    destroyed++;
                }

            player.destroyedCount = destroyed;
            player.lose = destroyed == player.getShips().length;
            if (player.lose) {
                return true;
            }
        }
        return false;
    }

    private String makeHitMessage(boolean incoming, int[] coords, Hit hit) {
        String msg;
        switch (hit) {
            case MISS:
                msg = hit.toString();
                break;
            case STIKE:
                msg = hit.toString();
                break;
            default:
                msg = String.format(getString(R.string.board_ship_sunk_format), hit.toString());
        }
        msg = String.format(getString(R.string.board_ship_hit_format), incoming ? "IA" : BattleShipsApplication.getPlayers()[0].getName(),
                ((char) ('A' + coords[0])),
                (coords[1] + 1), msg);
        return msg;
    }

    private void showMessage(String msg) {
        mInstructionTextView.setText(msg);
    }

    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_restart_game:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Restart game?");

// Add the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BattleShipsApplication.getGame().init(BattleShipsApplication.getPlayers()[0].getName());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

// Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default:
                break;
        }
        return true;
    }
}
