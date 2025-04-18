package pl.moniev.core;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class InputController implements InputProcessor {
    private final Main main;

    public InputController(Main main) {
        this.main = main;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.T:
                main.toggleRenderTree();
                return true;
            case Input.Keys.B: 
                main.toggleRenderBodies();
                return true;    
            case Input.Keys.P: 
                main.togglePaused();
                return true;
            case Input.Keys.ESCAPE: 
                com.badlogic.gdx.Gdx.app.exit();
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (amount > 0) {
            main.getCamera().zoom += 1f; 
        } else if (amount < 0) {
            main.getCamera().zoom -= 1f;
        }
        main.getCamera().update();
        return true;
}
}
