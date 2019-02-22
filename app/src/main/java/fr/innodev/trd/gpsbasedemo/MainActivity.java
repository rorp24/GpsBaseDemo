package fr.innodev.trd.gpsbasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "fr.innodev.trd.gpsbasedemo.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout LM = (ConstraintLayout) findViewById(R.id.mainLayout);
                int tab[] = new int[2];
                LM.getLocationOnScreen(tab);
                Log.d("INFO", "V1 " + tab[0] + " " + tab[1]);
                Snackbar.make(view, "Quiter l'application", Snackbar.LENGTH_LONG)
                        .setAction("quitter", null).show();
                System.exit(0);
            }
        });

        FloatingActionButton bGps = (FloatingActionButton) findViewById(R.id.GPS);
        bGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Lancer L'application de Localisation", Snackbar.LENGTH_LONG)
                        .setAction("Localisation", null).show();
                startGpsDemo(view);
            }
        });

        FloatingActionButton bLoc = (FloatingActionButton) findViewById(R.id.Login);
        bLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Se connecter", Snackbar.LENGTH_LONG)
                        .setAction("Connexion", null).show();
                startLoginDBDemo(
                        view);
            }
        });

    }

    /** Called when the user taps the Send button */
    public void startGpsDemo(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Send button */
    public void startLoginDBDemo(View view) {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}
