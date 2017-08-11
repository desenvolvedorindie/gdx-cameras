package com.desenvolvedorindie.gdxcamera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

public class CameraZoom implements ICameraStrategy {

    private float[] zoomLevels;

    private int zoomLevel = 1;

    private int lastZoomLevel = zoomLevel;

    private boolean zoomFinished = true;

    private float zoomProgress = 0;

    private float zoomSpeed;

    public CameraZoom(float[] zoomLevels, float zoomSpeed) {
        this.zoomLevels = zoomLevels;
        this.zoomSpeed = zoomSpeed;
    }

    public CameraZoom(float[] zoomLevels) {
        this(zoomLevels, 0.1f);
    }

    public void zoomOut() {
        lastZoomLevel = zoomLevel;
        zoomLevel = ++zoomLevel % zoomLevels.length;
        zoomProgress = 0;
        zoomFinished = false;
        Gdx.app.log("CameraZoom", String.valueOf(zoomLevels[zoomLevel]));
    }

    public void zoomIn() {
        lastZoomLevel = zoomLevel;
        --zoomLevel;
        if (zoomLevel < 0) {
            zoomLevel = zoomLevels.length - 1;
        }
        zoomProgress = 0;
        zoomFinished = false;
        Gdx.app.log("CameraZoom", String.valueOf(zoomLevels[zoomLevel]));
    }

    @Override
    public void update(Camera camera) {
        if (!(camera instanceof OrthographicCamera))
            throw new RuntimeException("Camera zoom supports only in CameraUpdater");

        OrthographicCamera cam = (OrthographicCamera) camera;

        if (!zoomFinished) {
            zoomProgress += zoomSpeed;
            if (zoomProgress > 1) {
                zoomProgress = 1;
                zoomFinished = true;
            }
            cam.zoom = MathUtils.lerp(zoomLevels[lastZoomLevel], zoomLevels[zoomLevel], zoomProgress);
            cam.update();
        }
    }
}
