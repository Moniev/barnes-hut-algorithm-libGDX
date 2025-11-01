package pl.moniev.java;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation;

import pl.moniev.core.Main;

public class MainDesktop {
  public static void main(String[] args) {
    Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
    config.setTitle("Barnes-Hut algorithm");
    config.setWindowedMode(1440, 1440);
    config.useVsync(false);
    config.setOpenGLEmulation(GLEmulation.GL20, 0, 0);
    new Lwjgl3Application(new Main(), config);
  }
}
