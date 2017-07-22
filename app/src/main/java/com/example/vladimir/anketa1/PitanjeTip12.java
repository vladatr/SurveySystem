package com.example.vladimir.anketa1;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
    private List<View> vListaOdgovor = new ArrayList<>();
    static EditText edit=null;


    PitanjeTip12(Pitanje pitanje, TextView tvPitanje, ConstraintLayout okvirOdgovora) {
        this.pitanje = pitanje;
        this.tvPitanje = tvPitanje;
        this.okvirOdgovora = okvirOdgovora;
    }

     void nacrtajPitanje() {
        tvPitanje.setText(pitanje.toString());
        okvirOdgovora.removeAllViews();
        int i=0;
        for(Odgovor odg : pitanje.listaOdgovora ) {
            View v;
            if(odg.isOtvoreno()) {
                EditText tv = new EditText(Main.kontekst);
                tv.setTextColor(Color.LTGRAY);
                tv.setHint("Your answer...");
                tv.setWidth(1000);
                tv.setHeight(150);
                tv.setTag(i+1);
                tv.setId(i+100);
                tv.setTextSize(23);
                tv.setPadding(25,10,10,15);
                tv.setBackgroundResource(R.drawable.border);
                edit=tv;
                    v = tv;

                tv.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        if(pitanje.getTip()==1) {
                            for(View tv : vListaOdgovor) {
                                tv.setBackgroundResource(R.drawable.border);
                            }
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        int redbrOdg = (int) edit.getTag();
                        String upisano = s.toString().trim();
                        Log.i("klik", "onTextChanged: " + upisano);
                        pitanje.unesiOtvoreno(redbrOdg, upisano);
                    }
                });


            } else {
                TextView tv = new TextView(Main.kontekst);
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
                        if(edit != null) {
                            if(TextUtils.isEmpty(edit.getText().toString())) edit.setText(""); //izbrisi ako izaberes nesto drugo
                        }
                        int redbrOdg = (int) v.getTag();
                        int izabrano = pitanje.izaberiOdgovor(redbrOdg);
                        if(pitanje.getTip()==1 && izabrano==1) {
                            for(View tv : vListaOdgovor) {
                                tv.setBackgroundResource(R.drawable.border);
                            }
                            v.setBackgroundColor(Color.rgb(100,160,190));
                        }
                        if(pitanje.getTip()==2) {
                            if(izabrano==0) {
                                v.setBackgroundResource(R.drawable.border);
                            } else {
                                v.setBackgroundColor(Color.rgb(100,160,190));
                            }
                        }
                       Log.e("klik", pitanje.printOdgovori());
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
