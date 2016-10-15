package particle;

import javafx.util.Pair;
import org.lwjgl.util.vector.Vector2f;
import particle.constraint.*;
import particle.force.AngularSpringForce;
import particle.force.DampedSpringForce;
import particle.force.Force;
import particle.interaction.HydroParticlesSpawner;
import particle.interaction.RigidBodySpawner;
import particle.rigid.primitive.Boundary;
import particle.rigid.RigidBodyForce;
import particle.solve.EulerSolver;
import particle.solve.MidPointSolver;
import particle.solve.RungeKutta4Solver;
import particle.sph.force.PressureForce;
import particle.sph.force.SurfaceTensionForce;
import particle.sph.force.ViscosityForce;
import particle.sph.smoothing.DefaultSmoothingKernel;
import particle.sph.smoothing.PressureSmoothingKernel;
import particle.sph.smoothing.ViscositySmoothingKernel;
import rendering.Camera2D;
import util.Util;

/**
 * Created by iVerb on 23-5-2015.
 */
public class PhysicsSystem2D extends PhysicsSystem {

    public void initSimpleDemo() {
        Vector2f center = new Vector2f(0f, 3f);
        StandardParticle p1 = new StandardParticle(new Vector2f(2f, 3f));
        StandardParticle p2 = new StandardParticle(new Vector2f(4f, 3f));
        StandardParticle p3 = new StandardParticle(new Vector2f(6f, 3f));
        objects.addAll(p1, p2, p3);

        Force spring = new DampedSpringForce(p2, p3, 2, 9, 5);
        forces.add(spring);

        Constraint c1 = new CircularWireConstraint(center, p1, 2);
        Constraint c2 = new RodConstraint(p1, p2, 2);
        constraints.addAll(c1, c2);

        addStandardForces();
    }

    public void initClothDemo() {
        cam.drawStandardParticles = false;

        int numRows = 12;
        int numColumns = 17;
        Vector2f topLeft = new Vector2f(-5f, 4f);
        StandardParticle[][] particleNetwork = createSpringNetwork(numRows, numColumns, topLeft, 9, 35, 2);

        Constraint xc = new XConstraint(particleNetwork[0][0], topLeft.x, 0, 0);
        constraints.add(xc);
        for (int i = 0; i < numColumns; i++) {
            Constraint yc = new YConstraint(particleNetwork[0][i], topLeft.y, 0, 0);
            constraints.add(yc);
        }

        addStandardForces();
    }

    public void initClothWallDemo() {
        cam.drawStandardParticles = false;

        int numRows = 12;
        int numColumns = 17;
        Vector2f topLeft = new Vector2f(-7.9f, 4f);
        StandardParticle[][] particleNetwork = createSpringNetwork(numRows, numColumns, topLeft, 9, 35, 2);

        for (int i = 0; i < numColumns; i++) {
            Constraint yc = new YConstraint(particleNetwork[0][i], topLeft.y, 0, 0);
            constraints.add(yc);
        }

        Boundary b1 = new Boundary(new Vector2f(4f, -1f), new Vector2f(-1f, -1f));
        objects.addAll(b1);

        addStandardForces();
    }

    public void initWallDemo() {
        Boundary b1 = new Boundary(new Vector2f(-1,-2), new Vector2f(-0.7f, 0.3f));
        Boundary b2 = new Boundary(new Vector2f(0,-2), new Vector2f(-0.1f, 0.0f));
        Boundary b3 = new Boundary(new Vector2f(1,-2), new Vector2f(-0.7f, -0.3f));
        objects.addAll(b1, b2, b3);

        for (int i = 0; i < 20; i++) {
            StandardParticle p = new StandardParticle(new Vector2f(-5+0.5f*i + (float)Math.random(), 4f+(float)Math.random()), 1 + Math.random()*10);
            objects.addAll(p);
        }

        addStandardForces();
    }

    public void initAngularSpringsDemo() {
        Vector2f center = new Vector2f(0f, 3f);

        StandardParticle p1 = new StandardParticle(new Vector2f(0f, 2.9f));
        StandardParticle p2 = new StandardParticle(new Vector2f(0f, 2.8f));
        objects.addAll(p1, p2);

        Pair<StandardParticle, StandardParticle> lastParticles1 = addAngularSprings(15, 0.18, 170, 60, new Pair<StandardParticle, StandardParticle>(p1, p2));
        Pair<StandardParticle, StandardParticle> lastParticles2 = addAngularSprings(5, 0.18, 180, 60, lastParticles1);
        addAngularSprings(15, 0.18, 195, 60, lastParticles2);

        Constraint c1 = new CircularWireConstraint(center, p1, 0.1);
        Constraint c2 = new RodConstraint(p1, p2, 0.1);
        constraints.addAll(c1, c2);

        addStandardForces();

        Ks = 100;
        Kd = 100;
        variableTimeStep = false;
        timeStep = 0.006;
        cam.drawStandardParticles = false;
    }

