package com.example.vladimir.anketa1;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


class PitanjeTip12 {
    private Pitanje pitanje;
    private TextView tvPitanje;
    private ConstraintLayout okvirOdgovora;
    private Context kontekst;
    private List<View> vListaOdgovor = new ArrayList<>();
    static EditText edit=null;


    PitanjeTip12(Pitanje pitanje, TextView tvPitanje, ConstraintLayout okvirOdgovora, Context kontekst) {
        this.pitanje = pitanje;
        this.tvPitanje = tvPitanje;
        this.okvirOdgovora = okvirOdgovora;
        this.kontekst = kontekst;

    }

     void nacrtajPitanje() {
        tvPitanje.setText(pitanje.toString());
        okvirOdgovora.removeAllViews();
        int i=0;
        for(Odgovor odg : pitanje.listaOdgovora ) {
            View v;
            if(odg.isOtvoreno()) {
                EditText tv = new EditText(kontekst);
                tv.setTextColor(Color.LTGRAY);
                tv.setHint("Unesi nesto drugo...");
                tv.setWidth(1000);
                tv.setHeight(150);
                tv.setTag(i+1);
                tv.setId(i+100);
                tv.setTextSize(23);
                tv.setPadding(25,10,10,15);
                tv.setBackgroundResource(R.drawable.border);
                edit=tv;
                    v = tv;
                tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        Log.i("klik", "fokus 1" + hasFocus);
                        if (!hasFocus) {
                            Log.i("klik", "fokus lost");
                            int redbrOdg = (int) v.getTag();
                            String upisano = ((EditText) v).getText().toString();
                            upisano.trim();
                            if(! upisano.contentEquals("")) {
                                pitanje.unesiOtvoreno(redbrOdg-1, upisano);
                            }

                        }

                    }
                });

            } else {
                TextView tv = new TextView(kontekst);
                tv.setText(odg.getOdgovor());
                    tv.setTextColor(Color.BLACK);
                tv.setWidth(1500);
                tv.setTag(i+1);
                tv.setId(i+100);
                tv.setTextSize(28);
                tv.setPadding(25,10,10,15);
                tv.setBackgroundResource(R.drawable.border);
                    v= tv;
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int redbrOdg = (int) v.getTag();
                        int izabrano = pitanje.izaberiOdgovor(redbrOdg);
                        if(pitanje.getTip()==1 && izabrano==1) {
                            for(View tv : vListaOdgovor) {
                                tv.setBackgroundResource(R.drawable.border);
                            }
                            if(edit != null) edit.setText(""); //izbrisi ako izaberes nesto drugo
                            v.setBackgroundColor(Color.rgb(100,160,190));
                        }
                        if(pitanje.getTip()==2) {
                            if(izabrano==0) {
                                v.setBackgroundResource(R.drawable.border);
                            } else {
                                v.setBackgroundColor(Color.rgb(100,160,190));
                            }
                        }
                    }

            });


            } //kraj crtanja textview

                vListaOdgovor.add(v);
                okvirOdgovora.addView(v);

                ConstraintSet set = new ConstraintSet();
                set.clone(okvirOdgovora);

                if(i==0) {
                    Log.i("okvir", i + ". - " + v.getId() + " ... " + okvirOdgovora.getId());
                    set.connect(v.getId(), ConstraintSet.TOP, okvirOdgovora.getId(), ConstraintSet.TOP, 20 );
                } else {
                    Log.i("okvir", i + ". - " + v.getId() + " ... " + vListaOdgovor.get(i-1).getId());
                    set.connect(v.getId(), ConstraintSet.TOP, vListaOdgovor.get(i-1).getId(), ConstraintSet.BOTTOM, 20 );
                }
                set.applyTo(okvirOdgovora);
            i++;
        }
    }
}
