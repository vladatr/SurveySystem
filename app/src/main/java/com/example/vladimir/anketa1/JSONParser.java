package com.example.vladimir.anketa1;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


class JSONParser {

    static void parseSaveOdgovor(String s) {
        try {

            JSONObject obj = new JSONObject(s);
            String status = obj.getString("status");
            String info = obj.getString("info");
            String error = obj.getString("error");
            Log.i("url", status + " " + info + " " + error);
            if(status.contentEquals("200")) {
                //sve ok

            }
        } catch (Exception e) {
            Log.e("url", "parseSacuvajOdgovor err " + e.toString());
            e.printStackTrace();
        }
    }

    static void parseGetPitanjaOdgovori(String s) {
        try {
            Log.i("url", s);
            JSONObject object = new JSONObject(s);

            JSONArray arInfo = object.getJSONArray("info");
            JSONObject obji = arInfo.getJSONObject(0);
            Prozor.anketa = obji.getInt("broj_ankete");
            String error = obji.getString("error");

            Log.i("get", Prozor.kod + " " + Prozor.anketa + ". " + error);
            if(!TextUtils.isEmpty(error)) {
                Toast.makeText(Prozor.kontekst, "Greska: " + error, Toast.LENGTH_SHORT).show();
            }

            //PITANJA
            JSONArray ar  = object.getJSONArray("pitanja");
             List<Pitanje> listaPitanja = new ArrayList<>();
            Log.i("url", "3");
            Prozor.brojPitanja = ar.length();
            for(int i=0; i<ar.length(); i++) {
                JSONObject obj = ar.getJSONObject(i);
                Pitanje pit = new Pitanje();
                pit.setRedni_broj(obj.getInt("redni_broj"));
                pit.setPitanje(obj.getString("pitanje"));
                pit.setTip(obj.getInt("tip"));
                listaPitanja.add(pit);
            }
            Prozor.listaPitanja= listaPitanja;

            //ODGOVORI
            JSONArray ar2 = object.getJSONArray("odgovori");
            for(int i=0; i<ar2.length(); i++) {
                JSONObject obj = ar2.getJSONObject(i);
                Odgovor odg = new Odgovor();
                odg.setPitanje(obj.getInt("pitanje"));
                odg.setRedni_broj(obj.getInt("redni_broj"));
                odg.setOdgovor(obj.getString("odgovor"));
                odg.setOtvoreno(obj.getInt("otvoreno")==1 ? true : false);
               // dodajme odgovor za pitanje
                Log.i("url", odg.getPitanje() + " " + odg.getRedni_broj()  );
                Prozor.listaPitanja.get(odg.getPitanje()-1).dodajOdgovor(odg);
            }
        } catch (Exception e) {
            Log.i("url", "parseString err " + e.toString());
            e.printStackTrace();
            Prozor.listaPitanja = null;
        }

    }
}
