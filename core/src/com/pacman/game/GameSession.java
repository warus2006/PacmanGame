package com.pacman.game;

import com.badlogic.gdx.Gdx;
import com.pacman.game.units.Ghost;
import com.pacman.game.units.PacMan;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GameSession implements Serializable {
    private PacMan pacMan;
    private GameMap gameMap;
    private Ghost[] monsters;

    public PacMan getPacMan() {
        return pacMan;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Ghost[] getMonsters() {
        return monsters;
    }

    public GameSession() {
    }

    public GameSession(PacMan pacMan, GameMap gameMap, Ghost[] monsters) {
        this.pacMan = pacMan;
        this.gameMap = gameMap;
        this.monsters = monsters;
    }

    public void saveSession() {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(Gdx.files.local("temp.dat").write(false));
            out.writeObject(pacMan);
            out.writeObject(gameMap);
            for (int i = 0; i < monsters.length; i++) {
                out.writeObject(monsters[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadSession() {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(Gdx.files.local("temp.dat").read());
            pacMan = (PacMan)in.readObject();
            gameMap = (GameMap)in.readObject();
            monsters = new Ghost[4];
            for (int i = 0; i < monsters.length; i++) {
                monsters[i] = (Ghost)in.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
