package br.mackenzie.utils;

import java.util.Random;

import com.badlogic.gdx.Gdx;

public class IoTMockupController {

    private float currentRPM = 60f; 
    private Random random;
    
    // Constantes para controle
    private final float MAX_RPM = 120f;
    private final float INCREASE_AMOUNT = 15f; 
    private final float DECAY_RATE = 40f; // Queda AGRESSIVA (40 RPM por segundo)

    public IoTMockupController() {
        this.random = new Random();
        Gdx.app.log("IoTMockup", "Simulador IoT Inicializado.");
    }

    public void increaseRpm() {
        currentRPM = Math.min(currentRPM + INCREASE_AMOUNT, MAX_RPM);
    }
    
    public void update(float delta) {
        // 1. Queda agressiva
        currentRPM = Math.max(currentRPM - (DECAY_RATE * delta), 10f); 
        
        // 2. Adiciona um pouco de ruído
        float variation = (random.nextFloat() - 0.5f) * 2f;
        currentRPM += variation * delta;
        
        currentRPM = Math.max(10f, Math.min(currentRPM, MAX_RPM));
    }

    public float getCurrentRPM() {
        return currentRPM;
    }
}