package br.mackenzie.screens;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import br.mackenzie.AstroPedalNavigator;
import br.mackenzie.ai.AIsteroid;
import br.mackenzie.utils.GameAssets;
import br.mackenzie.utils.IoTMockupController;

public abstract class BaseLevelScreen implements Screen {

    protected final AstroPedalNavigator game;
    protected SpriteBatch batch;
    protected BitmapFont font;

    protected IoTMockupController ioT;

    protected float shipX, shipY;
    protected final float shipW = 96f, shipH = 48f;

    // Obstáculos simples
    protected class Obstacle {
        Rectangle rect;
        float speed;
        Obstacle(Rectangle r, float speed) { rect = r; this.speed = speed; }
    }
    protected Array<Obstacle> obstacles;
    protected Array<AIsteroid> aiAsteroids;

    protected Random random = new Random();
    protected float spawnTimer = 0f;
    protected float spawnInterval = 1.2f;

    protected int score = 0;
    protected boolean gameOver = false;

    protected int levelNumber = 1;

    // level duration
    protected final float LEVEL_DURATION = 60f;
    protected float levelTimer = 0f;

    // gameplay mapping
    protected float targetRPM = 70f;
    protected final float VERT_SENS = 10f;

    // settings (for custom levels)
    protected boolean aiEnabled = false;
    protected String aiIntensity = "medium"; // none, light, medium, aggressive
    protected float obstacleBaseSpeed = 200f; // px/s

    protected Texture backgroundTexture;

    public BaseLevelScreen(AstroPedalNavigator game, int level) {
        this.game = game;
        this.levelNumber = level;
        this.batch = game.batch;
        this.font = game.font;

        ioT = new IoTMockupController();
        ioT.startSession();

        shipX = Gdx.graphics.getWidth() * 0.12f;
        shipY = Gdx.graphics.getHeight() / 2f;

        obstacles = new Array<>();
        aiAsteroids = new Array<>();

        setBackgroundForLevel(level);
    }

    protected void setBackgroundForLevel(int level) {
        switch (level) {
            case 1: backgroundTexture = GameAssets.get("textures/background.png"); break;
            case 2: backgroundTexture = GameAssets.get("textures/background2.png"); break;
            case 3: backgroundTexture = GameAssets.get("textures/background3.png"); break;
            default: backgroundTexture = GameAssets.get("textures/background.png");
        }
    }

    @Override
    public void render(float delta) {
        if (gameOver) {
            renderGameOver();
            return;
        }

        levelTimer += delta;
        spawnTimer += delta;

        ioT.update(delta);
        float rpm = ioT.getCurrentRPM();

        // controle via seta para cima (apenas para teste/manual)
        if (Gdx.input.isKeyJustPressed(Keys.UP)) {
            ioT.increaseRpm();
        }

        // cálculo de subida suavizado
        float rpmDelta = rpm - targetRPM;
        float maxDelta = 5f; // limita subida/descida extrema
        rpmDelta = Math.max(-maxDelta, Math.min(rpmDelta, maxDelta));
        float smoothing = 1.5f; // controla quão rápido o ship reage
        shipY += rpmDelta * VERT_SENS * smoothing * delta;
        shipY = Math.max(0f, Math.min(shipY, Gdx.graphics.getHeight() - shipH));

        ioT.recordSample(targetRPM, delta);

        // spawn de obstáculos
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            spawnObstacle();
            if (aiEnabled && random.nextFloat() < 0.25f) spawnAI();
        }

        // atualizar obstáculos
        Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle o = it.next();
            o.rect.x -= o.speed * delta;
            if (o.rect.x + o.rect.width < 0) { 
                it.remove(); 
                score += 5; 
                // passa de fase ao atingir 100 pontos
                if (score >= 100) finishLevel();
            }
            else if (Intersector.overlaps(getShipRect(), o.rect)) { gameOver = true; }
        }

        // atualizar asteroids AI
        Iterator<AIsteroid> ait = aiAsteroids.iterator();
        while (ait.hasNext()) {
            AIsteroid a = ait.next();
            a.update(delta, shipX + shipW/2f, shipY + shipH/2f);
            if (a.rect.x + a.rect.width < 0) { ait.remove(); }
            else if (Intersector.overlaps(getShipRect(), a.rect)) { gameOver = true; }
        }

        if (levelTimer >= LEVEL_DURATION) {
            finishLevel();
            return;
        }

        Gdx.gl.glClearColor(0f, 0f, 0.06f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        if (backgroundTexture != null)
            batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch.draw(GameAssets.get("textures/nave.png"), shipX, shipY, shipW, shipH);

        for (Obstacle o : obstacles)
            batch.draw(GameAssets.get("textures/asteroid.jpg"), o.rect.x, o.rect.y, 48f, 48f); // tamanho fixo

        for (AIsteroid a : aiAsteroids)
            batch.draw(GameAssets.get("textures/asteroid2.png"), a.rect.x, a.rect.y, 64f, 48f); // tamanho fixo

        font.draw(batch, "Level: " + levelNumber, 10, Gdx.graphics.getHeight()-10);
        font.draw(batch, String.format("RPM: %.1f", rpm), 10, Gdx.graphics.getHeight()-30);
        font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight()-50);
        font.draw(batch, String.format("Time: %.0f", LEVEL_DURATION - levelTimer), Gdx.graphics.getWidth()-120, Gdx.graphics.getHeight()-10);
        batch.end();
    }

   protected void spawnObstacle() {
    float w = 48f;  // tamanho fixo pra não mudar mais
    float h = 48f;  // tamanho fixo
    float x = Gdx.graphics.getWidth() + 20f;
    float y = random.nextFloat() * (Gdx.graphics.getHeight() - h); // qualquer posição vertical
    Rectangle r = new Rectangle(x, y, w, h);
    obstacles.add(new Obstacle(r, obstacleBaseSpeed));
}

    protected void spawnAI() {
        float w = 64f, h = 48f;
        float x = Gdx.graphics.getWidth() + 20f;
        float y = random.nextInt(Math.max(1, Gdx.graphics.getHeight()- (int)h));
        AIsteroid a = new AIsteroid(x, y, w, h, aiIntensity, obstacleBaseSpeed * 0.9f);
        aiAsteroids.add(a);
    }

    protected float randomYForObstacle(float h) {
        return random.nextBoolean() ? Gdx.graphics.getHeight() - h - 20 : 20;
    }

    protected Rectangle getShipRect() {
        return new Rectangle(shipX, shipY, shipW, shipH);
    }

    protected void finishLevel() {
        IoTMockupController.LevelStats stats = ioT.computeStats(targetRPM);
        float composite = stats.timeInRangePercent * 0.6f + stats.smoothnessPercent * 0.4f;
        int finalScore = Math.round(composite);
        dispose();
        game.setScreen(new LevelSummaryScreen(game, levelNumber, finalScore, stats));
    }

    protected void renderGameOver() {
        Gdx.gl.glClearColor(0.08f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font.draw(batch, "GAME OVER", Gdx.graphics.getWidth()/2f - 60, Gdx.graphics.getHeight()/2f + 20);
        font.draw(batch, "Score final: " + score, Gdx.graphics.getWidth()/2f - 60, Gdx.graphics.getHeight()/2f - 0);
        font.draw(batch, "Toque para voltar ao menu", Gdx.graphics.getWidth()/2f - 90, Gdx.graphics.getHeight()/2f - 40);
        batch.end();

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
