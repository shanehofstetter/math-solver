package ch.shanehofstetter.calculator;

import java.util.HashMap;
import java.util.Map;

public final class Constants {

    public static final double LIGHTSPEED = 299792458;
    public static Map<Character, Double> constants;
    public static Map<String, Double> specialConstants;

    private Constants() {
    }

    public static void init() {
        constants = new HashMap<>();
        constants.put('c', LIGHTSPEED);
        constants.put('π', Math.PI);
        constants.put('e', Math.E);

        specialConstants = new HashMap<>();
        specialConstants.put("µ0", 0.0000012566370614359);
        specialConstants.put("ε0", 8.854187817E-12);
    }
}
