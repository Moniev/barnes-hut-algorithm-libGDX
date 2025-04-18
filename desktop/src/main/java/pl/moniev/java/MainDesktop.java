package pl.moniev.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import pl.moniev.core.Main;

public class MainDesktop {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Barnes-Hut algorithm";
        config.width = 1440; 
        config.height = 1440; 
		new LwjglApplication(new Main(), config);
	}
}
