package com.example.vladimir.anketa1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class Main extends AppCompatActivity {
    ConstraintLayout okvirSkrola;
    public static List<Pitanje> listaPitanja;
    StringBuilder jsonOdgovori = new StringBuilder();
    public static String kod;
    public static int broj_ankete;
    public static String naziv_ankete;
    public static Context kontekst;

    TextView tvPitanje;
    TextView tvNazivAnkete;
    static int pitanje=0;
    public static int brojPitanja=0;

    private boolean zahtevPoslat =false;

    @Override
    protected void onResume() {
        super.onResume();
        if(listaPitanja == null && kod.length() == 5 && !zahtevPoslat) {
            UcitajPitanja task = new UcitajPitanja();
            task.execute("http://94.127.6.8/android2/services/get_survey/get_anketa.php");
            zahtevPoslat = true;
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
        tvNazivAnkete = (TextView) findViewById(R.id.txtAnketa);
        okvirSkrola = (ConstraintLayout) findViewById(R.id.okvirSkrola) ;

        SharedPreferences data = getSharedPreferences("kod", getApplicationContext().MODE_PRIVATE);
        kod = data.getString("kod", "");
        if(TextUtils.isEmpty(kod)) {
            Log.e("kod", "otvaram KodAnkete activity");
            Intent ak = new Intent(this, KodAnkete.class);
            startActivity(ak);
        }
        Toast.makeText(getApplicationContext(), "kod: " + kod, Toast.LENGTH_SHORT).show();

        if(listaPitanja == null && kod.length() == 5  && !zahtevPoslat) {
            UcitajPitanja task = new UcitajPitanja();
            task.execute("http://94.127.6.8/android2/services/get_survey/get_anketa.php");
        }
    }

    public void insertCode(View v){
        SharedPreferences.Editor pref = getApplicationContext().getSharedPreferences("A_PREFS_FILE", 0).edit();
        pref.clear();
        pref.apply();
        startActivity(new Intent(getApplicationContext(), KodAnkete.class));
    }

    public void sledecePitanje(View v) {
        if(listaPitanja.get(pitanje).brojIzabranihOdgovora>0 ||
                listaPitanja.get(pitanje).izabranOtovren ||
                listaPitanja.get(pitanje).getTip()==4) {
            //procitaj odg.
            jsonOdgovori.append(listaPitanja.get(pitanje).getJSONOdgovori());
            //nadji sledec epitanje
            if( listaPitanja.get(pitanje).getSledecePitanje() == 0) {
                pitanje++;
            } else {
                pitanje = listaPitanja.get(pitanje).getSledecePitanje()-1;
            }

           if(pitanje==brojPitanja || pitanje==98){
               Toast.makeText(getApplicationContext(), "Saving the Answers", Toast.LENGTH_SHORT).show();
               SacuvajOdgovore task = new SacuvajOdgovore();
               task.execute("http://94.127.6.8/android2/services/save_survey_data/save_data.php");
           } else {
              // Toast.makeText(getApplicationContext(), pitanje+1 + "/" + brojPitanja, Toast.LENGTH_SHORT).show();
               nacrtajSledecePitanje();
           }

        } else {
            Toast.makeText(getApplicationContext(), "Izberite odgovor", Toast.LENGTH_SHORT).show();
        }
    }

    private void nacrtajSledecePitanje() {
        Log.i("pit", pitanje + ".u fji nacrtajSledPit");
        PitanjeTip12 novoPitanje = new PitanjeTip12(listaPitanja.get(pitanje), tvPitanje, okvirSkrola);
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
        tv.setText(ss1);
    }

    private class UcitajPitanja extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i("url", "UcitajPitanja doInBackground " + params[0] + " " + kod);
            return HttpManager.getData(params[0], "&kod="+kod);
        }

        @Override
        protected void onPostExecute(String result) {
            zahtevPoslat=false;
           Log.i("url", "onPostExecute" + result);
            if(result!=null) {
                JSONParser.parseGetPitanjaOdgovori(result);
            } else {
                Toast.makeText(getApplicationContext(), "Error in connecting to the server!", Toast.LENGTH_SHORT).show();
            }

            if(listaPitanja != null) {
                //uspesno ucitano
                tvNazivAnkete.setText(naziv_ankete);
                nacrtajSledecePitanje();
            } else {
                Toast.makeText(getApplicationContext(), "Survey isn't loaded!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class SacuvajOdgovore extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String post = jsonOdgovori.toString();
            Log.i("url", "SAVE doInBackground " + params[0] + " " + post);

            return HttpManager.getData(params[0], "&anketa="+broj_ankete+"&odgovori=" + "[" + post.substring(0, post.length()-1) + "]");
        }

        @Override
        protected void onPostExecute(String result) {
            JSONParser.parseSaveOdgovor(result);
            Toast.makeText(getApplicationContext(), "Answers are saved!", Toast.LENGTH_SHORT).show();

           okvirSkrola.removeAllViews();

            TextView tv = new TextView(kontekst);
            tv.setText("Answers are saved on server. New survey can begin in 10s.");
            tv.setTextSize(30);
            tv.setTextColor(Color.rgb(250,150,50));
            tvPitanje.setText("");
            listaPitanja = null;
            jsonOdgovori = new StringBuilder();
            pitanje=0;
            brojPitanja=0;
            okvirSkrola.addView(tv);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    UcitajPitanja task = new UcitajPitanja();
                    task.execute("http://94.127.6.8/android2/services/get_survey/get_anketa.php");
                }
            }, 10000);


        }
    }


}


