package com.desenvolvedorindie.gdxcameras.tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.desenvolvedorindie.gdxcamera.constraint.*;

import static com.badlogic.gdx.Input.Keys;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GDXCamerasTest extends Game {

    public static final int TILE_SIZE = 16;

    private static final float GRAVITY = -576;
    private static final float[] ZOOM_LEVELS = new float[]{
            6 / 16f,
            1f,
            2,
            3,
    };
    private CameraConstraint cameraConstrains;
    private CameraZoom cameraZoom;
    private CameraFollowConstraint cameraFollow;
    private CameraConstraintBoundingBox cameraBoundingBox;
    private float zoomDuration = 2;
    private Interpolation interpolation = Interpolation.smooth;
    private Vector3 playerPosition = new Vector3();

    private boolean map[][] = new boolean[80][45];

    private OrthographicCamera camera;
    private Player player;
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    private Array<Rectangle> tiles = new Array<Rectangle>();
    private ShapeRenderer debugRenderer;

    public static void main(String[] argv) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
        config.resizable = false;
        config.vSyncEnabled = true;
        config.title = GDXCamerasTest.class.getSimpleName();
        new LwjglApplication(new GDXCamerasTest(), config);
    }

    @Override
    public void create() {
        Player.WIDTH = 16;
        Player.HEIGHT = 20;

        // create an orthographic camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (y < 3) {
                    map[x][y] = true;
                }
            }
        }

        // create the Player we want to move around the world
        player = new Player();
        player.position.set(20, 6 * TILE_SIZE);

        debugRenderer = new ShapeRenderer();

        //Camera Constraints
        cameraZoom = new CameraZoom(ZOOM_LEVELS, zoomDuration, interpolation);
        playerPosition.set(player.position.x, player.position.y, 0);
        cameraFollow = new CameraFollowConstraint(playerPosition);
        cameraBoundingBox = new CameraConstraintBoundingBox(new BoundingBox());

        cameraConstrains = new CameraConstraintMultiplexer(cameraZoom, cameraFollow, cameraBoundingBox);
    }

    @Override
    public void render() {
        // clear the screen
        Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // get the delta time
        float deltaTime = Gdx.graphics.getDeltaTime();

        // update the player (process input, collision detection, position update)
        updatePlayer(deltaTime);

        // let the camera follow the player, x-axis only
        playerPosition.set(player.position.x, player.position.y, 0);
        camera.position.x = player.position.x;
        camera.position.y = player.position.y;
        camera.update();


        // render the player
        renderPlayer();

        // render the map
        renderMap();
    }

    private void updatePlayer(float deltaTime) {
        // check input and apply to velocity & state
        if ((Gdx.input.isKeyPressed(Keys.SPACE) || isTouched(0.5f, 1)) && player.grounded) {
            player.velocity.y += Player.JUMP_VELOCITY;
            player.grounded = false;
        }

        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A) || isTouched(0, 0.25f)) {
            player.velocity.x = -Player.MAX_VELOCITY;
        }

        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D) || isTouched(0.25f, 0.5f)) {
            player.velocity.x = Player.MAX_VELOCITY;
        }

        // apply gravity if we are falling
        player.velocity.add(0, GRAVITY * deltaTime);

        // clamp the velocity to the maximum, x-axis only
        player.velocity.x = MathUtils.clamp(player.velocity.x, -Player.MAX_VELOCITY, Player.MAX_VELOCITY);

        // If the velocity is < 1, set it to 0 and set state to Standing
        if (Math.abs(player.velocity.x) < 1) {
            player.velocity.x = 0;
        }

        // multiply by delta time so we know how far we go
        // in this frame
        player.velocity.scl(deltaTime);

        // perform collision detection & response, on each axis, separately
        // if the player is moving right, check the tiles to the right of it's
        // right bounding box edge, otherwise check the ones to the left
        Rectangle rect = rectPool.obtain();
        rect.set(player.position.x, player.position.y, Player.WIDTH, Player.HEIGHT);
        float startX, startY, endX, endY;
        if (player.velocity.x > 0) {
            startX = endX = player.position.x + Player.WIDTH + player.velocity.x;
        } else {
            startX = endX = player.position.x + player.velocity.x;
        }
        startY = player.position.y;
        endY = player.position.y + Player.HEIGHT;
        getTiles(startX, startY, endX, endY, tiles);
        rect.x += player.velocity.x;
        for (Rectangle tile : tiles) {
            if (rect.overlaps(tile)) {
                player.velocity.x = 0;
                break;
            }
        }
        rect.x = player.position.x;

        // if the player is moving upwards, check the tiles to the top of its
        // top bounding box edge, otherwise check the ones to the bottom
        if (player.velocity.y > 0) {
            startY = endY = player.position.y + Player.HEIGHT + player.velocity.y;
        } else {
            startY = endY = player.position.y + player.velocity.y;
        }
        startX = player.position.x;
        endX = player.position.x + Player.WIDTH;
        getTiles(startX, startY, endX, endY, tiles);
        rect.y += player.velocity.y;
        for (Rectangle tile : tiles) {
            if (rect.overlaps(tile)) {
                // we actually reset the player y-position here
                if (player.velocity.y > 0) {
                    player.position.y = tile.y - Player.HEIGHT;
                } else {
                    player.position.y = tile.y + tile.height;
                    // if we hit the ground, mark us as grounded so we can jump
                    player.grounded = true;
                }
                player.velocity.y = 0;
                break;
            }
        }
        rectPool.free(rect);

        // unscale the velocity by the inverse delta time and set
        // the latest position
        player.position.add(player.velocity);
        player.velocity.scl(1 / deltaTime);

        // Apply damping to the velocity on the x-axis so we don't
        // walk infinitely once a key was pressed
        player.velocity.x *= Player.DAMPING;
    }

    private boolean isTouched(float startX, float endX) {
        // Check for touch inputs between startX and endX
        // startX/endX are given between 0 (left edge of the screen) and 1 (right edge of the screen)
        for (int i = 0; i < 2; i++) {
            float x = Gdx.input.getX(i) / (float) Gdx.graphics.getWidth();
            if (Gdx.input.isTouched(i) && (x >= startX && x <= endX)) {
                return true;
            }
        }
        return false;
    }

    private void getTiles(float startX, float startY, float endX, float endY, Array<Rectangle> tiles) {
        getTiles((int) startX / TILE_SIZE, (int) startY / TILE_SIZE, (int) endX / TILE_SIZE, (int) endY / TILE_SIZE, tiles);
    }

    private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (isValid(x, y) && map[x][y]) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    tiles.add(rect);
                }
            }
        }
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    public int getWidth() {
        return map.length;
    }

    public int getHeight() {
        return map[0].length;
    }

    private void renderPlayer() {
        debugRenderer.begin(ShapeType.Line);

        debugRenderer.setColor(Color.RED);

        debugRenderer.rect(player.position.x, player.position.y, Player.WIDTH, Player.HEIGHT);

        debugRenderer.end();
    }

    private void renderMap() {
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeType.Line);

        debugRenderer.setColor(Color.BLACK);

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (map[x][y]) {
                    debugRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        debugRenderer.end();
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
    }

    static class Player {
        static float WIDTH;
        static float HEIGHT;
        static float MAX_VELOCITY = 100f;
        static float JUMP_VELOCITY = 300f;
        static float DAMPING = 0.87f;
        final Vector2 position = new Vector2();
        final Vector2 velocity = new Vector2();
        boolean grounded = false;
    }

}