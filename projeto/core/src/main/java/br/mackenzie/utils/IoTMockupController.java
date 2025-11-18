package br.mackenzie.utils;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;

/**
 * Controller simulado para RPM com comportamento mais estável
 * - filtro EMA
 * - aumento moderado por press
 * - decaimento suave
 * - coleta de amostras para estatísticas
 */
public class IoTMockupController {

    private float currentRPM;
    private float emaRPM; // filtro exponencial
    private Random random;

    private final float MAX_RPM = 120f;
    private final float MIN_RPM = 10f;

    private final float INCREASE_AMOUNT = 6f; // por pressionamento
    private final float DECAY_RATE = 12f; // por segundo

    private final float EMA_ALPHA = 0.12f; // suavização

    // estatisticas por sessão
    private float sessionTime = 0f;
    private float sessionInRangeTime = 0f;
    private float rpmSum = 0f;
    private float rpmSumSq = 0f;
    private int rpmCount = 0;
    private ArrayList<Float> pressTimes = new ArrayList<>();

    public IoTMockupController() {
        random = new Random();
        currentRPM = 60f + random.nextFloat() * 10f;
        emaRPM = currentRPM;
        Gdx.app.log("IoTMockup", "Inicial RPM: " + currentRPM);
    }

    /** Inicia sessão de coleta */
    public void startSession() {
        sessionTime = 0f;
        sessionInRangeTime = 0f;
        rpmSum = 0f; rpmSumSq = 0f; rpmCount = 0;
        pressTimes.clear();
        currentRPM = 60f + random.nextFloat() * 6f;
        emaRPM = currentRPM;
    }

    /** Aumenta RPM como se fosse um botão pressionado */
    public void increaseRpm() {
        currentRPM += INCREASE_AMOUNT;
        if (currentRPM > MAX_RPM) currentRPM = MAX_RPM;
        pressTimes.add(sessionTime);
    }

    /** Atualiza RPM suavizado e decaimento */
    public void update(float delta) {
        currentRPM -= DECAY_RATE * delta;
        currentRPM += (random.nextFloat() - 0.5f) * 0.5f;

        if (currentRPM < MIN_RPM) currentRPM = MIN_RPM;
        if (currentRPM > MAX_RPM) currentRPM = MAX_RPM;

        emaRPM = EMA_ALPHA * currentRPM + (1f - EMA_ALPHA) * emaRPM;
        sessionTime += delta;
    }

    /** Registra amostra por frame */
    public void recordSample(float target, float delta) {
        float sample = emaRPM;
        rpmSum += sample;
        rpmSumSq += sample * sample;
        rpmCount++;
        if (Math.abs(sample - target) <= 5f) {
            sessionInRangeTime += delta;
        }
    }

    /** Calcula estatísticas da sessão */
    public LevelStats computeStats(float target) {
        LevelStats s = new LevelStats();
        s.sessionDuration = sessionTime;
        if (sessionTime > 0f) {
            s.timeInRangePercent = (sessionInRangeTime / sessionTime) * 100f;
        } else s.timeInRangePercent = 0f;

        // desvio padrão
        if (rpmCount > 1) {
            float mean = rpmSum / rpmCount;
            float variance = (rpmSumSq / rpmCount) - (mean * mean);
            if (variance < 0f) variance = 0f;
            float std = (float)Math.sqrt(variance);
            float smoothness = 100f - (std / (target + 1f)) * 100f;
            s.smoothnessPercent = Math.max(0f, Math.min(100f, smoothness));
        } else {
            s.smoothnessPercent = 0f;
        }

        // consistencia de press (intervalos)
        if (pressTimes.size() >= 2) {
            float sum = 0f;
            for (int i=1;i<pressTimes.size();i++) sum += (pressTimes.get(i)-pressTimes.get(i-1));
            float mean = sum / (pressTimes.size()-1);
            float sq = 0f;
            for (int i=1;i<pressTimes.size();i++) {
                float d = (pressTimes.get(i)-pressTimes.get(i-1)) - mean;
                sq += d*d;
            }
            float std = 0f;
            if (pressTimes.size()-1 > 0) std = (float)Math.sqrt(sq / (pressTimes.size()-1));
            float consistency = 100f - (std / (mean + 0.01f)) * 100f;
            s.pressConsistencyPercent = Math.max(0f, Math.min(100f, consistency));
        } else {
            s.pressConsistencyPercent = 0f;
        }

        return s;
    }

    /** Retorna RPM suavizado */
    public float getCurrentRPM() {
        return emaRPM;
    }

    /** Classe interna para estatísticas */
    public static class LevelStats {
        public float timeInRangePercent;
        public float smoothnessPercent;
        public float pressConsistencyPercent;
        public float sessionDuration;
    }
}
