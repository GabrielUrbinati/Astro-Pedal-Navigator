package br.mackenzie.astropedal.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import br.mackenzie.astropedal.AstroPedalNavigator;
import br.mackenzie.astropedal.utils.IoTMockupController;

public class GameScreen implements Screen {

    final AstroPedalNavigator game;
    private IoTMockupController ioTController;
    private int currentLevel;

    private float shipYPosition;
    private float targetRPM = 60f;

    public GameScreen(final AstroPedalNavigator game, int level) {
        this.game = game;
        this.currentLevel = level;
        this.ioTController = new IoTMockupController();
        shipYPosition = Gdx.graphics.getHeight() / 2f;
        Gdx.app.log("GameScreen", "Iniciando Fase: " + currentLevel);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float rpm = ioTController.getCurrentRPM();
        float verticalSpeed = (rpm - targetRPM) * 0.1f;
        shipYPosition += verticalSpeed;

        shipYPosition = Math.min(shipYPosition, Gdx.graphics.getHeight() - 50);
        shipYPosition = Math.max(shipYPosition, 50);

        Gdx.app.log("GameLogic", "RPM: " + rpm + ", Ship Y: " + shipYPosition);
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}