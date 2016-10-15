package picking.picking3D;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by s113958 on 23-5-2015.
 */
public class MouseRay {

    public Vector3f origin;
    public Vector3f direction;

    public MouseRay(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = direction;
    }

}
