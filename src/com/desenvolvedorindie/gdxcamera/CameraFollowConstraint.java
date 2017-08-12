package com.desenvolvedorindie.gdxcamera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;

public class CameraFollowConstraint implements CameraConstraint {

    private static final Vector3 temp = new Vector3();

    private Vector3 position;

    private Vector3 offset;

    private float duration;
    private float time;

    private Interpolation interpolation;

    public CameraFollowConstraint(Vector3 position, Vector3 offset) {
        this.position = position;
        this.offset = offset;
    }

    public CameraFollowConstraint(Vector3 position) {
        this(position, Vector3.Zero);
    }

    public CameraFollowConstraint(Vector3 position, Vector3 offset, float duration, Interpolation interpolation) {
        this(position, offset);
        this.duration = duration;
        this.interpolation = interpolation;
    }

    public CameraFollowConstraint(Vector3 position, float duration, Interpolation interpolation) {
        this(position, Vector3.Zero, duration, interpolation);
    }

    public CameraFollowConstraint(Vector3 position, float duration) {
        this(position, Vector3.Zero, duration, Interpolation.smooth);
    }

    public CameraFollowConstraint(Vector3 position, Vector3 offset, float duration) {
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

    @Override
    public void debug(Camera camera, ShapeRenderer shapeRenderer, float debug) {
        temp.set(position).add(offset);

        shapeRenderer.setProjectionMatrix(camera.combined);

        float radius = 20;

        if(camera instanceof OrthographicCamera) {
            radius *= ((OrthographicCamera) camera).zoom;
        }

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.ellipse(temp.x - radius / 2f, temp.y - radius / 2f, radius, radius);

        shapeRenderer.setColor(Color.CORAL);
        shapeRenderer.ellipse(camera.position.x - radius / 2f, camera.position.y - radius / 2f, radius, radius);
        shapeRenderer.end();
    }
}
