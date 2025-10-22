package br.mackenzie.astropedal.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class GameAssets {

   
    public static Texture shipTexture;
    public static Texture asteroidTexture;
    public static Texture backgroundTexture; 

    public static void load() {
         
            shipTexture = new Texture(Gdx.files.internal("nave.png"));
            asteroidTexture = new Texture(Gdx.files.internal("asteroid.png"));
            backgroundTexture = new Texture(Gdx.files.internal("background.png"));
            
        
    }

    public static void dispose() {
        
        if (shipTexture != null) shipTexture.dispose();
        if (asteroidTexture != null) asteroidTexture.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}
