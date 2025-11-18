package br.mackenzie.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import br.mackenzie.AstroPedalNavigator;

/**
 * Tela de configuração pós-level3 (também usada depois de qualquer custom level).
 * Permite selecionar:
 * - background (1/2/3)
 * - target RPM (40/60/80/100)
 * - obstacle speed (2/5/8 km/h -> convert to px/s)
 * - IA: none/light/medium/aggressive
 *
 * Interface simples: toque nas linhas para alternar/selecionar e toque "Start" para iniciar.
 */
public class ConfigScreen implements Screen {

    private final AstroPedalNavigator game;
    private final int nextLevelNumber; // ex.: 4 para começar

    private SpriteBatch batch;
    private BitmapFont font;

    // choices
    private int backgroundChoice = 1; // 1,2,3
    private int targetRPMChoice = 60; // 40,60,80,100
    private int obstacleSpeedChoiceKm = 5; // 2,5,8 km/h
    private String aiChoice = "none"; // none/light/medium/aggressive

    public ConfigScreen(AstroPedalNavigator game, int nextLevelNumber) {
        this.game = game;
        this.nextLevelNumber = nextLevelNumber;
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.08f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "CONFIGURAR LEVEL " + nextLevelNumber, 40, Gdx.graphics.getHeight() - 40);
        font.draw(batch, String.format("Background: [%d] (toque para alternar)", backgroundChoice), 40, Gdx.graphics.getHeight() - 100);
        font.draw(batch, String.format("Target RPM: [%d] (toque para alternar entre 40/60/80/100)", targetRPMChoice), 40, Gdx.graphics.getHeight() - 140);
        font.draw(batch, String.format("Obstacle speed (km/h): [%d] (toque para alternar 2/5/8)", obstacleSpeedChoiceKm), 40, Gdx.graphics.getHeight() - 180);
        font.draw(batch, String.format("AI: [%s] (toque para alternar none/light/medium/aggressive)", aiChoice), 40, Gdx.graphics.getHeight() - 220);
        font.draw(batch, "Toque para iniciar level customizado", 40, 120);
        font.draw(batch, "Toque com dois dedos (ou pressione M) para voltar ao menu", 40, 80);
        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            // Single tap: detect Y position to toggle specific option.
            // Simples heurística: top area toggles background, next toggles RPM, etc.
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            float x = Gdx.input.getX();

            // areas:
            if (y > Gdx.graphics.getHeight() - 120 && y < Gdx.graphics.getHeight() - 70) {
                // background area
                backgroundChoice++;
                if (backgroundChoice > 3) backgroundChoice = 1;
                return;
            }
            if (y > Gdx.graphics.getHeight() - 160 && y < Gdx.graphics.getHeight() - 120) {
                // rpm area
                if (targetRPMChoice == 40) targetRPMChoice = 60;
                else if (targetRPMChoice == 60) targetRPMChoice = 80;
                else if (targetRPMChoice == 80) targetRPMChoice = 100;
                else targetRPMChoice = 40;
                return;
            }
            if (y > Gdx.graphics.getHeight() - 200 && y < Gdx.graphics.getHeight() - 160) {
                // obstacle speed
                if (obstacleSpeedChoiceKm == 2) obstacleSpeedChoiceKm = 5;
                else if (obstacleSpeedChoiceKm == 5) obstacleSpeedChoiceKm = 8;
                else obstacleSpeedChoiceKm = 2;
                return;
            }
            if (y > Gdx.graphics.getHeight() - 240 && y < Gdx.graphics.getHeight() - 200) {
                // ai
                if (aiChoice.equals("none")) aiChoice = "light";
                else if (aiChoice.equals("light")) aiChoice = "medium";
                else if (aiChoice.equals("medium")) aiChoice = "aggressive";
                else aiChoice = "none";
                return;
            }

            // If touch near bottom -> start level
            if (y < 140 && y > 40) {
                startCustomLevel();
                return;
            }
        }

        // Keyboard shortcuts
        if (Gdx.input.isKeyJustPressed(Keys.M)) {
            game.setScreen(new MenuScreen(game));
        }
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            startCustomLevel();
        }
    }

    private void startCustomLevel() {
        // convert obstacleSpeedChoiceKm (km/h) to px/s (approx)
        // assume: 1 km/h ~ 0.27778 m/s; scale factor to px: choose 40 px ~ 1 m => 1 m = 40px => 1 km/h => 0.27778*40 = 11.111px/s
        float pxPerSec = obstacleSpeedChoiceKm * 0.27778f * 40f; // approx
        // Launch CustomLevelScreen
        game.setScreen(new CustomLevelScreen(game, nextLevelNumber, backgroundChoice, targetRPMChoice, aiChoice, pxPerSec));
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
