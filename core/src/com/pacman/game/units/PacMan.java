package com.pacman.game.units;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pacman.game.Assets;
import com.pacman.game.GameMap;
import com.pacman.game.GameScreen;

import java.io.Serializable;

public class PacMan extends Unit implements Serializable {
    private int score;
    private int lives;
    private StringBuilder guiHelper;
    private int foodEated;
    private float safeTime;
    private float huntTimer;

    public PacMan(float v, GameMap data, GameScreen gameScreen) {
        this.gameMap = data;
        this.v = v;
        this.move = 0;
        this.animationTimer = 0.0f;
        this.secPerFrame = 0.1f;
        resetPosition();
        this.lives = 2;
        this.score = 0;
        this.foodEated = 0;
        this.guiHelper = new StringBuilder(100);
        resetHuntTimer();
        loadResources(gameScreen);
    }

    public void setPrefferedMove(int mov) {
        this.move = mov;
    }

    public int getFoodEated() {
        return foodEated;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public void minusLife() {
        lives--;
        setSafeTime(3.0f);
    }

    public void activateHuntTimer() {
        this.huntTimer = 5.0f - gameMap.getLevel() * 0.2f;
        if (this.huntTimer < 0.0f) {
            this.huntTimer = 0.0f;
        }
     }

    public void resetHuntTimer() {
        this.huntTimer = 0.0f;
    }

    public boolean checkHuntTimer() {
        return this.huntTimer > 0.0f;
    }

    public float getHuntTimer() {
        return this.huntTimer;
    }

    public void decHuntTimer(float dt) {
        this.huntTimer -= dt;
    }


    public void setSafeTime(float safeTime) {
        this.safeTime = safeTime;
    }

    public boolean checkSafe() {
        return safeTime > 0.0f;
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        guiHelper.setLength(0);
        guiHelper.append("Level: ").append(gameMap.getLevel()).append("\n").append("Lives: ").append(lives).append("\nScore: ").append(score);
        font.draw(batch, guiHelper, 20, 700);
    }

    public void addScore(int amount) {
        score += amount;
    }

    @Override
    public void restart(boolean full) {
        if (full) {
            lives = 2;
            score = 0;
            foodEated = 0;
        }
        resetPosition();
        rotation = 0;
        setSafeTime(0);
        resetHuntTimer();
        move = 0;
        stop = false;

    }

    @Override
    public void loadResources(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.imgRegion = Assets.getInstance().getAtlas().findRegion("pacman").split(SIZE, SIZE)[0];
    }

    @Override
    public void render(SpriteBatch batch) {
        if (flipx != this.imgRegion[getCurrentFrame()].isFlipX()) this.imgRegion[getCurrentFrame()].flip(true, false);
        if (flipy != this.imgRegion[getCurrentFrame()].isFlipY()) this.imgRegion[getCurrentFrame()].flip(false, true);
        if (!checkSafe()) {
            batch.draw(this.imgRegion[getCurrentFrame()], position.x, position.y, HALF_SIZE, HALF_SIZE, SIZE, SIZE, 1, 1, rotation);
        } else {
            if (safeTime % 0.4f < 0.2f) {
                batch.draw(this.imgRegion[getCurrentFrame()], position.x, position.y, HALF_SIZE, HALF_SIZE, SIZE, SIZE, 1, 1, rotation);
            }
        }
    }

    @Override
    public void resetPosition() {
        this.position = gameMap.getUnitPosition('s');
        this.fposition = gameMap.getUnitPosition('s');
        this.rotation = 0;
        this.flipy = false;
        this.flipx = false;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (checkSafe()) {
            safeTime -= dt;
        }
        if (isMove()) changePosition(dt);
        else {
            if (this.gameMap.checkFoodEting(position.x, position.y)) {
                addScore(5);
                this.foodEated++;
            }
            if (this.gameMap.checkCherryEting(position.x, position.y)) {
                addScore(100);
                foodEated++;
                activateHuntTimer();
            }
            if ((this.gameMap.checkAndGetPortalDst(position.x, position.y) > 0) && !this.isTeleported) {
                dropToPortal(this.gameMap.checkAndGetPortalDst(position.x, position.y));
            }
            if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    this.move = 2;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    this.move = 4;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    this.move = 1;
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    this.move = 3;
                }
            }
            if (this.move == 1) {
                this.flipy = false;
                this.flipx = false;
                this.rotation = 90;
                setPossibleMove();
            } else if (this.move == 3) {
                this.flipy = false;
                this.flipx = false;
                this.rotation = 270;
                setPossibleMove();
            } else if (this.move == 2) {
                this.flipx = false;
                this.flipy = false;
                this.rotation = 0;
                setPossibleMove();
            } else if (this.move == 4) {
                this.flipx = true;
                this.flipy = false;
                this.rotation = 0;
                setPossibleMove();
            }
        }
    }
}

