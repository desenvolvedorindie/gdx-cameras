package com.desenvolvedorindie.gdxcamera.constraint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface CameraConstraint {

    void update(Camera camera, float delta);

    void debug(Camera camera, ShapeRenderer shapeRenderer, float delta);

    boolean isEnable();

    void setEnable(boolean value);

}