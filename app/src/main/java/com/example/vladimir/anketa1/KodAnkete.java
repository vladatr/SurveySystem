package com.example.vladimir.anketa1;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class KodAnkete extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kod_ankete);
    }

    public void saveCode(View v) {
        EditText etKod = (EditText) findViewById(R.id.etKod);
        String kod = etKod.getText().toString();
        if (kod.length() != 5) {
            Toast.makeText(getApplicationContext(), "Code must have at least 5 chars.", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor =getSharedPreferences("kod", getApplicationContext().MODE_PRIVATE).edit();
        editor.putString("kod", kod).apply();
        provera();
        this.finish();
    }

    private void provera() {
        SharedPreferences sp = getSharedPreferences("kod", getApplicationContext().MODE_PRIVATE);
        String kod = sp.getString("kod", "");
        Toast.makeText(getApplicationContext(), kod, Toast.LENGTH_SHORT).show();

    }
}
