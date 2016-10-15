package rendering.shade3D;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import rendering.mesh.Mesh3D;

/**
 * Created by iVerb on 14-4-2015.
 */
public class LightSource extends Mesh3D {

    public Vector4f diffuseColor = new Vector4f(0.8f, 0.8f, 0.8f, 1f);
    public Vector4f specularColor = new Vector4f(0.6f, 0.6f, 0.6f, 1f);

    private float lightIntensity = 1f;

    public LightSource(Vector3f position) {
        super("sphere", position);

        this.doShade = false;
    }

    public float getLightIntensity() {
        return this.lightIntensity;
    }

}
