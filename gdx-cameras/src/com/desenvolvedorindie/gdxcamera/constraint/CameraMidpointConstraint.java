package com.desenvolvedorindie.gdxcamera.constraint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;


public class CameraMidpointConstraint implements CameraConstraint {

    private boolean enabled = true;

    private boolean autoZoom;

    private float margin;

    private Array<Vector3> points;

    private Vector3 pos = new Vector3();
    private Vector2 min = new Vector2();
    private Vector2 max = new Vector2();;

    public CameraMidpointConstraint(boolean autoZoom, float margin) {
        this.autoZoom = autoZoom;
        this.margin = margin;
    }

    @Override
    public void update(Camera camera, float delta) {
        if (!enabled)
            return;

        if (points != null && points.size > 0) {
            pos.set(points.first());

            if (autoZoom) {
                min.set(points.first().x, points.first().y);
                max.set(min);
            }

            for (int i = 1; i < points.size; i++) {
                Vector3 point = points.get(i);

                pos.add(point);

                if (autoZoom) {
                    if (point.x < min.x)
                        min.x = point.x;
                    if (point.y < min.y)
                        min.y = point.y;
                    if (point.x > max.x)
                        max.x = point.x;
                    if (point.y > max.y)
                        max.y = point.y;
                }
            }

            pos.scl(1f / points.size);

            if (autoZoom && camera instanceof OrthographicCamera) {
                OrthographicCamera ortoCamera = (OrthographicCamera) camera;
                float difX = (max.x - min.x) + margin;
                float difY = (max.y - min.y) + margin;
                if (difX >= difY) {
                    ortoCamera.zoom = difX / camera.viewportWidth;
                } else {
                    ortoCamera.zoom = difY / camera.viewportHeight;
                }
            }

            camera.position.set(pos);
            camera.update();
        }
    }

    @Override
    public void debug(Camera camera, ShapeRenderer shapeRenderer, float delta) {
        if (!enabled)
            return;

        if (points != null) {
            shapeRenderer.setProjectionMatrix(camera.combined);

            float r = DEFAULT_RADIUS;

            if (camera instanceof OrthographicCamera) {
                r *= ((OrthographicCamera) camera).zoom;
            }

            shapeRenderer.setColor(Color.ORANGE);

            for (Vector3 point : points) {
                shapeRenderer.ellipse(point.x - r / 2f, point.y - r / 2f, r, r);
            }

            shapeRenderer.setColor(Color.CORAL);

            shapeRenderer.ellipse(camera.position.x - r / 2f, camera.position.y - r / 2f, r, r);

            if (autoZoom) {
                shapeRenderer.rect(min.x, min.y, max.x - min.x, max.y - min.y);
            }
        }
    }

    @Override
    public boolean isEnable() {
        return enabled;
    }

    @Override
    public void setEnable(boolean value) {
        this.enabled = value;
    }

    public boolean isAutoZoom() {
        return autoZoom;
    }

    public void setAutoZoom(boolean autoZoom) {
        this.autoZoom = autoZoom;
    }

    public float getMargin() {
        return margin;
    }

    public void setMargin(float margin) {
        this.margin = margin;
    }

    public Array<Vector3> getPoints() {
        return points;
    }

    public void setPoints(Array<Vector3> points) {
        this.points = points;
    }
}
