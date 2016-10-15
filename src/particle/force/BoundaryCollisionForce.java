package particle.force;

import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsObject;
import particle.PhysicsSystem;
import particle.State;
import particle.rigid.primitive.Boundary;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by s113958 on 26-5-2015.
 */
public class BoundaryCollisionForce extends Force {

    private Collection<Boundary> boundaries;
    private PhysicsSystem system;
    private double response;
    private double epsilon;

    public BoundaryCollisionForce(PhysicsSystem system, double epsilon, double response) {
        this.boundaries = new HashSet<Boundary>();
        this.system = system;
        this.epsilon = epsilon;
        this.response = response;
    }

    public void addPlanes(Collection<Boundary> boundaries) {
        this.boundaries.addAll(boundaries);
    }

    public void addPlanes(Boundary... boundaries) {
        Collections.addAll(this.boundaries, boundaries);
    }

    @Override
    public void apply(State state) {
        for (PhysicsObject p : system.objects) {
            RealVector particleVelocity = p.getVelocity(state);

            for (Boundary boundary : boundaries) {
                RealVector particlePosition = p.getPosition(state);

                RealVector particleRelativePosition = particlePosition.subtract(boundary.getMarkerPoint());

                double distance = particleRelativePosition.dotProduct(boundary.getNormal());

                if (distance < this.epsilon) {

                    double dot = boundary.getNormal().dotProduct(particleVelocity);
                    RealVector normalVelocity = boundary.getNormal().mapMultiply(dot);

                    RealVector force = p.getForce(state);

                    dot = boundary.getNormal().dotProduct(force);
                    RealVector normalForce = boundary.getNormal().mapMultiply(dot);

                    //p.addForce(state, particleVelocity.subtract(normalVelocity).mapMultiply(-plane.getFriction()*-dot));

                    if (boundary.getNormal().dotProduct(normalForce) <= 0) {
                        p.addForce(state, normalForce.mapMultiply(-1));
                    }

                    if (boundary.getNormal().dotProduct(normalVelocity) <= 0) {
                        RealVector f = normalVelocity.mapMultiply(-this.response*p.getDensity(state)/system.getTimeStep());
                        if (!f.isNaN() && !f.isInfinite()) {
                            p.addForce(state, f);
                        }
                    }
                }
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
