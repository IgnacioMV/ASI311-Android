package com.excilys.formation.battleships.android.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

import battleships.formation.excilys.com.battleships.R;

import static battleships.formation.excilys.com.battleships.R.drawable.win_background;

public class ScoreActivity extends AppCompatActivity {


    public static class Extra {
        public static String WIN = "EXTRA_WIN";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        boolean win = getIntent().getExtras().getBoolean(Extra.WIN);
        TextView winLabel = (TextView) findViewById(R.id.score_win_label);
        TextView loseLabel = (TextView) findViewById(R.id.score_lose_label);

        int winVisible = View.VISIBLE, loseVisible = View.VISIBLE;
        if (win) {
            winVisible = View.GONE;
            loseVisible = View.GONE;
            final int sdk = android.os.Build.VERSION.SDK_INT;
            View layout = findViewById(R.id.activity_score);
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.win_background) );
            } else {
                layout.setBackground(ContextCompat.getDrawable(this, R.drawable.win_background));
            }
        } else {
            winVisible = View.GONE;
        }

        winLabel.setVisibility(winVisible);
        loseLabel.setVisibility(loseVisible);
    }

    public void onPlayAgainClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Play again?");
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
    }

    public void onBackToBoardClick(View v) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {

    }

}
