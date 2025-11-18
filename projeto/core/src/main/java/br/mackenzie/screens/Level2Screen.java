package br.mackenzie.screens;

import br.mackenzie.AstroPedalNavigator;

public class Level2Screen extends BaseLevelScreen {

    public Level2Screen(AstroPedalNavigator game) {
        super(game, 2);
        targetRPM = 80f;
        setBackgroundForLevel(2);
        obstacleBaseSpeed = 220f;
        aiEnabled = false; // desativando IA para teste
        spawnInterval = 1.0f;
    }
}