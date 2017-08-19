package com.desenvolvedorindie.gdxcamera.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class DraggableCameraInputProcessor extends InputAdapter {

    private Vector3 last_touch_down;
    private Camera camera;

    private float max_width;
    private float max_height;

    private boolean isBounded;          // camera movement lock

    public DraggableCameraInputProcessor(Camera camera) {
        last_touch_down = new Vector3();
        this.camera = camera;

        isBounded = false;
    }

    public DraggableCameraInputProcessor(Camera camera, float max_width, float max_height) {
        last_touch_down = new Vector3();

        this.camera = camera;
        this.max_width = max_width;
        this.max_height = max_height;

        isBounded = true;
    }

    public boolean isBounded() {
        return isBounded;
    }

    public void unbound() {
        isBounded = false;
    }

    public void setBound(float max_width, float max_height) {
        this.max_width = max_width;
        this.max_height = max_height;
        isBounded = true;
        cameraUpdate();
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public void cameraUpdate() {
        if (isBounded) {
            camera.position.x = Math.min(Math.max(camera.position.x, Gdx.graphics.getWidth() / 2), max_width - Gdx.graphics.getWidth() / 2);
            camera.position.y = Math.min(Math.max(camera.position.y, Gdx.graphics.getHeight() / 2), max_height - Gdx.graphics.getHeight() / 2);
        }
        camera.update();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        last_touch_down.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        Vector3 touched = new Vector3(last_touch_down);
        camera.unproject(touched);

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        last_touch_down.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float delta_x = last_touch_down.x - Gdx.input.getX();
        float delta_y = last_touch_down.y - Gdx.input.getY();
        last_touch_down.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        camera.translate(delta_x, -delta_y, 0);
        cameraUpdate();

        return false;
    }
}
