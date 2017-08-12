package com.desenvolvedorindie.gdxcamera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

public class CameraFollow implements ICameraStrategy {

    private static final Vector3 temp = new Vector3();

    private Vector3 position;

    private Vector3 offset;

    private float duration;
    private float time;

    private Interpolation interpolation;

    public CameraFollow(Vector3 position, Vector3 offset) {
        this.position = position;
        this.offset = offset;
    }

    public CameraFollow(Vector3 position) {
        this(position, Vector3.Zero);
    }

    public CameraFollow(Vector3 position, Vector3 offset, float duration, Interpolation interpolation) {
        this(position, offset);
        this.duration = duration;
        this.interpolation = interpolation;
    }

    public CameraFollow(Vector3 position, float duration, Interpolation interpolation) {
        this(position, Vector3.Zero, duration, interpolation);
    }

    public CameraFollow(Vector3 position, float duration) {
        this(position, Vector3.Zero, duration, Interpolation.smooth);
    }

    public CameraFollow(Vector3 position, Vector3 offset, float duration) {
        this(position, offset, duration, Interpolation.smooth);
    }

    @Override
    public void update(Camera camera, float delta) {
        temp.set(position).add(offset);

        if (!temp.equals(camera.position)) {
            if (interpolation != null) {
                time += delta;
                float progress = Math.min(1f, time / duration);

                camera.position.interpolate(temp, progress, interpolation);
            } else {
                camera.position.set(temp);
            }
            
            camera.update();
        } else {
            time = 0;
        }
    }
}
