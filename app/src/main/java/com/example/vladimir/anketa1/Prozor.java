package com.example.vladimir.anketa1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

public class Prozor extends AppCompatActivity {
    ConstraintLayout okvirSkrola;
    public static List<Pitanje> listaPitanja;
    StringBuilder jsonOdgovori = new StringBuilder();
    public static String kod;
    public static int anketa;
    public static Context kontekst;

    TextView tvPitanje;
    static int pitanje=0;
    public static int brojPitanja=0;

    @Override
    protected void onResume() {
        super.onResume();
        if(listaPitanja == null && kod.length() == 5) {
            Log.i("kod", "onResueme");
            UcitajPitanja task = new UcitajPitanja();
          //  task.execute("http://94.127.6.8/android2/ucitaj/get_anketa.php");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_prozor);

        kontekst = getApplicationContext();

        tvPitanje = (TextView) findViewById(R.id.pitanje);
        okvirSkrola = (ConstraintLayout) findViewById(R.id.okvirSkrola) ;

        SharedPreferences data = getSharedPreferences("kod", getApplicationContext().MODE_PRIVATE);
        kod = data.getString("kod", "");
        if(kod.length() != 5) {
            Log.e("kod", "otvaram KodAnkete activity");
            Intent ak = new Intent(this, KodAnkete.class);
            startActivity(ak);
        }
        tvPitanje.setText(kod);

        tvPitanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor pref = getApplicationContext().getSharedPreferences("A_PREFS_FILE", 0).edit();
                pref.clear();
                pref.apply();
                startActivity(new Intent(getApplicationContext(), KodAnkete.class));
            }
        });

        if(listaPitanja == null && kod.length() == 5) {
                  UcitajPitanja task = new UcitajPitanja();
                task.execute("http://94.127.6.8/android2/services/get_survey/get_anketa.php");
        }


    }

    public void sledecePitanje(View v) {
        //da li je nesto upisano u otvoreno pitanje
        if(PitanjeTip12.edit!=null) PitanjeTip12.edit.clearFocus();
        if(listaPitanja.get(pitanje).brojIzabranihOdgovora>0 || listaPitanja.get(pitanje).getTip()==4) {
            jsonOdgovori.append(listaPitanja.get(pitanje).getJSONOdgovori());
           if(pitanje==brojPitanja-1){
               Toast.makeText(getApplicationContext(), "Cuvam odgovore", Toast.LENGTH_SHORT).show();
               SacuvajOdgovore task = new SacuvajOdgovore();
               task.execute("http://94.127.6.8/android2/services/save_survey_data/save_data.php");
           } else {
               Toast.makeText(getApplicationContext(), pitanje+1 + "/" + brojPitanja, Toast.LENGTH_SHORT).show();
               nacrtajSledecePitanje();
           }

        } else {
            Toast.makeText(getApplicationContext(), "Izberite odgovor", Toast.LENGTH_SHORT).show();
        }
    }

    private void nacrtajSledecePitanje() {
        pitanje++;
        Log.i("pit", pitanje + "");
        PitanjeTip12 novoPitanje = new PitanjeTip12(listaPitanja.get(pitanje), tvPitanje, okvirSkrola, getApplicationContext());
        novoPitanje.nacrtajPitanje();
        nacrtajTok();
    }

    private void nacrtajTok() {
        TextView tv = (TextView) findViewById(R.id.tok);
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<listaPitanja.size(); i++) {
           if(i==pitanje) {
               sb.append("o");
           } else {
              // char c = '\uF06C';
               sb.append("o");
           }
        }

        SpannableString ss1=  new SpannableString(sb.toString());
       // ss1.setSpan(new RelativeSizeSpan(2f), pitanje,pitanje+1, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.RED), pitanje, pitanje+1, 0);// set color
        //Log.i("span", ss1.toString());
        tv.setText(ss1);
    }

    private class UcitajPitanja extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i("url", "doInBackground " + params[0] + " " + kod);
            return HttpManager.getData(params[0], "&kod="+kod);
        }

        @Override
        protected void onPostExecute(String result) {
           // Log.i("url", "onPostExecute" + result);
            JSONParser.parseGetPitanjaOdgovori(result);

            if(listaPitanja != null) {
                //uspesno ucitano
                pitanje=-1;
                nacrtajSledecePitanje();
            } else {
                Toast.makeText(getApplicationContext(), "Pitnja nisu ucitana", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class SacuvajOdgovore extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.i("url", "doInBackground " + params[0]);
            String post = jsonOdgovori.toString();
            return HttpManager.getData(params[0], "&anketa="+anketa+"&odgovori=" + "[" + post.substring(0, post.length()-1) + "]");
        }

        @Override
        protected void onPostExecute(String result) {
            JSONParser.parseSaveOdgovor(result);
        }
    }

}


