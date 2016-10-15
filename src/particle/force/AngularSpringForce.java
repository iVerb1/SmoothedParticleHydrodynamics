package particle.force;

import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsObject;
import particle.State;
import util.Util;

/**
 * Created by iVerb on 26-5-2015.
 */
public class AngularSpringForce extends Force {

    private PhysicsObject p1;
    private PhysicsObject middle;
    private PhysicsObject p2;

    private double angle;
    private double springConstant;

    public AngularSpringForce(PhysicsObject p1, PhysicsObject middle, PhysicsObject p2, double angle, double springConstant) {
        this.middle = middle;

        if (angle % 360 > 180) {
            this.p1 = p2;
            this.p2 = p1;
            this.angle = 360 - (angle % 360);
        }
        else {
            this.p1 = p1;
            this.p2 = p2;
            this.angle = (angle % 360);
        }

        this.springConstant = springConstant;
    }

    @Override
    public void apply(State state) {
        RealVector l = p1.getPosition(state).subtract(p2.getPosition(state));
        double lLength = Math.sqrt(l.dotProduct(l));

        if (lLength != 0) {
            RealVector a = p1.getPosition(state).subtract(middle.getPosition(state));
            RealVector b = p2.getPosition(state).subtract(middle.getPosition(state));
            double aLength = Math.sqrt(a.dotProduct(a));
            double bLength = Math.sqrt(a.dotProduct(a));
            a.mapDivideToSelf(aLength);
            b.mapDivideToSelf(bLength);

            //double dot = a.getEntry(0)*b.getEntry(0) + a.getEntry(1)*b.getEntry(1) + a.getEntry(2)*b.getEntry(2);
            //double lenSq1 = a.getEntry(0)*a.getEntry(0) + a.getEntry(1)*a.getEntry(1) + a.getEntry(2)*a.getEntry(2);
            //double lenSq2 = b.getEntry(0)*b.getEntry(0) + b.getEntry(1)*b.getEntry(1) + b.getEntry(2)*b.getEntry(2);
            //double vectorAngle = (360 + (180 / Math.PI * Math.acos(dot / Math.sqrt(lenSq1 * lenSq2)))) % 360;
            double vectorAngle = (360 - (180/Math.PI * Math.atan2(a.getEntry(0)*b.getEntry(1) - b.getEntry(0)*a.getEntry(1), a.getEntry(0)*b.getEntry(0) + a.getEntry(1)*b.getEntry(1)))) % 360;

            RealVector forceDirection = Util.rotateCounterClockWise(l.mapDivide(lLength), 90);
            double forceMultiplier = springConstant * (vectorAngle - angle);

            RealVector force =  forceDirection.mapMultiply(forceMultiplier);

            middle.addForce(state, force);
            p1.addForce(state, force.mapMultiply(-0.5));
            p2.addForce(state, force.mapMultiply(-0.5));
        }
    }

    @Override
    protected void draw2D(State state) {

    }

    @Override
    protected void draw3D(State state) {

    }

}
