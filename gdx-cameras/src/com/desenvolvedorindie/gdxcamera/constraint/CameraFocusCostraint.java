package com.desenvolvedorindie.gdxcamera.constraint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class CameraFocusCostraint implements CameraConstraint {

    private boolean enabled = true;

    public CameraFocusCostraint() {

    }

    @Override
    public void update(Camera camera, float delta) {
        if (!enabled)
            return;
    }

    @Override
    public void debug(Camera camera, ShapeRenderer shapeRenderer, float delta) {
        if (!enabled)
            return;
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
