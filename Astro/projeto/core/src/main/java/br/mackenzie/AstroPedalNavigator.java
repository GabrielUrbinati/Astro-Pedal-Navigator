package br.mackenzie.astropedal;

import com.badlogic.gdx.Game;
import br.mackenzie.astropedal.screens.MenuScreen;

public class AstroPedalNavigator extends Game {

	@Override
	public void create () {
		setScreen(new MenuScreen(this));
	}

	@Override
	public void dispose () {}
}