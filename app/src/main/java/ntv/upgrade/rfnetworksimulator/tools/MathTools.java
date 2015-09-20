package ntv.upgrade.rfnetworksimulator.tools;

/**
 * Created by Paulino on 9/20/2015.
 */
public class MathTools {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
