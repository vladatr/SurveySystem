package com.example.vladimir.anketa1;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



class Pitanje {
   private int redni_broj;
    private String pitanje;
    private int tip;
    List<Odgovor> listaOdgovora = new ArrayList<>();
     int brojOdgovora=0;
    //izabrani odgovori...za tip 1 izabrani odgovor je na poziciji 0
    List<Integer> izabraniOdgovori = new ArrayList<>();
    int brojIzabranihOdgovora=0;
    //posebno se cuvaju otvoreni
    boolean izabranOtovren=false;

     void dodajOdgovor(Odgovor odg) {
        listaOdgovora.add(odg);
         izabraniOdgovori.add(brojOdgovora, 0);
         brojOdgovora++;
    }

    String getJSONOdgovori() {
        StringBuilder sb = new StringBuilder();
        try {
          if(tip==1) {
            sb.append("{\"pitanje\":");
              sb.append(redni_broj + ", ");
            //odstampaj sve odgovore
                int izabranaStavka = izabraniOdgovori.get(0);
                sb.append("\"odgovor\":");
              sb.append(izabranaStavka+", ");
                //ako je otvoren, sacuvaj i odgovor
                if(listaOdgovora.get(izabranaStavka-1).isOtvoreno()) {
                    sb.append("\"otvoreno\":\"");
                    sb.append(listaOdgovora.get(izabranaStavka-1).getOdgovor() + "\"},");
                } else {
                    sb.append("\"otovreno\":\"\"},");
                }

            } else if (tip==2) {

                for(int i=0; i<izabraniOdgovori.size(); i++){
                           if(izabraniOdgovori.get(i)>0) {
                               JSONObject jsonOdgovor = new JSONObject();
                               sb.append("{\"pitanje\":");
                               sb.append(redni_broj + ", ");
                               sb.append("\"odgovor\":");
                               sb.append(i+1 + ", ");
                               if(listaOdgovora.get(izabraniOdgovori.get(i)-1).isOtvoreno()) {
                                   sb.append("\"otvoreno\":\"");
                                   sb.append(listaOdgovora.get(izabraniOdgovori.get(i)-1).getOdgovor() + "\"},");
                                } else {
                                   sb.append("\"otvoreno\":\"\"},");
                               }

                            }
                 }

            }
        }   catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("save", sb.toString());
        return sb.toString();
    }

    void unesiOtvoreno(int redbrOdg, String tekst) {
        Log.e("klik", "unosim otvoreno " + tekst + "..." + redbrOdg);
        if(tip==1) {
            if(TextUtils.isEmpty(tekst)) {
                //tekst je izbrisan
                izabranOtovren=false;
                izabraniOdgovori.set(0, 0);
                listaOdgovora.get(redbrOdg-1).setOdgovor("");
            } else {
                izabraniOdgovori.set(0, redbrOdg);
                listaOdgovora.get(redbrOdg-1).setOdgovor(tekst);
                izabranOtovren=true;
                brojOdgovora--; //otvoren se cuva u booleanu
            }
        } else if(tip==2) {
            if(TextUtils.isEmpty(tekst)) {
                //tekst je izbrisan
                izabranOtovren=false;
                izabraniOdgovori.set(redbrOdg-1, 0);
                listaOdgovora.get(redbrOdg-1).setOdgovor("");
            } else {
                izabraniOdgovori.set(redbrOdg-1, redbrOdg);
                listaOdgovora.get(redbrOdg-1).setOdgovor(tekst);
                izabranOtovren=true;
            }
        }
        Log.e("klik", printOdgovori());
    }

    int izaberiOdgovor(int redbrOdg) {
        //ovaj metod moze da vrati 1, ako je nesto izabrano ili 0 ako je iskljuceno
        if(tip==1) {
            if(izabraniOdgovori.get(0)==redbrOdg) return 0; //vec izabran
            izabraniOdgovori.set(0, redbrOdg);
            Log.i("klik", "izabrano " + redbrOdg);
            brojIzabranihOdgovora=1;
            return 1;
        } else if (tip==2) {
            if(izabraniOdgovori.get(redbrOdg-1)==0) {
                //ukljucuje se stavka
                izabraniOdgovori.set(redbrOdg-1, redbrOdg);
                Log.i("klik", "ukljucujem " + redbrOdg);
                brojIzabranihOdgovora++;
                return 1;
            } else {
                //iskljucuje se stavka
                izabraniOdgovori.set(redbrOdg-1, 0);
                Log.i("klik", "iskljucujem " + redbrOdg);
                brojIzabranihOdgovora--;
                return 0;
            }

        }
        return 0;
    }

    public String printOdgovori() {
        StringBuilder sb = new StringBuilder();
        for (int a : izabraniOdgovori) {
            sb.append(a + ",");
          // if(listaOdgovora.get(a-1) != null) sb.append(" uneto: |" + listaOdgovora.get(a-1).getOdgovor() + "|, ");
        }
        sb.append("...broj odg: " + brojIzabranihOdgovora);

        return sb.toString();
    }

    public String toString() {
        return redni_broj + ". " + pitanje + " (" + tip + ". tip)";
    }


    public int getRedni_broj() {
        return redni_broj;
    }

     void setRedni_broj(int redni_broj) {
        this.redni_broj = redni_broj;
    }

    public String getPitanje() {
        return pitanje;
    }

     void setPitanje(String pitanje) {
        this.pitanje = pitanje;
    }

    public int getTip() {
        return tip;
    }

     void setTip(int tip) {
        this.tip = tip;
    }
}
