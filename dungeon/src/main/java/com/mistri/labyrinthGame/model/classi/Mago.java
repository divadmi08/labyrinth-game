package com.mistri.labyrinthGame.model.classi;

import com.mistri.labyrinthGame.model.*;
import com.mistri.labyrinthGame.model.abilita.Abilita;
import com.mistri.labyrinthGame.model.abilita.Difensore;
import com.mistri.labyrinthGame.model.abilita.UsatoreMagia;


public class Mago extends Personaggio implements UsatoreMagia, Difensore {

    public Mago(String nome, String emoji){
        super(nome, emoji, new Stats(5,5,20,18,8));
        aggiungiAbilita(new Abilita("Sfera Arcana", Abilita.Tipo.MAGIA, 12, 6, "Danno magico elevato"));
    }

    public void lanciaIncantesimo(Personaggio b){
        int danno = stats.getIntelligenza()/2;
        b.getStats().setVita(b.getStats().getVita()-danno);
        System.out.println(nome+" lancia fuoco! Danno: "+danno);
    }

    public void difendi(){
        System.out.println(nome+" crea scudo magico!");
    }

    public void mostraAbilita(){
        System.out.println("Magia + Difesa");
    }

    @Override
    protected void crescitaClasse(){
        stats.aumentaMana(3);
        stats.aumentaIntelligenza(2);
    }
}
