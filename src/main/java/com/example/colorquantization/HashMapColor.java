package com.example.colorquantization;

import java.awt.*;
import java.util.HashMap;

import static com.example.colorquantization.ColorPalette.rgbToHsv;

public class HashMapColor extends HashMap<Color, Integer> {

    @Override
    public boolean containsKey(Object o) {
        for (Color color : this.keySet()) {
            if (isSimilar(color, (Color) o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer get(Object key) {
        for (Color color : this.keySet()) {
            if (isSimilar(color, (Color) key)) {
                return super.get(color);
            }
        }
        return null;
    }

    static boolean isSimilar(Color co1, Color co2) {
        float[] c1 = rgbToHsv(co1);
        float[] c2 = rgbToHsv(co2);
        return (Math.abs(c1[0] - c2[0]) <= 50) && (Math.abs(c1[1] - c2[1]) <= 50) && (Math.abs(c1[2] - c2[2]) <= 50);
    }

}
