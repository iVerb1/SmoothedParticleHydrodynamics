package particle.rigid.primitive;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import particle.State;
import particle.rigid.RigidBody;
import rendering.Camera3D;
import rendering.shade3D.Material;
import rendering.shade3D.ShaderUtils;
import util.Util;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;


/**
 * Created by iVerb on 18-6-2015.

/**
 * Created by s113958 on 26-5-2015.
 */
public class Boundary extends RigidBody {

    private RealVector markerPoint;
    private RealVector normal;

    private RealVector spanner1;
    private RealVector spanner2;


    public Boundary(RealVector markerPoint, RealVector spanner1, RealVector spanner2) {
        super(new Vector3f(0f, 0f, 0f));
        this.markerPoint = markerPoint;
        this.spanner1 = spanner1;
        this.spanner2 = spanner2;
        staticBody = true;

        normal = Util.normalise(Util.crossProduct(spanner1, spanner2));
    }

    public Boundary(Vector2f markerPoint, Vector2f spanner) {
        this(new ArrayRealVector(new double[]{markerPoint.x, markerPoint.y, 0}), new ArrayRealVector(new double[]{spanner.x, spanner.y, 0}), new ArrayRealVector(new double[]{0,0,1}));
    }

    public RealVector getNormal() {
        return normal;
    }

    public RealVector getMarkerPoint() {
        return markerPoint;
    }

    @Override
    public boolean checkPenetration(State state, RealVector point) {
        return point.subtract(markerPoint).dotProduct(normal) < 0;
    }

    @Override
    public RealVector getContactPoint(State state, RealVector point) {
        double d = markerPoint.subtract(point).dotProduct(normal);
        return point.add(normal.mapMultiply(d));
    }

    @Override
    public RealVector getNormalInwards(State state, RealVector penetrationPoint) {
        return normal.mapMultiply(-1);
    }

    @Override
    public double getPenetrationDepth(State state, RealVector penetrationPoint) {
        RealVector contactPoint = getContactPoint(state, penetrationPoint);
        return Util.length(contactPoint.subtract(penetrationPoint));
    }

    @Override
    public Set<RealVector> getPenetrationPoints(State state, RigidBody rigidBody) {
        HashSet<RealVector> penetrationPoints = new HashSet<RealVector>();

        if (rigidBody instanceof Sphere) {
            Sphere sphere = (Sphere)rigidBody;
            RealVector spherePosition = sphere.getPosition(state);
            RealVector point = spherePosition.add(normal.mapMultiply(-sphere.radius));
            if (checkPenetration(state, point)) {
                penetrationPoints.add(point);
            }
        }
        else if (!(rigidBody instanceof Boundary)) {
            return null;
        }

        return penetrationPoints;
    }


    @Override
    protected void draw2D(State state) {
        glLineWidth(4.5f);
        glColor3f(1f, 1f, 1f);
        glBegin(GL_LINES);
        glVertex2d(markerPoint.getEntry(0) + 1000 * spanner1.getEntry(0), markerPoint.getEntry(1) + 1000 * spanner1.getEntry(1));
        glVertex2d(markerPoint.getEntry(0) - 1000 * spanner1.getEntry(0), markerPoint.getEntry(1) - 1000 * spanner1.getEntry(1));
        glEnd();
    }

    @Override
    protected void draw3D(State state) {
        Camera3D.prepareShading();
        Material material = new Material();
        ShaderUtils.initializeShader(true, material);

        RealVector p1 = new ArrayRealVector(new double[] {spanner1.getEntry(0), spanner1.getEntry(1), spanner1.getEntry(2)});
        RealVector p2 = new ArrayRealVector(new double[] {spanner2.getEntry(0), spanner2.getEntry(1), spanner2.getEntry(2)});
        p1 = Util.normalise(p1).mapMultiply(1000);
        p2 = Util.normalise(p2).mapMultiply(1000);

        glBegin(GL_QUADS);
        glNormal3d(normal.getEntry(0), normal.getEntry(1), normal.getEntry(2));
        glVertex3d(markerPoint.getEntry(0) + p1.getEntry(0), markerPoint.getEntry(1) + p1.getEntry(1), markerPoint.getEntry(2) + p1.getEntry(2));
        glVertex3d(markerPoint.getEntry(0) + p2.getEntry(0), markerPoint.getEntry(1) + p2.getEntry(1), markerPoint.getEntry(2) + p2.getEntry(2));
        glVertex3d(markerPoint.getEntry(0) - p1.getEntry(0), markerPoint.getEntry(1) - p1.getEntry(1), markerPoint.getEntry(2) - p1.getEntry(2));
        glVertex3d(markerPoint.getEntry(0) - p2.getEntry(0), markerPoint.getEntry(1) - p2.getEntry(1), markerPoint.getEntry(2) - p2.getEntry(2));
        glEnd();

        Camera3D.finalizeShading();
    }

}