    public void initFluidDemo() {
        interactors.add(new RigidBodySpawner(this));
        interactors.add(new HydroParticlesSpawner(this));

        Boundary b1 = new Boundary(new Vector2f(-1,-2), new Vector2f(0f, 1f));
        Boundary b2 = new Boundary(new Vector2f(0,-2), new Vector2f(-1f, 0.0f));
        Boundary b3 = new Boundary(new Vector2f(1,-2), new Vector2f(0f, -1f));
        objects.addAll(b1, b2, b3);

        PressureForce pressure = new PressureForce(this, new PressureSmoothingKernel(this));
        ViscosityForce viscosity = new ViscosityForce(this, new ViscositySmoothingKernel(this));
        SurfaceTensionForce surface = new SurfaceTensionForce(this, new DefaultSmoothingKernel(this));

        variableTimeStep = false;
        timeStep = 0.01;
        forces.add(pressure);
        forces.add(viscosity);
        forces.add(surface);
        solver = new EulerSolver();

        addStandardForces();
    }

    public void initFluidClothDemo() {
        interactors.add(new RigidBodySpawner(this));
        interactors.add(new HydroParticlesSpawner(this));

        int numRows = 7;
        int numColumns = 7;
        Vector2f topLeft = new Vector2f(-2f, 1f);
        StandardParticle[][] particleNetwork = createSpringNetwork(numRows, numColumns, topLeft, 2, 35, 5);

        for (int i = 0; i < numColumns; i++) {
            Constraint yc = new YConstraint(particleNetwork[0][i], topLeft.y, 0, 0);
            constraints.add(yc);
        }

        Boundary b1 = new Boundary(new Vector2f(-2.5f, -2f), new Vector2f(0f, 1f));
        Boundary b2 = new Boundary(new Vector2f(0f, -2f), new Vector2f(-1f, 0.0f));
        Boundary b3 = new Boundary(new Vector2f(1f, -2f), new Vector2f(0f, -1f));
        objects.addAll(b1, b2, b3);

        PressureForce pressure = new PressureForce(this, new PressureSmoothingKernel(this));
        ViscosityForce viscosity = new ViscosityForce(this, new ViscositySmoothingKernel(this));
        SurfaceTensionForce surface = new SurfaceTensionForce(this, new DefaultSmoothingKernel(this));

        variableTimeStep = false;
        timeStep = 0.01;
        forces.add(pressure);
        forces.add(viscosity);
        forces.add(surface);
        solver = new MidPointSolver();

        addStandardForces();
    }

    private StandardParticle[][] createSpringNetwork(int numRows, int numColumns, Vector2f topLeft, double width, double springConstantMultiplier, double dampingConstant) {
        StandardParticle[][] particleNetwork = new StandardParticle[numRows][numColumns];

        float spacing = (float)(width / numColumns);
        double springConstant = numRows * springConstantMultiplier;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                StandardParticle p = new StandardParticle(new Vector2f(topLeft.x + j * spacing, topLeft.y - i * spacing));
                if (i > 0) {
                    Force spring = new DampedSpringForce(particleNetwork[i - 1][j], p, spacing, springConstant, dampingConstant);
                    forces.add(spring);
                }
                if (j > 0) {
                    Force spring = new DampedSpringForce(particleNetwork[i][j - 1], p, spacing, springConstant, dampingConstant);
                    forces.add(spring);
                }
                particleNetwork[i][j] = p;
                objects.addAll(p);
            }
        }
        return particleNetwork;
    }

    private Pair<StandardParticle, StandardParticle> addAngularSprings(int amount, double length, double incrementalAngle, double springForce, Pair<StandardParticle, StandardParticle> lastParticles) {
        StandardParticle prev1 = lastParticles.getKey();
        StandardParticle prev2 = lastParticles.getValue();
        StandardParticle[] newParticles = new StandardParticle[amount];
        Constraint[] newConstraints = new Constraint[amount];

        for (int i = 0; i < amount; i++) {
            Vector2f pos1 = new Vector2f(prev1.initialPosition.x, prev1.initialPosition.y);
            Vector2f pos2 = new Vector2f(prev2.initialPosition.x, prev2.initialPosition.y);
            Vector2f dir = Vector2f.sub(pos2, pos1, null).normalise(null);
            Vector2f reverse = dir.negate(null);

            Vector2f rotated = Util.rotateClockWise(reverse, incrementalAngle);
            Vector2f newPos = Vector2f.add(pos2, (Vector2f)rotated.scale((float)length), null);

            StandardParticle p = new StandardParticle(newPos);
            newParticles[i] = p;

            RodConstraint rod = new RodConstraint(prev2, p, length);
            newConstraints[i] = rod;

            AngularSpringForce spring1 = new AngularSpringForce(prev1, prev2, p, incrementalAngle, springForce);
            forces.add(spring1);

            prev1 = prev2;
            prev2 = p;
        }
        objects.addAll(newParticles);
        constraints.addAll(newConstraints);
        return new Pair<StandardParticle, StandardParticle>(prev1, prev2);
    }

    @Override
    protected void resetCamera() {
        cam = new Camera2D(this);
    }

}
