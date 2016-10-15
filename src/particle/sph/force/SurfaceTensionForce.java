package particle.sph.force;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsSystem;
import particle.State;
import particle.force.Force;
import particle.sph.HydroParticle;
import particle.sph.smoothing.SmoothingKernel;
import util.Util;

/**
 * Created by iVerb on 13-6-2015.
 */
public class SurfaceTensionForce extends Force {

    public PhysicsSystem system;
    public SmoothingKernel kernel;

    public double threshold = 0.3;
    public double tensionCoefficient = 0.0728;

    public SurfaceTensionForce(PhysicsSystem system, SmoothingKernel kernel) {
        this.system = system;
        this.kernel = kernel;
    }

    public SurfaceTensionForce(PhysicsSystem system, SmoothingKernel kernel, double threshold, double tensionCoefficient) {
        this.system = system;
        this.kernel = kernel;
        this.threshold = threshold;
        this.tensionCoefficient = tensionCoefficient;
    }

    @Override
    public void apply(State state) {
        for (HydroParticle pi : system.objects.hydro) {
            double ci = 0;
            RealVector ni = new ArrayRealVector(3, 0);

            for (HydroParticle pj : system.objects.hydro) {
                double x = pj.getMass() / pj.getDensity(state);
                RealVector r = pi.getPosition(state).subtract(pj.getPosition(state));
                ci += (x * kernel.evalLaplacian(r));
                ni = ni.add(kernel.evalGradient(r).mapMultiply(x));
            }

            double niLength = Util.length(ni);
            if (niLength >= threshold) {
                pi.addForce(state, ni.mapDivide(niLength).mapMultiply(ci).mapMultiply(-tensionCoefficient));
            }
        }
    }

    @Override
    protected void draw2D(State state) {

    }

    @Override
    protected void draw3D(State state) {

    }

}
