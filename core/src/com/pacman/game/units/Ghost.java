package com.pacman.game.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.pacman.game.Assets;
import com.pacman.game.GameMap;
import com.pacman.game.GameScreen;
import java.io.Serializable;

public class Ghost extends Unit implements Serializable {

    PacMan pacman;
    int type;
    char unitChar;
    transient TextureRegion[] whiteRegions;
    int hunterRange;//радиус активизации призраков в клетках
    int hunterStatus;//0 - рандом, 1 - охота, 2 - кипишь
    final int[] moves = {1, 2, 3, 4};//массив приоритетных ходов (по часовой стрелке)

    public Ghost(GameScreen gameScreen, float v, GameMap data, PacMan pacMan, int type, char unitChar) {
        this.gameMap = data;
        this.pacman = pacMan;
        this.v = v;
        this.move = 0;
        this.animationTimer = 0.0f;
        this.secPerFrame = 0.2f;
        this.rotation = 0;
        this.unitChar = unitChar;
        this.hunterStatus = 0;
        this.type = type;
        loadResources(gameScreen);
        resetPosition();
        checkLevel();
    }

    public void checkLevel() {
        this.v = this.v + gameMap.getLevel() * 20.0f;
        if (this.v > 300.0f) {
            this.v = 300.0f;
        }
        this.hunterRange =  4 + gameMap.getLevel();
    }

    @Override
    public void restart(boolean full) {
        resetPosition();
        checkLevel();
    }

    @Override
    public void loadResources(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.imgRegion = Assets.getInstance().getAtlas().findRegion("ghosts").split(SIZE, SIZE)[type];
        this.whiteRegions = Assets.getInstance().getAtlas().findRegion("ghosts").split(SIZE, SIZE)[4];
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentRegion = imgRegion[getCurrentFrame()];
        if (pacman.checkHuntTimer() && pacman.getHuntTimer() % 0.4f > 0.2f) {
            currentRegion = whiteRegions[getCurrentFrame()];
        }
        if (flipx != currentRegion.isFlipX()) currentRegion.flip(true, false);
        batch.draw(currentRegion, position.x, position.y, HALF_SIZE, HALF_SIZE, SIZE, SIZE, 1, 1, rotation);
    }

    @Override
    public void resetPosition() {
        this.position = gameMap.getUnitPosition(unitChar);
        this.fposition = gameMap.getUnitPosition(unitChar);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        //update_my(dt);
        if (isMove()) changePosition(dt);
        else {
            if ((this.gameMap.checkAndGetPortalDst(position.x, position.y) > 0) && !this.isTeleported) {
                dropToPortal(this.gameMap.checkAndGetPortalDst(position.x, position.y));
            }
            checkAndSetHunterStatus();
            if (this.hunterStatus == 0) {
                this.move = MathUtils.random(1, 4);
            }
            if (this.hunterStatus == 1) {
                this.move = gameMap.buildRoute((int) pacman.getPosition().x / SIZE, (int) pacman.getPosition().y / SIZE, (int) position.x / SIZE, (int) position.y / SIZE);
            }
            if (this.hunterStatus == 2) {
                this.move = goToFromPacman(true);
            }
            this.flipx = (this.move == 4);
            this.flipy = false;
            setPossibleMove();
        }
    }

    public void update_my(float dt) {
        if (isMove()) changePosition(dt);
        else {
            checkAndSetHunterStatus();
            if (this.hunterStatus == 0) {
                this.move = MathUtils.random(1, 4);
            }
            if (this.hunterStatus == 1) {
                this.move = goToFromPacman(false);
            }
            if (this.hunterStatus == 2) {
                this.move = goToFromPacman(true);
            }
            this.flipx = (this.move == 4);
            this.flipy = false;
            setPossibleMove();
        }
    }

    public void checkAndSetHunterStatus() {
        this.hunterStatus = 0;
        int currentRange = (int) ((this.pacman.getPosition().cpy().sub(this.position).len()) / SIZE);
        if (currentRange <= hunterRange) {
            this.hunterStatus = 1;
            if (pacman.checkHuntTimer()) this.hunterStatus = 2;
        }
    }

    public int goToFromPacman(boolean fromPM) {
        int currentMove = 0;
        float deltax = (this.pacman.getPosition().x - this.position.x);
        float deltay = (this.pacman.getPosition().y - this.position.y);
        if ((Math.abs(deltax) < Math.abs(deltay))) {
            if (deltax != 0) {
                currentMove = (deltax > 0) ? (fromPM ? 4 : 2) : (fromPM ? 2 : 4);
                if (checkMove(currentMove)) return currentMove;
            }
            if (deltay != 0) {
                currentMove = (deltay > 0) ? (fromPM ? 3 : 1) : (fromPM ? 1 : 3);
                if (checkMove(currentMove)) return currentMove;
            }
        } else {
            if (deltay != 0) {
                currentMove = (deltay > 0) ? (fromPM ? 3 : 1) : (fromPM ? 1 : 3);
                if (checkMove(currentMove)) return currentMove;
            }
            if (deltax != 0) {
                currentMove = (deltax > 0) ? (fromPM ? 4 : 2) : (fromPM ? 2 : 4);
                if (checkMove(currentMove)) return currentMove;
            }
        }
        //альтернативное движение по массиву приоритетов, если есть препятствие и не можем осуществить "опимальный" ход
        for (int mov : moves
                ) {
            if (checkMove(mov)) {
                return mov;
            }
        }
        return currentMove;
    }


}
