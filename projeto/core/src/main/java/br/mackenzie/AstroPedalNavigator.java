package br.mackenzie;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import br.mackenzie.screens.MenuScreen;
import br.mackenzie.utils.GameAssets;

public class AstroPedalNavigator extends Game {

    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        // Carrega todos os assets antes de qualquer tela
        GameAssets.load();
        GameAssets.finishLoading();

        batch = new SpriteBatch();
        font = new BitmapFont();

        // Primeira tela
        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // mantém o ciclo de renderização das screens
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();

        GameAssets.dispose(); // libera assets carregados
        super.dispose();
    }
}
