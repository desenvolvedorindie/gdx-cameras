package com.desenvolvedorindie.gdxcameras.tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.desenvolvedorindie.gdxcamera.constraint.*;

import static com.badlogic.gdx.Input.Keys;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GDXCamerasTest extends Game {

    private CameraZoom cameraZoom;
    private CameraFollowConstraint cameraFollow;
    private CameraMidpointConstraint cameraMidpoint;
    private CameraConstraintBoundingBox cameraBoundingBox;
    private CameraConstraint cameraConstrains;
    private int zoomInterpolation = 0, positionInterpolation = 0;
    private Vector3 playerPosition = new Vector3();
    private boolean map[][] = new boolean[160][45];
    private OrthographicCamera camera;
    private Entity player = new Entity(), npc = new Entity();
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    private Array<Rectangle> tiles = new Array<Rectangle>();
    private ShapeRenderer debugRenderer;
    private Stage stage;
    private Label zoomLevelLabel, zoomInterpolationLabel, positionInterpolationLabel, boundingBoxLabel, modeLabel;
    private Utils.Mode mode = Utils.Mode.Follow;
    private Array<Vector3> points = new Array<>();

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
        SpriteBatch batch = new SpriteBatch();
        debugRenderer = new ShapeRenderer();

        // create an orthographic camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        for (int x = 0; x < getWidth(); x++)
            for (int y = 0; y < 3; y++)
                map[x][y] = true;

        // create the Entity we want to move around the world
        player.position.set(20, 6 * Utils.TILE_SIZE);
        npc.position.set((getWidth() - 50) * Utils.TILE_SIZE, 3 * Utils.TILE_SIZE);


        //Camera Constraints
        cameraZoom = new CameraZoom(Utils.ZOOM_LEVELS, 2, Utils.getInterpolation(Utils.INTERPOLATIONS[zoomInterpolation]));

        playerPosition.set(player.position.x, player.position.y, 0);
        cameraFollow = new CameraFollowConstraint(playerPosition, 2, Utils.getInterpolation(Utils.INTERPOLATIONS[positionInterpolation]));

        cameraBoundingBox = new CameraConstraintBoundingBox(new BoundingBox(new Vector3(40 * Utils.TILE_SIZE, 0, 0), new Vector3((getWidth() - 40) * Utils.TILE_SIZE, getHeight() * Utils.TILE_SIZE, 0)));

        cameraMidpoint = new CameraMidpointConstraint(true, 100);
        cameraMidpoint.setPoints(points);

        points.add(new Vector3(player.position.x, player.position.y, 0));
        points.add(new Vector3(npc.position.x + Entity.WIDTH / 2, npc.position.y + Entity.HEIGHT, 0));

        cameraConstrains = new CameraConstraintMultiplexer(cameraZoom, cameraFollow, cameraMidpoint, cameraBoundingBox);

        // Stage
        Skin skin = new Skin();

        skin.add("default", new Label.LabelStyle(new BitmapFont(), Color.BLACK));

        stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);

        zoomLevelLabel = new Label(String.format(Utils.ZOOM_LEVEL_TEXT, camera.zoom), skin, "default");
        zoomLevelLabel.setPosition(20, Gdx.graphics.getHeight() - 30);
        stage.addActor(zoomLevelLabel);

        zoomInterpolationLabel = new Label(String.format(Utils.ZOOM_INTERPOLATION_TEXT, Utils.INTERPOLATIONS[zoomInterpolation]), skin, "default");
        zoomInterpolationLabel.setPosition(20, Gdx.graphics.getHeight() - 60);
        stage.addActor(zoomInterpolationLabel);

        positionInterpolationLabel = new Label(String.format(Utils.POSITION_INTERPOLATION_TEXT, Utils.INTERPOLATIONS[positionInterpolation]), skin, "default");
        positionInterpolationLabel.setPosition(20, Gdx.graphics.getHeight() - 90);
        stage.addActor(positionInterpolationLabel);

        boundingBoxLabel = new Label(String.format(Utils.LIMIT_TEXT, Utils.getIsEnable(cameraFollow.isEnable())), skin, "default");
        boundingBoxLabel.setPosition(20, Gdx.graphics.getHeight() - 120);
        stage.addActor(boundingBoxLabel);

        modeLabel = new Label(String.format(Utils.MODE_TEXT, mode.toString()), skin, "default");
        modeLabel.setPosition(20, Gdx.graphics.getHeight()- 150);
        stage.addActor(modeLabel);
    }


    @Override
    public void render() {
        // clear the screen
        Gdx.gl.glClearColor(0.7f, 0.7f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // get the delta time
        float deltaTime = Gdx.graphics.getDeltaTime();

        // update the player
        updatePlayer(deltaTime);

        //update camera
        updateCamera(deltaTime);

        // render
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeType.Line);
        renderEntities();
        renderMap();
        cameraConstrains.debug(camera, debugRenderer, deltaTime);
        debugRenderer.end();
        stage.draw();
    }

    private void updateCamera(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Keys.Z))
            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
                cameraZoom.zoomIn();
            else
                cameraZoom.zoomOut();

        if (Gdx.input.isKeyJustPressed(Keys.M))
            if (mode == Utils.Mode.Follow)
                mode = Utils.Mode.MidPoint;
            else
                mode = Utils.Mode.Follow;

        if (Gdx.input.isKeyJustPressed(Keys.X))
            cameraZoom.setInterpolation(Utils.getInterpolation(Utils.INTERPOLATIONS[zoomInterpolation = ++zoomInterpolation % Utils.INTERPOLATIONS.length]));

        if (Gdx.input.isKeyJustPressed(Keys.C))
            cameraFollow.setInterpolation(Utils.getInterpolation(Utils.INTERPOLATIONS[positionInterpolation = ++positionInterpolation % Utils.INTERPOLATIONS.length]));

        if (Gdx.input.isKeyJustPressed(Keys.B))
            cameraBoundingBox.setEnable(!cameraBoundingBox.isEnable());

        if (mode == Utils.Mode.Follow) {
            cameraFollow.setEnable(true);
            cameraZoom.setEnable(true);
            cameraMidpoint.setEnable(false);
        } else {
            cameraMidpoint.setEnable(true);
            cameraFollow.setEnable(false);
            cameraZoom.setEnable(false);
        }

        playerPosition.set(player.position.x + Entity.WIDTH / 2, player.position.y + Entity.HEIGHT, 0);
        points.get(0).set(playerPosition);

        cameraConstrains.update(camera, deltaTime);

        zoomLevelLabel.setText(String.format(Utils.ZOOM_LEVEL_TEXT, Utils.ZOOM_LEVELS[cameraZoom.getZoomLevel()]));
        zoomInterpolationLabel.setText(String.format(Utils.ZOOM_INTERPOLATION_TEXT, Utils.INTERPOLATIONS[zoomInterpolation]));
        positionInterpolationLabel.setText(String.format(Utils.POSITION_INTERPOLATION_TEXT, Utils.INTERPOLATIONS[positionInterpolation]));
        boundingBoxLabel.setText(String.format(Utils.LIMIT_TEXT, Utils.getIsEnable(cameraBoundingBox.isEnable())));
        modeLabel.setText(String.format(Utils.MODE_TEXT, mode.toString()));
    }

    private void updatePlayer(float deltaTime) {
        // check input and apply to velocity
        if ((Gdx.input.isKeyPressed(Keys.SPACE)) && player.grounded) {
            player.velocity.y += Entity.JUMP_VELOCITY;
            player.grounded = false;
        }

        boolean moving = false;

        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
            player.velocity.x = -Entity.MAX_VELOCITY;
            moving = true;
        }

        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) {
            player.velocity.x = Entity.MAX_VELOCITY;
            moving = true;
        }

        if (!moving) {
            player.velocity.x = 0;
        }

        // apply gravity if we are falling
        player.velocity.add(0, Utils.GRAVITY * deltaTime);

        // multiply by delta time so we know how far we go
        // in this frame
        player.velocity.scl(deltaTime);

        // perform collision detection & response, on each axis, separately
        // if the player is moving right, check the tiles to the right of it's
        // right bounding box edge, otherwise check the ones to the left
        Rectangle rect = rectPool.obtain();
        rect.set(player.position.x, player.position.y, Entity.WIDTH, Entity.HEIGHT);
        float startX, startY, endX, endY;
        if (player.velocity.x > 0) {
            startX = endX = player.position.x + Entity.WIDTH + player.velocity.x;
        } else {
            startX = endX = player.position.x + player.velocity.x;
        }
        startY = player.position.y;
        endY = player.position.y + Entity.HEIGHT;
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
            startY = endY = player.position.y + Entity.HEIGHT + player.velocity.y;
        } else {
            startY = endY = player.position.y + player.velocity.y;
        }
        startX = player.position.x;
        endX = player.position.x + Entity.WIDTH;
        getTiles(startX, startY, endX, endY, tiles);
        rect.y += player.velocity.y;
        for (Rectangle tile : tiles) {
            if (rect.overlaps(tile)) {
                // we actually reset the player y-position here
                if (player.velocity.y > 0) {
                    player.position.y = tile.y - Entity.HEIGHT;
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

        if (player.position.x < 0) {
            player.position.x = 0;
        }

        if (player.position.x > getWidth() * Utils.TILE_SIZE - player.WIDTH) {
            player.position.x = getWidth() * Utils.TILE_SIZE - player.WIDTH;
        }
    }

    private void getTiles(float startX, float startY, float endX, float endY, Array<Rectangle> tiles) {
        getTiles((int) startX / Utils.TILE_SIZE, (int) startY / Utils.TILE_SIZE, (int) endX / Utils.TILE_SIZE, (int) endY / Utils.TILE_SIZE, tiles);
    }

    private void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (isValid(x, y) && map[x][y]) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x * Utils.TILE_SIZE, y * Utils.TILE_SIZE, Utils.TILE_SIZE, Utils.TILE_SIZE);
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

    private void renderEntities() {
        debugRenderer.setColor(Color.RED);
        player.render(debugRenderer);
        npc.render(debugRenderer);
    }

    private void renderMap() {
        debugRenderer.setColor(Color.BLACK);
        for (int x = 0; x < getWidth(); x++)
            for (int y = 0; y < getHeight(); y++)
                if (map[x][y])
                    debugRenderer.rect(x * Utils.TILE_SIZE, y * Utils.TILE_SIZE, Utils.TILE_SIZE, Utils.TILE_SIZE);
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
    }

}
