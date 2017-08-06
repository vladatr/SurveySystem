package com.example.vladimir.anketa1;


 class Odgovor {
    private int pitanje;
    private int redni_broj;
    private boolean otvoreno;
    private String odgovor;

     public int getSkok() {
         return skok;
     }

     public void setSkok(int skok) {
         this.skok = skok;
     }

     private int skok;


     boolean isOtvoreno() {
        return otvoreno;
    }

     void setOtvoreno(boolean otvoreno) {
        this.otvoreno = otvoreno;
    }

    public String toString() {
        return redni_broj + ". " + odgovor ;
    }

     int getPitanje() {
        return pitanje;
    }

     void setPitanje(int pitanje) {
        this.pitanje = pitanje;
    }

     int getRedni_broj() {
        return redni_broj;
    }

     void setRedni_broj(int redni_broj) {
        this.redni_broj = redni_broj;
    }

     String getOdgovor() {
        return odgovor;
    }

     void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }




}

