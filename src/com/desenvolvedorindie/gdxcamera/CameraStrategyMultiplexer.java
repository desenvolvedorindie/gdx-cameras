package com.desenvolvedorindie.gdxcamera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Array;

public class CameraStrategyMultiplexer implements ICameraStrategy {

    private Array<ICameraStrategy> strategies = new Array(4);

    public CameraStrategyMultiplexer(ICameraStrategy... strategies) {
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

    public void addProcessor(int index, ICameraStrategy strategy) {
        if (strategy == null) throw new NullPointerException("strategy cannot be null");
        strategies.insert(index, strategy);
    }

    public void removeProcessor(int index) {
        strategies.removeIndex(index);
    }

    public void addProcessor(ICameraStrategy strategy) {
        if (strategy == null) throw new NullPointerException("strategy cannot be null");
        strategies.add(strategy);
    }

    public void removeProcessor(ICameraStrategy strategy) {
        strategies.removeValue(strategy, true);
    }

    public int size() {
        return strategies.size;
    }

    public void clear() {
        strategies.clear();
    }

    public Array<ICameraStrategy> getStrategies() {
        return strategies;
    }

    public void setStrategies(Array<ICameraStrategy> strategies) {
        this.strategies = strategies;
    }
}
