package team2485.smartdashboard.extension.util;

/**
 * Stores an HSV min/max configuration.
 * @author Bryce Matsumori
 */
public class HSVConfig {
    public int
            hMin = 0, hMax = 180,
            sMin = 0, sMax = 255,
            vMin = 0, vMax = 255;

    /**
     * Creates a new Axis camera configuration with the default values.
     */
    public HSVConfig() {
    }

    /**
     * Creates a new Axis camera configuration with the specified values.
     * @param hMin hue min
     * @param hMax hue max
     * @param sMin sat min
     * @param sMax sat max
     * @param vMin vib min
     * @param vMax vib max
     */
    public HSVConfig(int hMin, int hMax, int sMin, int sMax, int vMin, int vMax) {
        this.hMin = hMin;
        this.hMax = hMax;
        this.sMin = sMin;
        this.sMax = sMax;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    /**
     * Creates A string in the format "hMin,hMax,sMin,sMax,vMin,vMax".
     * @return The string.
     */
    @Override
    public String toString() {
        return String.format("%d,%d,%d,%d,%d,%d", hMin, hMax, sMin, sMax, vMin, vMax);
    }

    /**
     * Parses a saved String to a config. Returns null if string is invalid.
     * @param s The String to parse.
     * @return The config.
     */
    public static HSVConfig parse(String s) {
        String[] split = s.split(",");
        if (split.length != 6) return null;
        HSVConfig config = new HSVConfig();
        try {
            config.hMin = Integer.parseInt(split[0]);
            config.hMax = Integer.parseInt(split[1]);
            config.sMin = Integer.parseInt(split[2]);
            config.sMax = Integer.parseInt(split[3]);
            config.vMin = Integer.parseInt(split[4]);
            config.vMax = Integer.parseInt(split[5]);
            return config;
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + this.hMin;
        hash = 13 * hash + this.hMax;
        hash = 13 * hash + this.sMin;
        hash = 13 * hash + this.sMax;
        hash = 13 * hash + this.vMin;
        hash = 13 * hash + this.vMax;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        final HSVConfig other = (HSVConfig) obj;

        return this.hMin == other.hMin && this.hMax == other.hMax &&
               this.sMin == other.sMin && this.sMax == other.sMax &&
               this.vMin == other.vMin && this.vMax == other.vMax;
    }
}
