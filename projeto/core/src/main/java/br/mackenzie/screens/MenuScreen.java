package br.mackenzie.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import br.mackenzie.AstroPedalNavigator;
import br.mackenzie.utils.GameAssets;

public class MenuScreen extends ScreenAdapter {

    private final AstroPedalNavigator game;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture titleTexture;
    private BitmapFont font;

    public MenuScreen(AstroPedalNavigator game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        GameAssets.load();
        GameAssets.finishLoading();

        backgroundTexture = GameAssets.get("textures/menu_background.png");
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        if (backgroundTexture != null)
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (titleTexture != null)
            batch.draw(titleTexture,
                (Gdx.graphics.getWidth() - titleTexture.getWidth()) / 2f,
                Gdx.graphics.getHeight() * 0.65f
            );

        font.draw(batch, "Press ENTER to Start",
                Gdx.graphics.getWidth() / 2f - 80,
                Gdx.graphics.getHeight() * 0.30f
        );

        batch.end();

        if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.justTouched()) {
             game.setScreen(new Level1Screen(game)); // inicia nível 1
        }
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
