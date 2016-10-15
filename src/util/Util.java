package util;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.*;

import java.nio.FloatBuffer;

/**
 * Created by iVerb on 18-3-2015.
 */
public class Util {

    public static float[] asArray3(Vector3f v) {
        return new float[] {v.x, v.y, v.z};
    }

    public static float[] asArray4(Vector3f v) {
        return new float[] {v.x, v.y, v.z, 1f};
    }

    public static float[] asArray(Vector4f v) {
        return new float[] {v.x, v.y, v.z, v.w};
    }

    public static Vector toVector(RealVector v) {
        if (v.getDimension() == 2)
            return new Vector2f((float)v.getEntry(0), (float)v.getEntry(1));
        else if (v.getDimension() == 3)
            return new Vector3f((float)v.getEntry(0), (float)v.getEntry(1), (float)v.getEntry(2));
        else
            throw new RuntimeException("Unsupported number of dimensions!");
    }

    public static RealVector toRealVector(Vector3f v) {
        return new ArrayRealVector(new double[] {v.x, v.y, v.z});
    }

    public static FloatBuffer asFloatBuffer(float... values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values);
        buffer.flip();
        return buffer;
    }

    public static void rotateVector3f(Vector3f v, Matrix4f m, Vector3f dest) {
        float x = (m.m00 * v.x) + (m.m10 * v.y) + (m.m20 * v.z);
        float y = (m.m01 * v.x) + (m.m11 * v.y) + (m.m21 * v.z);
        float z = (m.m02 * v.x) + (m.m12 * v.y) + (m.m22 * v.z);

        dest.set(x, y, z);
    }

    public static Vector3f rayPlaneIntersection(Vector3f rayOrigin, Vector3f rayDirection, Vector3f pointOnPlane, Vector3f spanner1, Vector3f spanner2) {
        Vector3f w = new Vector3f();
        Vector3f.sub(rayOrigin, pointOnPlane, w);

        Vector3f n = new Vector3f();
        Vector3f.cross(spanner1, spanner2, n);

        float s = -(Vector3f.dot(n, w))/(Vector3f.dot(n,rayDirection));

        return new Vector3f(rayOrigin.x + s*rayDirection.x, rayOrigin.y + s*rayDirection.y, rayOrigin.z + s*rayDirection.z);
    }

    public static Vector2f rotateCounterClockWise(Vector2f v, double degrees) {
        double ca = Math.cos(degrees * Math.PI/180);
        double sa = Math.sin(degrees * Math.PI/180);
        return new Vector2f((float)(ca*v.x - sa*v.y), (float)(sa*v.x + ca*v.y));
    }

    public static Vector2f rotateClockWise(Vector2f v, double degrees) {
        return rotateCounterClockWise(v, (360 - degrees) % 360);
    }

    public static RealVector rotateCounterClockWise(RealVector v, double degrees) {
        double ca = Math.cos(degrees * Math.PI/180);
        double sa = Math.sin(degrees * Math.PI/180);
        return new ArrayRealVector(new double[] {(ca * v.getEntry(0) - sa * v.getEntry(1)), (sa * v.getEntry(0) + ca * v.getEntry(1)), 0});
    }

    public static RealVector rotateClockWise(RealVector v, double degrees) {
        return rotateCounterClockWise(v, (360 - degrees) % 360);
    }

    public static RealVector crossProduct(RealVector v1, RealVector v2) {
        return new ArrayRealVector(new double[] {
                v1.getEntry(1)*v2.getEntry(2) - v1.getEntry(2)*v2.getEntry(1),
                v1.getEntry(2)*v2.getEntry(0) - v1.getEntry(0)*v2.getEntry(2),
                v1.getEntry(0)*v2.getEntry(1) - v1.getEntry(1)*v2.getEntry(0),
        });
    }

    public static double length(RealVector v) {
        return Math.sqrt(v.dotProduct(v));
    }

    public static RealVector normalise(RealVector v) {
        return v.mapDivide(length(v));
    }


}
