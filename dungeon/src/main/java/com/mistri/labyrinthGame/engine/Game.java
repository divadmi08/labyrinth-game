package com.mistri.labyrinthGame.engine;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import com.mistri.labyrinthGame.model.Nemico;
import com.mistri.labyrinthGame.model.Personaggio;
import com.mistri.labyrinthGame.world.Labirinto;

public class Game {

    private int x = 1;
    private int y = 1;

    // Nemico di test in posizione fissa (colonna 5, riga 1 della mappa)
    private Nemico nemico = new Nemico("Goblin", "G", 20, 5, 50);
    private boolean nemicoVivo = true;
    private boolean screenStarted = false;

    public void start(Personaggio player) throws Exception {

        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen    = new com.googlecode.lanterna.screen.TerminalScreen(terminal);
        screen.startScreen();
        screenStarted = true;
        screen.setCursorPosition(null);

        // ── Collega il level-up al terminale Lanterna ────────────────────────
        player.setOnLevelUp(punti -> {
            try {
                distribuisciPuntiLanterna(screen, player, punti);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Labirinto lab = new Labirinto();

        try {
            while (true) {

                screen.clear();
                char[][] map = lab.getMappa();

                // Disegna mappa
                for (int r = 0; r < map.length; r++) {
                    for (int c = 0; c < map[r].length; c++) {
                        screen.setCharacter(c, r + 2, new TextCharacter(map[r][c]));
                    }
                }

                // Disegna nemico (se vivo)
                if (nemicoVivo) {
                    screen.setCharacter(5, 1 + 2, new TextCharacter(nemico.getEmoji().charAt(0)));
                }

                // Disegna player
                screen.setCharacter(x, y + 2, new TextCharacter(player.getEmoji().charAt(0)));

                // HUD
                String hud = "Lv:" + player.getLivello()
                        + " HP:" + player.getStats().getVita()
                        + " MP:" + player.getStats().getMana()
                        + " FOR:" + player.getStats().getForza()
                        + " INT:" + player.getStats().getIntelligenza()
                        + " STA:" + player.getStats().getStamina();

                screen.newTextGraphics().putString(0, 0, hud);
                screen.newTextGraphics().putString(0, 1, "Frecce=muovi  ESC=esci");

                screen.refresh();

                KeyStroke key = screen.readInput();
                if (key == null) {
                    continue;
                }
                if (key.getKeyType() == KeyType.Escape) {
                    return;
                }

                int newX = x, newY = y;

                switch (key.getKeyType()) {
                    case ArrowUp    -> newY--;
                    case ArrowDown  -> newY++;
                    case ArrowLeft  -> newX--;
                    case ArrowRight -> newX++;
                    default -> {}
                }

                if (puoMuoversi(newX, newY, map)) {
                    x = newX;
                    y = newY;

                    // Controlla collisione con nemico
                    if (nemicoVivo && x == 5 && y == 1) {
                        boolean vittoria = CombatEngine.combatti(screen, player, nemico);
                        if (vittoria) {
                            nemicoVivo = false;
                        }
                        if (!player.getStats().isVivo()) {
                            return;
                        }
                    }
                }
            }
        } finally {
            if (screenStarted) {
                screen.stopScreen();
                screenStarted = false;
            }
        }
    }

    // ── Distribuzione punti bonus livello via Lanterna ───────────────────────

    private void distribuisciPuntiLanterna(Screen screen, Personaggio player, int punti) throws Exception {
        while (punti > 0) {
            screen.clear();
            var g = screen.newTextGraphics();
            g.putString(0, 0, "LEVEL UP!  Punti rimasti: " + punti);
            g.putString(0, 1, "1) Forza   2) Vita   3) Mana   4) Int   5) Stamina");
            g.putString(0, 2, "Stats: FOR:" + player.getStats().getForza()
                    + " VIT:" + player.getStats().getVita()
                    + " MP:"  + player.getStats().getMana()
                    + " INT:" + player.getStats().getIntelligenza()
                    + " STA:" + player.getStats().getStamina());
            screen.refresh();

            KeyStroke key = screen.readInput();
            if (key == null) {
                continue;
            }
            Character ch  = key.getCharacter();
            if (ch == null) continue;

            switch (ch) {
                case '1' -> { player.getStats().aumentaForza(1);        punti--; }
                case '2' -> { player.getStats().aumentaVita(1);         punti--; }
                case '3' -> { player.getStats().aumentaMana(1);         punti--; }
                case '4' -> { player.getStats().aumentaIntelligenza(1); punti--; }
                case '5' -> { player.getStats().aumentaStamina(1);      punti--; }
                default  -> {} // tasto non valido, ripete
            }
        }
    }

    private boolean puoMuoversi(int newX, int newY, char[][] map) {
        return newY >= 0 && newY < map.length
                && newX >= 0 && newX < map[0].length
                && map[newY][newX] != '▓';
    }
}
