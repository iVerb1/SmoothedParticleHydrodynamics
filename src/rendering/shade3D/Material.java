package rendering.shade3D;

import org.lwjgl.util.vector.Vector4f;

import java.io.Serializable;

/**
 * Created by iVerb on 19-4-2015.
 */
public class Material implements Serializable {
    /** Between 0 and 1000. */
    public float shininess = 140;

    public Vector4f color = new Vector4f(1f, 1f, 1f, 1f);
    public Vector4f diffuseColor = new Vector4f(0.3f, 0.3f, 0.3f, 1f);
    public Vector4f specularColor = new Vector4f(1f, 1f, 1f, 1f);


}
