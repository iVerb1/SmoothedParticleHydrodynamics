package rendering.mesh;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import particle.State;
import rendering.Renderable;
import rendering.shade3D.Material;
import rendering.shade3D.ShaderUtils;

import java.io.FileNotFoundException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

/**
 * Created by s113427 on 4-3-2015.
 */
public class Mesh3D extends Renderable {

    public String modelName;
    public transient Model model;
    public Material material;

    public boolean doShade;

    public Vector3f position;
    public float scalingFactor;
    public Matrix4f rotation;
    protected transient FloatBuffer rotationFloatBuffer;

    public Mesh3D(String modelName, Vector3f position) {
        this.modelName = modelName;
        this.material = new Material();
        loadModel(modelName);

        this.doShade = true;

        this.position = position;
        this.scalingFactor = 1.0f;
        this.rotationFloatBuffer = BufferUtils.createFloatBuffer(16);
        this.rotation = new Matrix4f();
        this.setRotation(this.rotation);
    }

    public Mesh3D(String modelName) {
        this(modelName, new Vector3f(0f, 0f, 0f));
    }

    public void setRotation(Matrix4f rotation) {
        this.rotation = rotation;
        this.rotationFloatBuffer.rewind();
        rotation.store(this.rotationFloatBuffer);
        this.rotationFloatBuffer.flip();
    }

    private void loadModel(String modelName) {
        try {
            model = Model.fromObj(modelName);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw() {
        glPushMatrix();

        glTranslatef(position.x, position.y, position.z);
        glScalef(scalingFactor, scalingFactor, scalingFactor);
        glMultMatrix(rotationFloatBuffer);

        glBindBuffer(GL_ARRAY_BUFFER, model.vertexBufferId);
        glVertexPointer(3, GL_FLOAT, 0, 0L);

        if (glGetInteger(GL_RENDER_MODE) == GL_RENDER) {
            ShaderUtils.initializeShader(doShade, material);

            glBindBuffer(GL_ARRAY_BUFFER, model.normalBufferId);
            glNormalPointer(GL_FLOAT, 0, 0L);
        }

        glDrawArrays(GL_TRIANGLES, 0, model.getFaces().size() * 3);


        glPopMatrix();
    }

}