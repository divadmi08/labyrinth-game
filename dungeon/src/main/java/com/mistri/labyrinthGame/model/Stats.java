package com.mistri.labyrinthGame.model;


public class Stats {

    private int forza;
    private int vita;
    private int mana;
    private int intelligenza;
    private int stamina;

    private static final int MAX = 100;

    public Stats(int forza, int vita, int mana, int intelligenza, int stamina) {
        this.forza = limita(forza);
        this.vita = limita(vita);
        this.mana = limita(mana);
        this.intelligenza = limita(intelligenza);
        this.stamina = limita(stamina);
    }

    private int limita(int v){
        return Math.min(MAX, Math.max(0, v));
    }

    public void aumentaForza(int v){
        forza = limita(forza+v);
    }
    public void aumentaVita(int v){
        vita = limita(vita+v);
    }
    public void aumentaMana(int v){
        mana = limita(mana+v);
    }
    public void aumentaIntelligenza(int v){
        intelligenza = limita(intelligenza+v);
    }
    public void aumentaStamina(int v){
        stamina = limita(stamina+v);
    }

    public int getForza(){
        return forza;
    }
    public int getVita(){
        return vita;
    }
    public int getMana(){
        return mana;
    }
    public int getIntelligenza(){
        return intelligenza; }
    public int getStamina(){ return stamina;
    }

    public void mostra(){
        System.out.println("FOR:"+forza+" VIT:"+vita+" MANA:"+mana+ " INT:"+intelligenza+" STA:"+stamina);
    }

    public void setVita(int nuovaVita){
        this.vita = limita(nuovaVita);
    }

    public boolean isVivo(){
        return vita > 0;
    }
}
