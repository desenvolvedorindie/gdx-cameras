package com.desenvolvedorindie.gdxcamera;

import com.badlogic.gdx.graphics.Camera;

public class CameraUpdater {

    private ICameraStrategy strategy;

    public CameraUpdater() {
    }

    public CameraUpdater(ICameraStrategy strategy) {
        setCameraPositionStrategy(strategy);
    }

    public void update(Camera camera, float delta) {
        if (strategy != null) {
            strategy.update(camera, delta);
        }
    }

    public void setCameraPositionStrategy(ICameraStrategy strategy) {
        this.strategy = strategy;
    }


}
