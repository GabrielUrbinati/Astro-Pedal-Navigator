package br.mackenzie.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import br.mackenzie.AstroPedalNavigator;
import br.mackenzie.utils.IoTMockupController;

/**
 * Tela de resumo exibida após cada nível. Mostra métricas e permite avançar.
 * Após level 3, o botão "Continuar" leva para ConfigScreen.
 */
public class LevelSummaryScreen implements Screen {

    private final AstroPedalNavigator game;
    private final int completedLevel;
    private final int finalScore;
    private final IoTMockupController.LevelStats stats;

    private SpriteBatch batch;
    private BitmapFont font;

    public LevelSummaryScreen(AstroPedalNavigator game, int completedLevel, int finalScore, IoTMockupController.LevelStats stats) {
        this.game = game;
        this.completedLevel = completedLevel;
        this.finalScore = finalScore;
        this.stats = stats;
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.06f, 0.06f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "LEVEL " + completedLevel + " COMPLETED", 40, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Score: " + finalScore, 40, Gdx.graphics.getHeight() - 90);
        font.draw(batch, String.format("Tempo na faixa alvo: %.1f %%", stats.timeInRangePercent), 40, Gdx.graphics.getHeight() - 140);
        font.draw(batch, String.format("Suavidade do ritmo: %.1f %%", stats.smoothnessPercent), 40, Gdx.graphics.getHeight() - 180);
        font.draw(batch, String.format("Consistência das pressões: %.1f %%", stats.pressConsistencyPercent), 40, Gdx.graphics.getHeight() - 220);
        font.draw(batch, "Toque para continuar", 40, 120);
        font.draw(batch, "Pressione 'M' para voltar ao menu", 40, 90);
        batch.end();

        if (Gdx.input.justTouched()) {
            proceedAfterSummary();
        }
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)) {
            dispose();
            game.setScreen(new MenuScreen(game));
        }
    }

    private void proceedAfterSummary() {
        dispose();
        if (completedLevel < 3) {
            // seguir para o próximo level estático
            if (completedLevel == 1) game.setScreen(new Level2Screen(game));
            else if (completedLevel == 2) game.setScreen(new Level3Screen(game));
        } else {
            // após level 3 -> abrir ConfigScreen (para level 4 custom)
            game.setScreen(new ConfigScreen(game, 4));
        }
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
