package particle.sph.smoothing;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsSystem;
import util.Util;

/**
 * Created by iVerb on 13-6-2015.
 */
public class DefaultSmoothingKernel extends SmoothingKernel {

    public DefaultSmoothingKernel(PhysicsSystem system) {
        super(system);
    }
    @Override
    public double eval(RealVector v) {
        double r = Util.length(v);
        double h = system.CORE_RADIUS;

        if (r >= 0 && r <= h) {
            return (315.0 / (64*Math.PI*Math.pow(h, 9))) * Math.pow(Math.pow(h, 2) - Math.pow(r, 2), 3);
        }
        else {
            return 0;
        }
    }

    @Override
    public RealVector evalGradient(RealVector v) {
        double r = Util.length(v);
        double h = system.CORE_RADIUS;

        if (r >= 0 && r <= h) {
            return v.mapMultiply(-(945.0 / (32*Math.PI*Math.pow(h, 9))) * Math.pow(Math.pow(h, 2) - Math.pow(r, 2), 2));
        }
        else {
            return new ArrayRealVector(3, 0);
        }
    }

    @Override
    public double evalLaplacian(RealVector v) {
        double r = Util.length(v);
        double h = system.CORE_RADIUS;

        if (r >= 0 && r <= h) {
            return -(945.0 / (32*Math.PI*Math.pow(h, 9))) * ((Math.pow(h, 2) - Math.pow(r, 2)) * ((3*Math.pow(h, 2) - 7*Math.pow(r, 2))));
        }
        else {
            return 0;
        }
    }
}
