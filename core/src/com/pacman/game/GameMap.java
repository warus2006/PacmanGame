package com.pacman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GameMap implements Serializable {

    public enum CellType {
        EMPTY('0'), WALL('1'), FOOD('_'), CHERRY('*'), PLAYER('s'), BLUE('b'), PINK('p'),
        RED('r'), ORANGE('o'), PORTAL1('i'), PORTAL2('u');
        char datSymbol;
        CellType(char datSymbol) {
            this.datSymbol = datSymbol;
        }
    }

    private int level;
    private CellType[][] gameMap;
    private transient TextureRegion textureGnd;
    private transient TextureRegion textureWall;
    private transient TextureRegion textureFood;
    private transient TextureRegion textureCherry;
    private Vector2 portal1;
    private Vector2 portal2;
    private int foodCount;
    private transient TextureRegion texturePRT;
    public static final int CELL_SIZE = GameScreen.WORLD_CELL_PX;

    private HashMap<Character, Vector2> startPositions;
    private int mapSizeX;
    private int mapSizeY;

    public int getMapSizeX() {
        return mapSizeX;
    }

    public int getMapSizeY() {
        return mapSizeY;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public float getPortalx(int portalGate) {
        if (portalGate == 2) return portal2.x;
        return portal1.x;
    }

    public float getPortaly(int portalGate) {
        if (portalGate == 2) return portal2.y;
        return portal1.y;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public GameMap() {
        level = 1;
        loadResources();
        loadMap("map.dat");

    }

    public void loadResources() {
        textureGnd = Assets.getInstance().getAtlas().findRegion("ground");
        textureWall = Assets.getInstance().getAtlas().findRegion("wall");
        textureFood = Assets.getInstance().getAtlas().findRegion("food");
        textureCherry = Assets.getInstance().getAtlas().findRegion("energizer");
        texturePRT = Assets.getInstance().getAtlas().findRegion("portal");
    }

    public void loadMap(String name) {
        startPositions = new HashMap<Character, Vector2>();
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = Gdx.files.internal(name).reader(8192);
            String str;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapSizeX = list.get(0).length();
        mapSizeY = list.size();
        gameMap = new CellType[mapSizeX][mapSizeY];
        for (int y = 0; y < list.size(); y++) {
            for (int x = 0; x < list.get(y).length(); x++) {
                char currentSymb = list.get(y).charAt(x);
                for (int i = 0; i < CellType.values().length; i++) {
                    if (currentSymb == CellType.values()[i].datSymbol) {
                        gameMap[x][mapSizeY - y - 1] = CellType.values()[i];
                        if (CellType.values()[i] == CellType.FOOD) {
                            foodCount++;
                        }
                        if (CellType.values()[i] == CellType.PLAYER || CellType.values()[i] == CellType.BLUE || CellType.values()[i] == CellType.PINK || CellType.values()[i] == CellType.RED || CellType.values()[i] == CellType.ORANGE) {
                            startPositions.put(currentSymb, new Vector2(x * CELL_SIZE, (mapSizeY - y - 1) * CELL_SIZE));
                        }
                        if (CellType.values()[i] == CellType.PORTAL1) {
                            portal1 = new Vector2(x * CELL_SIZE, (mapSizeY - y - 1) * CELL_SIZE);
                        }
                        if (CellType.values()[i] == CellType.PORTAL2) {
                            portal2 = new Vector2(x * CELL_SIZE, (mapSizeY - y - 1) * CELL_SIZE);
                        }
                        break;
                    }
                }
            }
        }
    }

    public Vector2 getUnitPosition(char unitChar) {
        return startPositions.get(unitChar).cpy();
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < mapSizeX; i++) {
            for (int j = 0; j < mapSizeY; j++) {
                batch.draw(textureGnd, i * CELL_SIZE, j * CELL_SIZE);
                if (this.gameMap[i][j] == CellType.PORTAL2 || this.gameMap[i][j] == CellType.PORTAL1) batch.draw(texturePRT, i * CELL_SIZE, j * CELL_SIZE);
                if (this.gameMap[i][j] == CellType.WALL) batch.draw(textureWall, i * CELL_SIZE, j * CELL_SIZE);
                if (this.gameMap[i][j] == CellType.FOOD) batch.draw(textureFood, i * CELL_SIZE, j * CELL_SIZE);
                if (this.gameMap[i][j] == CellType.CHERRY) batch.draw(textureCherry, i * CELL_SIZE, j * CELL_SIZE);
            }
        }
    }

    public CellType[][] getGameMap() {
        return gameMap;
    }

    public boolean checkFoodEting(float x, float y) {
        if (this.gameMap[(int) (x / CELL_SIZE)][(int) (y / CELL_SIZE)] == CellType.FOOD) {
            this.gameMap[(int) (x / CELL_SIZE)][(int) (y / CELL_SIZE)] = CellType.EMPTY;
            foodCount--;
            return true;
        }
        return false;
    }

    public boolean checkCherryEting(float x, float y) {
        if (this.gameMap[(int) (x / CELL_SIZE)][(int) (y / CELL_SIZE)] == CellType.CHERRY) {
            this.gameMap[(int) (x / CELL_SIZE)][(int) (y / CELL_SIZE)] = CellType.EMPTY;
            return true;
        }
        return false;
    }

    public int checkAndGetPortalDst(float x, float y) {
        if (this.gameMap[(int) (x / CELL_SIZE)][(int) (y / CELL_SIZE)] == CellType.PORTAL1) {
            return 2;
        }
        if (this.gameMap[(int) (x / CELL_SIZE)][(int) (y / CELL_SIZE)] == CellType.PORTAL2) {
            return 1;
        }
        return 0;
    }

    public int buildRoute(int srcX, int srcY, int dstX, int dstY) {
        int[][] arr = new int[mapSizeX][mapSizeY];
        for (int i = 0; i < mapSizeX; i++) {
            for (int j = 0; j < mapSizeY; j++) {
                if (this.gameMap[i][j] == CellType.WALL) {
                    arr[i][j] = -1;
                }
            }
        }
        arr[srcX][srcY] = 1;
        updatePoint(arr, srcX, srcY, 2);
        int lastPoint = -1;
        for (int i = 2; i < 45; i++) {
            for (int x = 0; x < mapSizeX; x++) {
                for (int y = 0; y < mapSizeY; y++) {
                    if (arr[x][y] == i) {
                        updatePoint(arr, x, y, i + 1);
                    }
                }
            }
            if (arr[dstX][dstY] > 0) {
                lastPoint = arr[dstX][dstY];
                return getMove(arr, dstX, dstY, lastPoint-1);
            }
        }
        return 0;
    }

    public void updatePoint(int[][] arr, int x, int y, int number) {
        if (x - 1 > -1 && arr[x - 1][y] == 0) arr[x - 1][y] = number;
        if (x + 1 < mapSizeX && arr[x + 1][y] == 0) arr[x + 1][y] = number;
        if (y + 1 < mapSizeY && arr[x][y + 1] == 0) arr[x][y + 1] = number;
        if (y - 1 > -1 && arr[x][y - 1] == 0) arr[x][y - 1] = number;
    }

    public int getMove(int[][] arr, int x, int y, int lp) {
        if ((x - 1) >= 0) if (arr[x - 1][y] == lp) return 4;
        if ((x + 1) < mapSizeX) if (arr[x + 1][y] == lp) return 2;
        if ((x + 1) < mapSizeY) if (arr[x][y + 1] == lp) return 1;
        if ((y - 1) >= 0) if (arr[x][y - 1] == lp) return 3;
        return 0;
    }
}
