package br.mackenzie.screens;

import br.mackenzie.AstroPedalNavigator;

public class Level1Screen extends BaseLevelScreen {

    public Level1Screen(AstroPedalNavigator game) {
        super(game, 1);
        targetRPM = 70f;
        setBackgroundForLevel(1);
        obstacleBaseSpeed = 180f;
        aiEnabled = false;
    }
}
