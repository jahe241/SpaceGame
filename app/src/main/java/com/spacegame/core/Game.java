package com.spacegame.core;

import java.util.ArrayList;

public class Game {
    public static Game game;


    private Game() {
        Game.game = new Game();
    }

    public static Game getInstance() {
        if (Game.game == null) {
            Game.game = new Game();
        }
        return Game.game;
    }
}
