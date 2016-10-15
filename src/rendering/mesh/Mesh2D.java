package rendering.mesh;

import org.lwjgl.util.vector.Vector2f;
import particle.State;
import rendering.Renderable;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by iVerb on 16-6-2015.
 */
public class Mesh2D extends Renderable {

    public Vector2f position;
    public Vector2f[] points;
    public float rotation;

    public float[] color;
    public boolean doRender;


    public Mesh2D(Vector2f position, Vector2f... points) {
        this.position = position;
        this.rotation = 0f;
        this.points = points;

        this.doRender = true;
        this.color = new float[] {1f, 0f, 0f, 0f};
    }

    public void setRotation(float rotation) {

    }

    @Override
    public void draw() {
        if (doRender) {
            glColor4f(color[0], color[1], color[2], color[3]);
            glBegin(GL_POINTS);
            for (Vector2f point : points) {
                glVertex2f(point.x, point.y);
            }
            glEnd();
        }
    }
}
