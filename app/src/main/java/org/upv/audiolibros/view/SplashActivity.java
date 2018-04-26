package org.upv.audiolibros.view;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.upv.audiolibros.R;
import org.upv.audiolibros.view.list.ui.BookListActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this, BookListActivity.class);
        ActivityOptions opts = ActivityOptions.makeCustomAnimation(this, R.anim.entrada_izquierda, R.anim.salida_derecha);
        startActivity(intent, opts.toBundle());
    }
}
