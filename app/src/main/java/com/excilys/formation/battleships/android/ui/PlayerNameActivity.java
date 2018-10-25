package com.excilys.formation.battleships.android.ui;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import battleships.formation.excilys.com.battleships.R;

public class PlayerNameActivity extends AppCompatActivity {
    public static final String PLAYER_NAME = "PLAYER_NAME";

    private EditText mPlayerName;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_name);

        mPlayerName = (EditText) findViewById(R.id.player_name_edit_text);
        mPreferences = getApplicationContext().getSharedPreferences("Pref", MODE_PRIVATE);

        String name = mPreferences.getString("player_name", "");
        if (!name.isEmpty()) {
            BattleShipsApplication.getGame().init(name);
        }
    }

    public void onClickPlay(View v) {

        String name = mPlayerName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "Type your name first!", Toast.LENGTH_LONG).show();
            return;
        }
        mPreferences.edit().putString("player_name", name).apply();
        BattleShipsApplication.getGame().init(name);

    }
}
