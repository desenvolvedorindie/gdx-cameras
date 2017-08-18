package com.desenvolvedorindie.gdxcameras.tests;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public final class Utils {

    public static final String[] INTERPOLATIONS = new String[]{
            "none",
            "linear",
            "smooth",
            "smooth2",
            "smoother",
            "pow2",
            "pow2In",
            "pow2Out",
            "pow2InInverse",
            "pow2OutInverse",
            "pow3",
            "pow3In",
            "pow3Out",
            "pow3InInverse",
            "pow3OutInverse",
            "pow4",
            "pow4In",
            "pow4Out",
            "pow5",
            "pow5In",
            "pow5Out",
            "sine",
            "sineIn",
            "sineOut",
            "exp10",
            "exp10In",
            "exp10Out",
            "exp5",
            "exp5In",
            "exp5Out",
            "circle",
            "circleIn",
            "circleOut",
            "elastic",
            "elasticIn",
            "elasticOut",
            "swing",
            "swingIn",
            "swingOut",
            "bounce",
            "bounceIn",
            "bounceOut",
    };

    public static String getIsEnable(boolean enable) {
        return enable ? "Enable" : "Disable";
    }

    public static Interpolation getInterpolation(String name) {
        if (name == null)
            return null;
        try {
            Object obj = ClassReflection.getField(Interpolation.class, name).get(null);
            if (obj instanceof Interpolation)
                return (Interpolation) obj;
        } catch (ReflectionException e) {
        }
        return null;
    }

    public enum Mode {
        Follow,
        MidPoint,
        //Focus,
    }

    public static final int TILE_SIZE = 16;

    public static final float GRAVITY = -576;
    public static final float[] ZOOM_LEVELS = new float[]{
            6 / 16f,
            1f,
            2,
            3,
    };
    public static final String ZOOM_LEVEL_TEXT = "Zoom Interpolation (Key: z): %.2f";
    public static final String ZOOM_INTERPOLATION_TEXT = "Zoom Interpolation (Key: x): %s";
    public static final String POSITION_INTERPOLATION_TEXT = "Follow Interpolation (Key: c): %s";
    public static final String LIMIT_TEXT = "Bounding Box (Key: b): %s";
    public static final String MODE_TEXT = "Mode (Key: m): %s";

}
