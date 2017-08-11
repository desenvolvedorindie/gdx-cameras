package com.desenvolvedorindie.gdxcamera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public class CameraFollow implements ICameraStrategy {

    private static final Vector2 temp = new Vector2();
    private Vector2 position;
    private Vector2 offset;

    public CameraFollow(Vector2 position, Vector2 offset) {
        this.position = position;
        this.offset = offset;
    }

    public CameraFollow(Vector2 position) {
        this(position, Vector2.Zero);
    }

    @Override
    public void update(Camera camera) {
        temp.set(position).add(offset);

        if (!temp.equals(camera.position)) {
            camera.position.set(temp, 0);
            camera.update();
        }
    }
}
