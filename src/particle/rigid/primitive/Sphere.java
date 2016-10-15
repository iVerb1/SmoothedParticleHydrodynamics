package particle.rigid.primitive;

import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import particle.State;
import particle.rigid.RigidBody;
import util.Util;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by iVerb on 17-6-2015.
 */
public class Sphere extends RigidBody {

    public double radius = 0.5;

    public Sphere(Vector3f initialPosition, double mass) {
        super(initialPosition, mass);
    }

    public Sphere(Vector3f initialPosition) {
        super(initialPosition);
    }

    public Sphere(Vector2f initialPosition, double mass) {
        super(initialPosition, mass);
    }

    public Sphere(Vector2f initialPosition) {
        super(initialPosition);
    }

    @Override
    public boolean checkPenetration(State state, RealVector point) {
        RealVector position = getPosition(state);
        RealVector x = point.subtract(position);
        return x.dotProduct(x) - Math.pow(radius, 2) < 0;
    }

    @Override
    public RealVector getContactPoint(State state, RealVector point) {
        RealVector position = getPosition(state);
        RealVector x = Util.normalise(point.subtract(position));
        return position.add(x.mapMultiply(radius));
    }

    @Override
    public RealVector getNormalInwards(State state, RealVector penetrationPoint) {
        RealVector position = getPosition(state);
        return Util.normalise(position.subtract(penetrationPoint));
    }

    @Override
    public double getPenetrationDepth(State state, RealVector penetrationPoint) {
        RealVector position = getPosition(state);
        return radius - Util.length(position.subtract(penetrationPoint));
    }

    @Override
    public Set<RealVector> getPenetrationPoints(State state, RigidBody rigidBody) {
        Set<RealVector> penetrationPoints = new HashSet<RealVector>();
        RealVector position = getPosition(state);

        if (rigidBody instanceof Sphere) {
            Sphere sphere = (Sphere)rigidBody;
            RealVector spherePosition = sphere.getPosition(state);
            RealVector point = spherePosition.add(position.subtract(spherePosition).mapDivide(2));
            if (checkPenetration(state, point))
                penetrationPoints.add(point);
        }
        else {
            return null;
        }

        return penetrationPoints;
    }


    @Override
    protected void draw2D(State state) {
        glPushMatrix();
        glColor3f(0f, 1f, 0f);
        glTranslated(getX(state), getY(state), 0);
        new Disk().draw(0f, (float)radius, 15, 1);
        glPopMatrix();
    }

    @Override
    protected void draw3D(State state) {

    }
}
