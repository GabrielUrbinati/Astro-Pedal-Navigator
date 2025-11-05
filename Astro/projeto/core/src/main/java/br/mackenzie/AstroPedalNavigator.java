package br.mackenzie;

import com.badlogic.gdx.Game;

import br.mackenzie.screens.MenuScreen;
import br.mackenzie.utils.GameAssets;

public class AstroPedalNavigator extends Game {

	@Override
	public void create () {
		GameAssets.load();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void dispose () {}
}