package br.mackenzie.screens;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import br.mackenzie.AstroPedalNavigator;
import br.mackenzie.utils.GameAssets;
import br.mackenzie.utils.IoTMockupController;

public class GameScreen implements Screen {

	final AstroPedalNavigator game;
	private SpriteBatch batch;
	private BitmapFont font;

	private IoTMockupController ioTController;

	// ship position (fixed X, variable Y)
	private final float shipX;
	private float shipY;
	private final float shipWidth = 96f;
	private final float shipHeight = 48f;
	private boolean isCrouching = false;
	private float crouchTimer = 0f;
	private final float CROUCH_DURATION = 1.0f; 

	// mapping
	private float targetRPM = 70f; 
	private final float VERTICAL_SENSITIVITY = 10.0f; // SENSIBILIDADE ALTA

	// obstacles
	private class Obstacle {
		Rectangle rect;
		boolean top; 
		float speed;
		Obstacle(Rectangle r, boolean top, float speed) { this.rect = r; this.top = top; this.speed = speed; }
	}
	private Array<Obstacle> obstacles;
	private float spawnTimer = 0f;
	private final float SPAWN_INTERVAL_BASE = 1.2f; 
	private Random random = new Random();

	// gameplay
	private int currentLevel = 1;
	private float elapsedTime = 0f;
	private int score = 0;
	private boolean gameOver = false;

	public GameScreen(final AstroPedalNavigator game, int level) {
		this.game = game;
		this.currentLevel = level;

		batch = new SpriteBatch();
		font = new BitmapFont();

		ioTController = new IoTMockupController();

		shipX = Gdx.graphics.getWidth() * 0.12f;
		shipY = Gdx.graphics.getHeight() / 2f;

		obstacles = new Array<>();
		Gdx.app.log("GameScreen", "Iniciando nivel " + level);
		targetRPM = 70f + level * 10f; 
	}

	@Override
	public void render(float delta) {
		if (gameOver) {
			renderGameOver();
			return;
		}

		elapsedTime += delta;
		spawnTimer += delta;

		// --- CONTROLE DE RPM ---
		ioTController.update(delta); 
		float rpm = ioTController.getCurrentRPM();
		
		// 2. CHAMA O AUMENTO: Captura a tecla "Seta para Cima"
		if (Gdx.input.isKeyJustPressed(Keys.UP)) { 
		    ioTController.increaseRpm();
		}
		// --- FIM DO CONTROLE DE RPM ---

		// --- MOVIMENTO VERTICAL ---
		// ship sobe se rpm > targetRPM, desce se rpm < targetRPM
		float rpmDelta = rpm - targetRPM;
		
		shipY += rpmDelta * VERTICAL_SENSITIVITY * delta;
		
		// clamp nos limites da tela
		shipY = Math.max(0f, Math.min(shipY, Gdx.graphics.getHeight() - shipHeight));

		// --- spawn obstaculos periodicamente ---
		float spawnInterval = SPAWN_INTERVAL_BASE - (currentLevel - 1) * 0.15f; 
		if (spawnInterval < 0.5f) spawnInterval = 0.5f;
		if (spawnTimer >= spawnInterval) {
			spawnTimer = 0f;
			spawnObstacle();
		}

		// --- mover obstaculos e checar colisões ---
		float obstacleBaseSpeed = 200f + (currentLevel - 1) * 40f; 
		Iterator<Obstacle> it = obstacles.iterator();
		while (it.hasNext()) {
			Obstacle ob = it.next();
			ob.rect.x -= ob.speed * delta;
			
			if (ob.rect.x + ob.rect.width < 0) {
				it.remove();
				score += 5; 
				continue;
			}
			// colisão: comparar rects
			Rectangle shipRect = getShipRect(); 
			if (Intersector.overlaps(shipRect, ob.rect)) {
				Gdx.app.log("Game", "Colisão detectada! score: " + score);
				gameOver = true;
			}
		}

		// --- desenho ---
		Gdx.gl.glClearColor(0f, 0f, 0.06f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(GameAssets.backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// desenha nave 
		if (isCrouching) {
			float crouchHeight = shipHeight * 0.6f;
			batch.draw(GameAssets.shipTexture, shipX, shipY, shipWidth, crouchHeight);
		} else {
			batch.draw(GameAssets.shipTexture, shipX, shipY, shipWidth, shipHeight);
		}

		// desenha obstaculos
		for (Obstacle ob : obstacles) {
			batch.draw(GameAssets.asteroidTexture, ob.rect.x, ob.rect.y, ob.rect.width, ob.rect.height);
		}

		// HUD
		font.draw(batch, "Level: " + currentLevel, 10, Gdx.graphics.getHeight() - 10);
		font.draw(batch, String.format("RPM: %.1f", rpm), 10, Gdx.graphics.getHeight() - 30);
		font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 50);
		
		batch.end();
	}

	private void spawnObstacle() {
		boolean top = random.nextBoolean();
		float width = 64 + random.nextInt(64);
		float height = 32 + random.nextInt(96); 

		float x = Gdx.graphics.getWidth() + 20;
		float y;
		if (top) {
			y = Gdx.graphics.getHeight() - height - 20;
		} else {
			y = 20;
		}

		Rectangle rect = new Rectangle(x, y, width, height);
		float speed = 200f + random.nextFloat() * 80f + (currentLevel - 1) * 30f; 
		obstacles.add(new Obstacle(rect, top, speed));
	}

	private Rectangle getShipRect() {
		if (isCrouching) {
			float crouchHeight = shipHeight * 0.6f;
			return new Rectangle(shipX, shipY, shipWidth, crouchHeight);
		} else {
			return new Rectangle(shipX, shipY, shipWidth, shipHeight);
		}
	}

	private void renderGameOver() {
		Gdx.gl.glClearColor(0.08f, 0f, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		font.draw(batch, "GAME OVER", Gdx.graphics.getWidth()/2f - 60, Gdx.graphics.getHeight()/2f + 20);
		font.draw(batch, "Score final: " + score, Gdx.graphics.getWidth()/2f - 60, Gdx.graphics.getHeight()/2f - 0);
		font.draw(batch, "Toque para voltar ao menu", Gdx.graphics.getWidth()/2f - 90, Gdx.graphics.getHeight()/2f - 40);
		batch.end();

		if (Gdx.input.justTouched()) {
			dispose(); 
			game.setScreen(new MenuScreen(game));
		}
	}

	@Override public void show() {}
	@Override public void resize(int width, int height) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void hide() {}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}