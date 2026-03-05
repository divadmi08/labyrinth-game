package com.mistri.labyrinthGame.model.classi;

import com.mistri.labyrinthGame.model.*;
import com.mistri.labyrinthGame.model.abilita.Abilita;
import com.mistri.labyrinthGame.model.abilita.Attaccante;
import com.mistri.labyrinthGame.model.abilita.UsatoreMagia;

public class Paladino extends Personaggio implements Attaccante, UsatoreMagia {

    public Paladino(String nome, String emoji){
        super(nome, emoji, new Stats(15, 18, 15, 10, 10));
        aggiungiAbilita(new Abilita("Giudizio Sacro", Abilita.Tipo.MAGIA, 10, 4, "Punizione divina"));
    }

    public void attacca(Personaggio b){
        int danno = stats.getForza()/2;
        b.getStats().setVita(b.getStats().getVita()-danno);
        System.out.println("Colpo sacro! "+danno);
    }

    public void lanciaIncantesimo(Personaggio b){
        int danno = stats.getMana()/2;
        b.getStats().setVita(b.getStats().getVita()-danno);
        System.out.println("Luce divina! "+danno);
    }

    public void mostraAbilita(){
        System.out.println("Attacco + Magia sacra");
    }

    @Override
    protected void crescitaClasse(){
        stats.aumentaForza(2);
        stats.aumentaVita(2);
        stats.aumentaMana(1);
    }
}
