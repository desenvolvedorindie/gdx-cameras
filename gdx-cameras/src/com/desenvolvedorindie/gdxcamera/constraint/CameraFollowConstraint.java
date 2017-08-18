package com.desenvolvedorindie.gdxcamera.constraint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class CameraFollowConstraint implements CameraConstraint {

    private boolean enabled = true;

    private Vector3 position;

    private float duration;

    private float time;

    private Interpolation interpolation;

    public CameraFollowConstraint(Vector3 position) {
        this.position = position;
    }

    public CameraFollowConstraint(Vector3 position, float duration, Interpolation interpolation) {
        this(position);
        this.duration = duration;
        this.interpolation = interpolation;
    }

    public CameraFollowConstraint(Vector3 position, float duration) {
        this(position, duration, Interpolation.smooth);
    }

    @Override
    public void update(Camera camera, float delta) {
        if (!enabled)
            return;

        if (!position.equals(camera.position)) {
            if (interpolation != null) {
                time += delta;
                float progress = Math.min(1f, time / duration);

                camera.position.interpolate(position, progress, interpolation);
            } else {
                camera.position.set(position);
            }

            camera.update();
        } else {
            time = 0;
        }
    }

    @Override
    public void debug(Camera camera, ShapeRenderer shapeRenderer, float delta) {
        if (!enabled)
            return;

        float r = DEFAULT_RADIUS;

        if (camera instanceof OrthographicCamera) {
            r *= ((OrthographicCamera) camera).zoom;
        }

        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.ellipse(position.x - r / 2f, position.y - r / 2f, r, r);

        shapeRenderer.setColor(Color.CORAL);
        shapeRenderer.ellipse(camera.position.x - r / 2f, camera.position.y - r / 2f, r, r);
    }

    @Override
    public boolean isEnable() {
        return enabled;
    }

    @Override
    public void setEnable(boolean value) {
        enabled = value;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public Interpolation getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(Interpolation interpolation) {
        this.interpolation = interpolation;
    }


}
