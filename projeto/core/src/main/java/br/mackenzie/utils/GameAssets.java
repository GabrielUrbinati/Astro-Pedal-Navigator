package br.mackenzie.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class GameAssets {

    private static final AssetManager manager = new AssetManager();
    private static boolean loaded = false;

    /** Carrega todos os assets do jogo */
    public static void load() {
        if (loaded) return;

        manager.load("textures/background.png", Texture.class);
        manager.load("textures/background2.png", Texture.class);
        manager.load("textures/background3.png", Texture.class);

        manager.load("textures/asteroid.jpg", Texture.class);
        manager.load("textures/asteroid2.png", Texture.class);

        manager.load("textures/nave.png", Texture.class);

        manager.load("textures/menu_background.png", Texture.class);
        

        loaded = true;
    }

    /** Aguarda o carregamento completo */
    public static void finishLoading() {
        manager.finishLoading();
    }

    /** Retorna a Texture pelo nome; loga erro se não carregada */
    public static Texture get(String name) {
        if (!loaded || !manager.isLoaded(name, Texture.class)) {
            Gdx.app.error("GameAssets", "Asset não carregado: " + name);
            return null;
        }
        return manager.get(name, Texture.class);
    }

    /** Libera todos os assets */
    public static void dispose() {
        manager.dispose();
        loaded = false;
    }
}
