package com.mistri.labyrinthGame.engine;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.mistri.labyrinthGame.model.Nemico;
import com.mistri.labyrinthGame.model.Personaggio;
import com.mistri.labyrinthGame.model.abilita.Abilita;
import com.mistri.labyrinthGame.model.abilita.UsatoreMagia;

import java.util.List;

/**
 * Gestisce il combattimento a turni direttamente sulla Screen Lanterna.
 * Niente Scanner — tutto via tasti freccia / numero.
 *
 * Flusso:
 *   1. Mostra stato player + nemico
 *   2. Player sceglie azione (1 Attacca, 2 Magia, 3 Abilità, 4 Fuggi)
 *   3. Nemico risponde con attacco base
 *   4. Controlla vittoria / sconfitta
 */
public class CombatEngine {

    // Riga di partenza nell'area combattimento
    private static final int ROW_TITLE   = 0;
    private static final int ROW_ENEMY   = 2;
    private static final int ROW_PLAYER  = 3;
    private static final int ROW_LOG     = 5;
    private static final int ROW_MENU    = 9;

    /**
     * @return true  se il player ha vinto (nemico morto)
     *         false se il player ha perso o è fuggito
     */
    public static boolean combatti(Screen screen, Personaggio player, Nemico nemico) throws Exception {

        String log1 = "Incontri " + nemico.getNome() + " " + nemico.getEmoji() + "!";
        String log2 = "";

        while (true) {

            // ── Disegna UI combattimento ──────────────────────────────────────
            screen.clear();
            var g = screen.newTextGraphics();

            g.putString(0, ROW_TITLE,  "═══ COMBATTIMENTO ═══");
            g.putString(0, ROW_ENEMY,  nemico.statusBar());
            g.putString(0, ROW_PLAYER, playerBar(player));

            g.putString(0, ROW_LOG,    log1);
            g.putString(0, ROW_LOG+1,  log2);

            g.putString(0, ROW_MENU,   "1) Attacca fisico");
            g.putString(0, ROW_MENU+1, "2) Usa magia");
            g.putString(0, ROW_MENU+2, "3) Abilità speciali");
            g.putString(0, ROW_MENU+3, "4) Fuggi");

            screen.refresh();

            // ── Input player ─────────────────────────────────────────────────
            KeyStroke key = screen.readInput();
            if (key == null) continue;
            if (key.getKeyType() == KeyType.Escape) return false;

            Character ch = key.getCharacter();
            if (ch == null) continue;

            int dannoPlayer = 0;

            switch (ch) {
                case '1' -> {
                    // Attacco fisico base
                    dannoPlayer = calcolaDannoFisico(player);
                    nemico.subisciDanno(dannoPlayer);
                    log1 = player.getNome() + " attacca! Danno: " + dannoPlayer;
                }
                case '2' -> {
                    // Magia (solo se il player è UsatoreMagia)
                    if (player instanceof UsatoreMagia) {
                        dannoPlayer = calcolaDannoMagia(player);
                        if (player.getStats().getMana() >= 5) {
                            nemico.subisciDanno(dannoPlayer);
                            player.getStats().aumentaMana(-5);
                            log1 = player.getNome() + " usa magia! Danno: " + dannoPlayer;
                        } else {
                            log1 = "Mana insufficiente!";
                            dannoPlayer = 0;
                        }
                    } else {
                        log1 = "Questa classe non usa magia!";
                        dannoPlayer = 0;
                    }
                }
                case '3' -> {
                    // Prima abilità della lista (se esiste)
                    List<Abilita> lista = player.getAbilita();
                    if (!lista.isEmpty()) {
                        Abilita ab = lista.get(0);
                        if (player.getStats().getMana() >= ab.getCostoMana()) {
                            dannoPlayer = ab.getPotenza();
                            nemico.subisciDanno(dannoPlayer);
                            player.getStats().aumentaMana(-ab.getCostoMana());
                            log1 = ab.getNome() + "! Danno: " + dannoPlayer;
                        } else {
                            log1 = "Mana insufficiente per " + ab.getNome();
                            dannoPlayer = 0;
                        }
                    } else {
                        log1 = "Nessuna abilità disponibile!";
                        dannoPlayer = 0;
                    }
                }
                case '4' -> {
                    log1 = "Sei fuggito!";
                    screen.clear();
                    screen.newTextGraphics().putString(0, 0, log1);
                    screen.refresh();
                    Thread.sleep(1000);
                    return false;
                }
                default -> {
                    log1 = "Tasto non valido. Usa 1-4.";
                    continue;
                }
            }

            // ── Controlla se nemico è morto ───────────────────────────────────
            if (!nemico.isVivo()) {
                screen.clear();
                screen.newTextGraphics().putString(0, 0, "Hai sconfitto " + nemico.getNome() + "!");
                screen.newTextGraphics().putString(0, 1, "+" + nemico.getExpDato() + " EXP");
                screen.refresh();
                player.guadagnaExp(nemico.getExpDato());
                Thread.sleep(1500);
                return true;
            }

            // ── Turno nemico ──────────────────────────────────────────────────
            int dannoNemico = Math.max(1, nemico.getForza() - player.getStats().getStamina() / 4);
            player.getStats().setVita(player.getStats().getVita() - dannoNemico);
            log2 = nemico.getNome() + " colpisce! Danno: " + dannoNemico;

            // ── Controlla se player è morto ───────────────────────────────────
            if (!player.getStats().isVivo()) {
                screen.clear();
                screen.newTextGraphics().putString(0, 0, "Sei stato sconfitto da " + nemico.getNome() + "...");
                screen.newTextGraphics().putString(0, 1, "Game Over");
                screen.refresh();
                Thread.sleep(2000);
                return false;
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static int calcolaDannoFisico(Personaggio p) {
        return Math.max(1, p.getStats().getForza() / 2);
    }

    private static int calcolaDannoMagia(Personaggio p) {
        return Math.max(1, p.getStats().getIntelligenza() / 2);
    }

    private static String playerBar(Personaggio p) {
        return p.getNome() + "  Lv:" + p.getLivello()
                + "  HP:" + p.getStats().getVita()
                + "  MP:" + p.getStats().getMana();
    }
}
