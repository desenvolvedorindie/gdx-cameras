package com.desenvolvedorindie.gdxcameras.tests;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Entity {
    public final static float WIDTH = 16;
    public final static float HEIGHT = 20;
    public final static float MAX_VELOCITY = 400f;
    public final static float JUMP_VELOCITY = 500f;
    final Vector2 position = new Vector2();
    final Vector2 velocity = new Vector2();
    boolean grounded = false;


    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(position.x, position.y, Entity.WIDTH, Entity.HEIGHT);
    }

}