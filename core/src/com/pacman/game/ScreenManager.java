package com.pacman.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pacman.game.units.PacMan;

public class ScreenManager {

    public enum ScreenType {MENU, GAME, GAMEOVER}

    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private GameOverScreen gameOverScreen;
    private LoadingScreen loadingScreen;
    private static ScreenManager ourInstance = new ScreenManager();
    private MyGdxGame game;
    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;
    private Screen targetScreen;

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    private ScreenManager() {
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void init(MyGdxGame _game, SpriteBatch batch) {

        this.batch = batch;
        this.camera = new OrthographicCamera(1280, 720);
        this.gameScreen = new GameScreen(batch, camera);
        this.menuScreen = new MenuScreen(batch);
        this.gameOverScreen = new GameOverScreen(batch);
        this.loadingScreen = new LoadingScreen(batch);
        this.game = _game;
        this.viewport = new FitViewport(1280, 720, camera);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    public void resetCamera() {
        camera.position.set(640, 360, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void changeScreen(ScreenType type) {
        Screen screen = game.getScreen();
        Assets.getInstance().clear();
        if (screen != null) {
            screen.dispose();
        }
        resetCamera();
        game.setScreen(loadingScreen);
        switch (type) {
            case GAME:
                targetScreen = gameScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME);
                break;
            case MENU:
                targetScreen = menuScreen;
                Assets.getInstance().loadAssets(ScreenType.MENU);
                break;
            case GAMEOVER:
                targetScreen = gameOverScreen;
                Assets.getInstance().loadAssets(ScreenType.GAMEOVER);
                break;
        }
    }

    public void transferPacmanToGameOverScreen(PacMan pacMan) {
        gameOverScreen.setPacMan(pacMan);
    }

    public void goToTarget() {
        game.setScreen(targetScreen);
    }
}
