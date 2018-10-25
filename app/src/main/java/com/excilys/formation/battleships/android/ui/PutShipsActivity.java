package com.excilys.formation.battleships.android.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import battleships.formation.excilys.com.battleships.R;
import battleships.ship.AbstractShip;

public class PutShipsActivity extends AppCompatActivity implements BoardGridFragment.BoardGridFragmentListener {
    private static final String TAG = PutShipsActivity.class.getSimpleName();


    private SharedPreferences mPreferences;
    /* ***
     * Widgets
     */
    private CoordinatorLayout mCoordinatorLayout;
    private RadioGroup mOrientationRadioGroup;
    private RadioButton mNorthRadio;
    private RadioButton mSouthRadio;
    private RadioButton mEastRadio;
    private RadioButton mWestRadio;
    private TextView mShipName;
    private Button mGoToGameActivityButton;

    /* ***
     * Attributes
     */
    private BoardController mBoard;
    private int mCurrentShip;
    private AbstractShip[] mShips;
    Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = getApplicationContext().getSharedPreferences("Pref", MODE_PRIVATE);

        // Setup the layout
        setContentView(R.layout.activity_put_ships);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);

        // Init the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mOrientationRadioGroup = (RadioGroup) findViewById(R.id.putship_radios_orientation);
        mOrientationRadioGroup.setOnCheckedChangeListener(new ShipOrientationChangeListener());

        mNorthRadio = (RadioButton) findViewById(R.id.radio_north);
        mSouthRadio = (RadioButton) findViewById(R.id.radio_south);
        mEastRadio = (RadioButton) findViewById(R.id.radio_east);
        mWestRadio = (RadioButton) findViewById(R.id.radio_west);
        mShipName = (TextView) findViewById(R.id.ship_name);
        mGoToGameActivityButton = (Button) findViewById(R.id.putship_gotogame_button);

        // init board controller to create BoardGridFragments
        int playerId = 0;
        mCurrentShip = 0;
        mBoard = BattleShipsApplication.getBoard();
        mShips = BattleShipsApplication.getPlayers()[playerId].getShips();

        mFragment = mBoard.getFragments()[BoardController.SHIPS_FRAGMENT];
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.putships_fragment_container,
                            mFragment)
                    .commit();
        }

        updateRadioButton();
        updateNextShipNameToDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_name:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Edit player name");

                String name = mPreferences.getString("player_name", "");

                final EditText input = new EditText(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setText(name);
                builder.setView(input);

// Add the buttons
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mPreferences.edit().putString("player_name", String.valueOf(input.getText())).apply();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    @Override
    public void onTileClick(int boardId, int x, int y) {
        String msg;
        msg = String.format(Locale.US, "put ship : (%d, %d)", x, y);
        Log.d(TAG, msg);

        try {
            mBoard.putShip(mShips[mCurrentShip], x, y);
            mCurrentShip++;
            updateNextShipNameToDisplay();
        } catch (Exception e) {
            if (mCurrentShip >= mShips.length) {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "No more ships to place", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
            else {
                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.put_ship_error, Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }

        if (mCurrentShip >= mShips.length) {
            mGoToGameActivityButton.setVisibility(View.VISIBLE);
        } else {
            updateRadioButton();
        }
    }

    private void gotoBoardActivity() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(mFragment)
                .commit();

        Intent intent = new Intent(this, BoardActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateRadioButton() {

        switch (mShips[mCurrentShip].getOrientation()) {
            case NORTH:
                mNorthRadio.setChecked(true);
                break;
            case SOUTH:
                mSouthRadio.setChecked(true);
                break;
            case EAST:
                mEastRadio.setChecked(true);
                break;
            case WEST:
                mWestRadio.setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {

    }

    private class ShipOrientationChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.radio_east:
                    mShips[mCurrentShip].setOrientation(AbstractShip.Orientation.EAST);
                    break;
                case R.id.radio_north:
                    mShips[mCurrentShip].setOrientation(AbstractShip.Orientation.NORTH);
                    break;
                case R.id.radio_south:
                    mShips[mCurrentShip].setOrientation(AbstractShip.Orientation.SOUTH);
                    break;
                case R.id.radio_west:
                    mShips[mCurrentShip].setOrientation(AbstractShip.Orientation.WEST);
                    break;
            }
        }
    }

    private void updateNextShipNameToDisplay() {
        if (mCurrentShip < mShips.length) {
            mShipName.setText(mShips[mCurrentShip].getName());
        }
    }

    public void onGoToGameActivityClick(View v) {
        gotoBoardActivity();
    }

    public void onResetShipsClick(View v) {
        resetShips();
    }

    private void resetShips() {
        mCurrentShip = 0;
        int playerId = 0;
        BattleShipsApplication.getGame().init( BattleShipsApplication.getPlayers()[playerId].getName());
    }
}
