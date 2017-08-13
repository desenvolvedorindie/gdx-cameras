package com.desenvolvedorindie.gdxcamera.constraint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.collision.BoundingBox;

import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.*;

public class CameraConstraintBoundingBox implements CameraConstraint {

    BoundingBox boundingBox;

    public CameraConstraintBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    @Override
    public void update(Camera camera, float delta) {
        boolean changed = false;

        if (camera.position.x < boundingBox.min.x) {
            camera.position.x = boundingBox.min.x;
            changed = true;
        }

        if (camera.position.y < boundingBox.min.y) {
            camera.position.y = boundingBox.min.y;
            changed = true;
        }

        if (camera.position.z < boundingBox.min.z) {
            camera.position.z = boundingBox.min.z;
            changed = true;
        }

        if (camera.position.x > boundingBox.max.x) {
            camera.position.x = boundingBox.max.x;
            changed = true;
        }

        if (camera.position.y > boundingBox.max.y) {
            camera.position.y = boundingBox.max.y;
            changed = true;
        }

        if (camera.position.z > boundingBox.max.z) {
            camera.position.z = boundingBox.max.z;
            changed = true;
        }

        if (changed) {
            camera.update();
        }
    }

    @Override
    public void debug(Camera camera, ShapeRenderer shapeRenderer, float delta) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeType.Line);

        shapeRenderer.setColor(Color.BROWN);

        shapeRenderer.rect(boundingBox.min.x, boundingBox.min.y, boundingBox.max.x - boundingBox.min.x, boundingBox.max.y - boundingBox.min.y);

        shapeRenderer.end();
    }
}
