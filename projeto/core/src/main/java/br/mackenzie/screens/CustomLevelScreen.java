package br.mackenzie.screens;

import br.mackenzie.AstroPedalNavigator;

/**
 * Nível customizável (usado para level 4 em diante). Recebe configurações via construtor.
 */
public class CustomLevelScreen extends BaseLevelScreen {

    private int bgChoice = 1;
    private int targetChoiceRPM = 60;
    private String aiChoice = "none"; // none/light/medium/aggressive
    private float obstacleSpeedOverride = 200f;

    public CustomLevelScreen(AstroPedalNavigator game, int level, int bgChoice, int targetRPM, String aiChoice, float obstacleSpeedPxPerSec) {
        super(game, level);
        this.bgChoice = bgChoice;
        this.targetChoiceRPM = targetRPM;
        this.aiChoice = aiChoice;
        this.obstacleSpeedOverride = obstacleSpeedPxPerSec;

        targetRPM = targetChoiceRPM;
        aiEnabled = !aiChoice.equals("none");
        aiIntensity = aiChoice;
        obstacleBaseSpeed = obstacleSpeedOverride;
        spawnInterval = 1.0f;
    }
}
