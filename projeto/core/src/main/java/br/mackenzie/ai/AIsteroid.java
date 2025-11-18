package br.mackenzie.ai;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class AIsteroid {

    public Rectangle rect;
    private Texture texture;

    private Vector2 velocity;
    private float speed;

    private String intensity; // "light", "medium", "aggressive"

    public AIsteroid(float x, float y, float w, float h, String intensity, float baseSpeed) {
        this.texture = new Texture("asteroid2.png");

        this.rect = new Rectangle(x, y, w, h);
        this.velocity = new Vector2(0, 0);
        this.intensity = intensity;

        // velocidade base
        this.speed = baseSpeed;
    }

    public void update(float delta, float targetX, float targetY) {

        // Direção até o jogador
        Vector2 desired = new Vector2(targetX - rect.x, targetY - rect.y);

        // intensidade da IA
        float followStrength = 0.1f; // padrão

        switch (intensity) {
            case "light": followStrength = 0.05f; break;
            case "medium": followStrength = 0.12f; break;
            case "aggressive": followStrength = 0.22f; break;
        }

        desired.nor().scl(speed);

        // Lerp para suavidade
        velocity.lerp(desired, followStrength);

        // move
        rect.x += velocity.x * delta;
        rect.y += velocity.y * delta;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
    }

    public void dispose() {
        texture.dispose();
    }
}
