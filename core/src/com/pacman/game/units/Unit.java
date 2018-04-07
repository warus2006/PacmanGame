package com.pacman.game.units;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.pacman.game.GameMap;
import com.pacman.game.GameScreen;

import java.io.Serializable;

public abstract class Unit implements Serializable {

    final int HALF_SIZE = GameScreen.WORLD_CELL_PX / 2;
    final int SIZE = GameScreen.WORLD_CELL_PX;
    transient GameScreen gameScreen;
    transient TextureRegion[] imgRegion;
    GameMap gameMap;
    Vector2 position;
    Vector2 fposition;
    int move;//0-стоим, 1 - вверх, 2 - вправо, 3 - вниз, 4 - влево
    float v;
    float rotation;
    float animationTimer;
    float secPerFrame;
    boolean stop;
    boolean flipx;
    boolean flipy;
    boolean isTeleported;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getCurrentFrame() {
        return (int) (this.animationTimer / this.secPerFrame);
    }

    public abstract void render(SpriteBatch batch);

    public abstract void loadResources(GameScreen gameScreen);

    public abstract void resetPosition();

    public abstract void restart(boolean full);

    public void update(float dt) {
        this.animationTimer += dt;
        if (this.animationTimer > this.secPerFrame * this.imgRegion.length) {
            this.animationTimer = 0.0f;
        }
    }

    public boolean isMove() {
        return ((this.move == 1 && position.y < fposition.y)
                || (this.move == 2 && position.x < fposition.x)
                || (this.move == 3 && position.y > fposition.y)
                || (this.move == 4 && position.x > fposition.x));
    }

    public void changePosition(float dt) {
        float delta = this.v * dt;
        if (this.move == 1) {
            float deltay = (this.fposition.y - this.position.y);
            this.position.y += deltay < delta ? deltay : delta;
        } else if (this.move == 2) {
            float deltax = (this.fposition.x - this.position.x);
            this.position.x += deltax < delta ? deltax : delta;
        } else if (this.move == 3) {
            float deltay = (this.position.y - this.fposition.y);
            this.position.y -= deltay < delta ? deltay : delta;
        } else if (this.move == 4) {
            float deltax = (this.position.x - this.fposition.x);
            this.position.x -= deltax < delta ? deltax : delta;
        }
        if (MathUtils.isEqual(this.position.x, this.fposition.x, 0.0f) &&
                MathUtils.isEqual(this.position.y, this.fposition.y, 0.0f) &&
                (((Gdx.app.getType() == Application.ApplicationType.Android) && this.stop) ||
                 (Gdx.app.getType() == Application.ApplicationType.Desktop))){
            this.move = 0;
        }
    }

    public void setPossibleMove() {
        if (checkMove(this.move)) {
            float fx = this.position.x;
            float fy = this.position.y;
            if (this.move == 1) fy += SIZE;
            else if (this.move == 2) fx += SIZE;
            else if (this.move == 3) fy -= SIZE;
            else if (this.move == 4) fx -= SIZE;
            this.fposition.x = fx;
            this.fposition.y = fy;
            this.isTeleported = false;
        }
    }

    public boolean checkMove(int mov) {
        float fx = this.position.x;
        float fy = this.position.y;
        if (mov == 1) fy += SIZE;
        else if (mov == 2) fx += SIZE;
        else if (mov == 3) fy -= SIZE;
        else if (mov == 4) fx -= SIZE;
        int xX = (int) (fx / SIZE);
        int yY = (int) (fy / SIZE);

        if ((xX < this.gameMap.getMapSizeX()) && (xX >= 0)
                && (yY < this.gameMap.getMapSizeY()) && (yY >= 0)) {
            return (this.gameMap.getGameMap()[xX][yY] != GameMap.CellType.WALL);
        }
        return false;
    }

    public void dropToPortal(int portalGateNumber) {
        this.position.x = this.gameMap.getPortalx(portalGateNumber);
        this.position.y = this.gameMap.getPortaly(portalGateNumber);
        this.fposition.x = this.gameMap.getPortalx(portalGateNumber);
        this.fposition.y = this.gameMap.getPortaly(portalGateNumber);
        this.isTeleported = true;
        this.move = 0;
    }

}
