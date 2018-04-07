package com.pacman.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.pacman.game.units.Ghost;
import com.pacman.game.units.PacMan;

public class GameScreen implements Screen {
    public static int WORLD_CELL_PX = 80;
    private SpriteBatch batch;
    private PacMan pacman;
    private GameMap gameMap;
    private Camera camera;
    private Ghost[] gosts;
    private BitmapFont font48;
    private boolean paused;
    private Stage stage;
    private Skin skin;

    public GameScreen(SpriteBatch batch, Camera _camera) {

        this.batch = batch;
        this.camera = _camera;
    }

    @Override
    public void show() {
        this.font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf");
        this.gameMap = new GameMap();
        this.pacman = new PacMan(250, gameMap, this);
        this.gosts = new Ghost[4];
        this.gosts[0] = new Ghost(this, 200, gameMap, pacman, 0, 'r');
        this.gosts[1] = new Ghost(this, 200, gameMap, pacman, 1, 'b');
        this.gosts[2] = new Ghost(this, 200, gameMap, pacman, 2, 'o');
        this.gosts[3] = new Ghost(this, 200, gameMap, pacman, 3, 'p');
        resetCamera();
        this.createGUI();
        this.paused = false;
    }

    public void createGUI() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        skin.add("font48", font48);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("shortButton");
        textButtonStyle.font = font48;
        skin.add("simpleSkin", textButtonStyle);

        final Button btnPause = new TextButton("II", skin, "simpleSkin");
        btnPause.setPosition(1180, 620);
        stage.addActor(btnPause);

        final Group pausePanel = new Group();

        btnPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                paused = true;
                pausePanel.setVisible(true);
                btnPause.setVisible(false);
            }
        });

        // ------------ PAUSE PANEL --------------
        Pixmap pixmap = new Pixmap(440, 320, Pixmap.Format.RGB888);
        pixmap.setColor(0.0f, 0.0f, 0.2f, 1.0f);
        pixmap.fill();
        Texture texturePanel = new Texture(pixmap);
        skin.add("texturePanel", texturePanel);
        pausePanel.setVisible(false);
        Button btnMenu = new TextButton("M", skin, "simpleSkin");
        final Button btnContinue = new TextButton(">", skin, "simpleSkin");
        Button btnRestart = new TextButton("R", skin, "simpleSkin");
        Label.LabelStyle ls = new Label.LabelStyle(font48, Color.WHITE);
        Label pauseLabel = new Label("PAUSED", ls);
        pauseLabel.setPosition(120, 240);
        Image image = new Image(skin, "texturePanel");
        pausePanel.addActor(image);
        pausePanel.setPosition(1280 / 2 - 220, 720 / 2 - 160);
        pausePanel.addActor(btnMenu);
        pausePanel.addActor(btnContinue);
        pausePanel.addActor(btnRestart);
        pausePanel.addActor(pauseLabel);
        btnMenu.setPosition(40, 40);
        btnRestart.setPosition(180, 40);
        btnContinue.setPosition(320, 40);
        stage.addActor(pausePanel);
        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });
        btnContinue.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                paused = false;
                pausePanel.setVisible(false);
                btnPause.setVisible(true);
            }
        });
        btnRestart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                restartGame();
                paused = false;
                pausePanel.setVisible(false);
                btnPause.setVisible(true);
            }
        });

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            Button btnLeft = new TextButton("A", skin, "simpleSkin");
            Button btnRight = new TextButton("D", skin, "simpleSkin");
            Button btnUp = new TextButton("W", skin, "simpleSkin");
            Button btnDown = new TextButton("S", skin, "simpleSkin");
            btnLeft.setPosition(980, 120);
            btnRight.setPosition(1180, 120);
            btnUp.setPosition(1080, 220);
            btnDown.setPosition(1080, 20);
            stage.addActor(btnLeft);
            stage.addActor(btnRight);
            stage.addActor(btnUp);
            stage.addActor(btnDown);
            btnLeft.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    pacman.setStop(false);
                    pacman.setPrefferedMove(4);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    pacman.setStop(true);
                }
            });
            btnRight.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    pacman.setStop(false);
                    pacman.setPrefferedMove(2);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    pacman.setStop(true);
                }
            });
            btnUp.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    pacman.setStop(false);
                    pacman.setPrefferedMove(1);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    pacman.setStop(true);
                }
            });
            btnDown.addListener(new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    pacman.setStop(false);
                    pacman.setPrefferedMove(3);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    pacman.setStop(true);
                }
            });
        }
    }

    public void restartGame() {
        gameMap.loadMap("map.dat");
        gameMap.setLevel(1);
        pacman.restart(true);
        for (int i = 0; i < gosts.length; i++) {
            gosts[i].restart(true);
        }
    }

    public void levelUp() {
        gameMap.setLevel(gameMap.getLevel() + 1);
        gameMap.loadMap("map.dat");
        pacman.restart(false);
        for (int i = 0; i < gosts.length; i++) {
            gosts[i].restart(false);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        gameMap.render(batch);
        pacman.render(batch);
        for (int i = 0; i < gosts.length; i++) {
            gosts[i].render(batch);
        }
        resetCamera();
        batch.setProjectionMatrix(camera.combined);
        pacman.renderGUI(batch, font48);
        batch.end();
        stage.draw();
    }

    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            new GameSession(pacman, gameMap, gosts).saveSession();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F9)) {
            GameSession gs = new GameSession();
            gs.loadSession();
            this.pacman = gs.getPacMan();
            this.gameMap = gs.getGameMap();
            this.gosts = gs.getMonsters();
            this.pacman.loadResources(this);
            this.gameMap.loadResources();
            for (int i = 0; i < this.gosts.length; i++) {
                this.gosts[i].loadResources(this);
            }
        }
        if (!paused) {
            pacman.update(dt);
            for (int i = 0; i < gosts.length; i++) {
                gosts[i].update(dt);
            }
            checkCollisions();
            if (this.pacman.checkHuntTimer()) {
                this.pacman.decHuntTimer(dt);
            }
        }
        if (gameMap.getFoodCount() == 0) {
            levelUp();
        }
        trackPacMan();
        stage.act(dt);
    }

    public void checkCollisions() {
        for (int i = 0; i < gosts.length; i++) {
            if (MathUtils.isEqual(gosts[i].getPosition().x, pacman.getPosition().x, GameMap.CELL_SIZE / 4) &&
                    MathUtils.isEqual(gosts[i].getPosition().y, pacman.getPosition().y, GameMap.CELL_SIZE / 4)) {
                if (!pacman.checkSafe() && !this.pacman.checkHuntTimer()) {
                    pacman.resetPosition();
                    pacman.minusLife();
                    if (pacman.getLives() < 0) {
                        ScreenManager.getInstance().transferPacmanToGameOverScreen(pacman);
                        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER);
                    }
                }
                if (this.pacman.checkHuntTimer()) {
                    pacman.addScore(200);
                    gosts[i].resetPosition();
                }
            }
        }
    }

    public void trackPacMan() {
        camera.position.set(pacman.getPosition().x + (GameMap.CELL_SIZE / 2), pacman.getPosition().y + (GameMap.CELL_SIZE / 2), 0);
        if (camera.position.x < 640) camera.position.x = 640;
        if (camera.position.y < 360) camera.position.y = 360;
        if (camera.position.x > gameMap.getMapSizeX() * GameMap.CELL_SIZE - 640)
            camera.position.x = gameMap.getMapSizeX() * GameMap.CELL_SIZE - 640;
        if (camera.position.y > gameMap.getMapSizeY() * GameMap.CELL_SIZE - 360)
            camera.position.y = gameMap.getMapSizeY() * GameMap.CELL_SIZE - 360;
        camera.update();
    }

    public void resetCamera() {
        camera.position.set(640, 360, 0);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().resize(width, height);
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
