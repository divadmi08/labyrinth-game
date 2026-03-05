package com.mistri.labyrinthGame.model.classi;

import com.mistri.labyrinthGame.model.*;
import com.mistri.labyrinthGame.model.abilita.Abilita;
import com.mistri.labyrinthGame.model.abilita.Attaccante;
import com.mistri.labyrinthGame.model.abilita.Difensore;

public class Guerriero extends Personaggio implements Attaccante, Difensore {

    public Guerriero(String nome, String emoji){
        super(nome, emoji, new Stats(20,18,5,5,12));
        aggiungiAbilita(new Abilita("Colpo Possente", Abilita.Tipo.FISICO, 11, 0, "Attacco fisico pesante"));
    }

    public void attacca(Personaggio b){
        int danno = stats.getForza()/2;
        b.getStats().setVita(b.getStats().getVita()-danno);
        System.out.println(nome+" colpisce! Danno: "+danno);
    }

    public void difendi(){
        System.out.println(nome+" alza lo scudo!");
    }

    public void mostraAbilita(){
        System.out.println("Attacco + Difesa");
    }

    @Override
    protected void crescitaClasse(){
        stats.aumentaForza(3);
        stats.aumentaVita(2);
    }
}
