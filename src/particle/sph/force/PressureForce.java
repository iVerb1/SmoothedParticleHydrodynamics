package particle.sph.force;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsSystem;
import particle.State;
import particle.force.Force;
import particle.sph.HydroParticle;
import particle.sph.smoothing.SmoothingKernel;

/**
 * Created by iVerb on 13-6-2015.
 */
public class PressureForce extends Force {

    public PhysicsSystem system;
    public SmoothingKernel kernel;

    public double gasConstant = 25;


    public PressureForce(PhysicsSystem system, SmoothingKernel kernel) {
        this.system = system;
        this.kernel = kernel;
    }

    public PressureForce(PhysicsSystem system, SmoothingKernel kernel, double gasConstant) {
        this.system = system;
        this.kernel = kernel;
        this.gasConstant = gasConstant;
    }

    @Override
    public void apply(State state) {
        for (HydroParticle pi : system.objects.hydro) {
            RealVector sum = new ArrayRealVector(3, 0);
            for (HydroParticle pj : system.objects.hydro) {
                if (!pi.equals(pj)) {
                    double pressure_i = gasConstant * (pi.getDensity(state) - system.REST_DENSITY);
                    double pressure_j = gasConstant * (pj.getDensity(state) - system.REST_DENSITY);
                    //double x = pj.getMass() * ((pressure_i + pressure_j) / (2 * pj.getDensity(state)));
                    double x = pj.getMass() * ((pressure_i / Math.pow(pi.getDensity(state), 2)) + (pressure_j / Math.pow(pj.getDensity(state), 2)));
                    sum = sum.subtract(kernel.evalGradient(pi.getPosition(state).subtract(pj.getPosition(state))).mapMultiply(x));
                }
            }
            //pi.addForce(state, sum);
            pi.addForce(state, sum.mapMultiply(pi.getDensity(state)));
        }
    }

    @Override
    protected void draw2D(State state) {

    }

    @Override
    protected void draw3D(State state) {

    }
}
