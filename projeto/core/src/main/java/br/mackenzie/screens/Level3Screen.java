package br.mackenzie.screens;

import br.mackenzie.AstroPedalNavigator;

public class Level3Screen extends BaseLevelScreen {

    public Level3Screen(AstroPedalNavigator game) {
        super(game, 3);
        targetRPM = 90f;
        setBackgroundForLevel(3);
        obstacleBaseSpeed = 260f;
        aiEnabled = false;
        aiIntensity = "aggressive";
        spawnInterval = 0.9f;
    }
}
