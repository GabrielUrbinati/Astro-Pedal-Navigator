package br.mackenzie.utils;

import java.util.Random;

import com.badlogic.gdx.Gdx;

public class IoTMockupController {

    private float currentRPM = 0f;
    private Random random;

    public IoTMockupController() {
        this.random = new Random();
        Gdx.app.log("IoTMockup", "Simulador IoT Inicializado.");
    }

    public float getCurrentRPM() {
        float baseRPM = 60f;
        float variation = (random.nextFloat() - 0.5f) * 10f;
        currentRPM = currentRPM * 0.9f + (baseRPM + variation) * 0.1f;
        return Math.max(0, currentRPM);
    }
}