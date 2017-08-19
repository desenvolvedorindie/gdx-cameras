package com.desenvolvedorindie.gdxcamera.constraint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.collision.BoundingBox;

public class CameraConstraintBoundingBox implements CameraConstraint {

    private BoundingBox boundingBox;

    private boolean enabled = true;

    public CameraConstraintBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    @Override
    public void update(Camera camera, float delta) {
        if (!enabled)
            return;

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
        if (!enabled)
            return;

        shapeRenderer.setColor(Color.BROWN);

        shapeRenderer.rect(boundingBox.min.x, boundingBox.min.y, boundingBox.max.x - boundingBox.min.x, boundingBox.max.y - boundingBox.min.y);
    }

    @Override
    public boolean isEnable() {
        return enabled;
    }

    @Override
    public void setEnable(boolean value) {
        enabled = value;
    }

}
