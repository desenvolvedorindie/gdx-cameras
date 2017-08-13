package com.desenvolvedorindie.gdxcamera.constraint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class CameraConstraintMultiplexer implements CameraConstraint {

    private Array<CameraConstraint> strategies = new Array(4);

    public CameraConstraintMultiplexer(CameraConstraint... strategies) {
        for (int i = 0; i < strategies.length; i++) {
            this.strategies.add(strategies[i]);
        }
    }

    @Override
    public void update(Camera camera, float delta) {
        for (int i = 0; i < strategies.size; i++) {
            this.strategies.get(i).update(camera, delta);
        }
    }

    @Override
    public void debug(Camera camera, ShapeRenderer shapeRenderer, float delta) {
        for (int i = 0; i < strategies.size; i++) {
            this.strategies.get(i).debug(camera, shapeRenderer, delta);
        }
    }

    public void addProcessor(int index, CameraConstraint strategy) {
        if (strategy == null) throw new NullPointerException("strategy cannot be null");
        strategies.insert(index, strategy);
    }

    public void removeProcessor(int index) {
        strategies.removeIndex(index);
    }

    public void addProcessor(CameraConstraint strategy) {
        if (strategy == null) throw new NullPointerException("strategy cannot be null");
        strategies.add(strategy);
    }

    public void removeProcessor(CameraConstraint strategy) {
        strategies.removeValue(strategy, true);
    }

    public int size() {
        return strategies.size;
    }

    public void clear() {
        strategies.clear();
    }

    public Array<CameraConstraint> getStrategies() {
        return strategies;
    }

    public void setStrategies(Array<CameraConstraint> strategies) {
        this.strategies = strategies;
    }
}
