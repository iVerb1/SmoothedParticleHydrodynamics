package rendering.mesh;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static util.Util.asArray3;

/**
 * Created by s113427 on 7-3-2015.
 */
public class Model {

    public static final Map<String, Model> modelMap = new HashMap<String, Model>();

    public transient final List<Vector3f> vertices;
    public transient final List<Vector3f> normals;
    public transient final List<Face> faces;

    public transient int vertexBufferId;
    public transient int normalBufferId;

    private static String modelsFolder = "resources/models";


    public Model(List<Vector3f> vertices, List<Vector3f> normals, List<Face> faces) {
        this.vertices = vertices;
        this.normals = normals;
        this.faces = faces;
    }

    public List<Vector3f> getVertices() {
        return vertices;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }

    public List<Face> getFaces() {
        return faces;
    }

    public void createVBOs() {
        if (Display.isCreated()) {
            vertexBufferId = glGenBuffers();
            normalBufferId = glGenBuffers();

            FloatBuffer vertices = BufferUtils.createFloatBuffer(getFaces().size() * 9);
            FloatBuffer normals = BufferUtils.createFloatBuffer(getFaces().size() * 9);

            for (Face face : getFaces()) {
                vertices.put(asArray3(getVertices().get(face.getVertexIndices()[0] - 1)));
                vertices.put(asArray3(getVertices().get(face.getVertexIndices()[1] - 1)));
                vertices.put(asArray3(getVertices().get(face.getVertexIndices()[2] - 1)));
                normals.put(asArray3(getNormals().get(face.getNormalIndices()[0] - 1)));
                normals.put(asArray3(getNormals().get(face.getNormalIndices()[1] - 1)));
                normals.put(asArray3(getNormals().get(face.getNormalIndices()[2] - 1)));
            }
            vertices.flip();
            normals.flip();

            glBindBuffer(GL_ARRAY_BUFFER, vertexBufferId);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, normalBufferId);
            glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
    }

    public static void recreateAllVBOs() {
        for (Model m : modelMap.values()) {
            m.createVBOs();
        }
    }

    public static Model fromObj(String name) throws FileNotFoundException {
        if (modelMap.containsKey(name)) {
            Model m = modelMap.get(name);
            return m;
        }
        else {
            Scanner sc = new Scanner(new File(modelsFolder + "/" + name + ".obj"));
            List<Vector3f> vertices = new ArrayList<Vector3f>();
            List<Vector3f> normals = new ArrayList<Vector3f>();
            List<Face> faces = new ArrayList<Face>();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String prefix = line.split(" ")[0];
                if (prefix.equals("#") || prefix.equals("o") || prefix.equals("s")) {
                    continue;
                } else if (prefix.equals("v")) {
                    String[] xyz = line.split(" ");
                    vertices.add(new Vector3f(
                            Float.parseFloat(xyz[1]),
                            Float.parseFloat(xyz[2]),
                            Float.parseFloat(xyz[3])
                    ));
                } else if (prefix.equals("vn")) {
                    String[] xyz = line.split(" ");
                    normals.add(new Vector3f(
                            Float.parseFloat(xyz[1]),
                            Float.parseFloat(xyz[2]),
                            Float.parseFloat(xyz[3])
                    ));
                } else if (prefix.equals("f")) {
                    String[] faceIndices = line.split(" ");
                    faces.add(new Face(
                            new int[]{
                                    Integer.parseInt(faceIndices[1].split("/")[0]),
                                    Integer.parseInt(faceIndices[2].split("/")[0]),
                                    Integer.parseInt(faceIndices[3].split("/")[0])
                            },
                            new int[]{
                                    Integer.parseInt(faceIndices[1].split("/")[2]),
                                    Integer.parseInt(faceIndices[2].split("/")[2]),
                                    Integer.parseInt(faceIndices[3].split("/")[2])
                            }
                    ));
                } else {
                    throw new RuntimeException("OBJ file contains line which cannot be parsed correctly: " + line);
                }
            }
            Model m = new Model(vertices, normals, faces);
            m.createVBOs();
            modelMap.put(name, m);
            return m;
        }
    }

    public static class Face implements Cloneable {

        private final int[] vertexIndices = {-1, -1, -1};
        private final int[] normalIndices = {-1, -1, -1};

        public boolean hasNormals() {
            return normalIndices[0] != -1;
        }

        public int[] getVertexIndices() {
            return vertexIndices;
        }

        public int[] getNormalIndices() {
            return normalIndices;
        }

        public Face(int[] vertexIndices) {
            this.vertexIndices[0] = vertexIndices[0];
            this.vertexIndices[1] = vertexIndices[1];
            this.vertexIndices[2] = vertexIndices[2];
        }

        public Face(int[] vertexIndices, int[] normalIndices) {
            this.vertexIndices[0] = vertexIndices[0];
            this.vertexIndices[1] = vertexIndices[1];
            this.vertexIndices[2] = vertexIndices[2];
            this.normalIndices[0] = normalIndices[0];
            this.normalIndices[1] = normalIndices[1];
            this.normalIndices[2] = normalIndices[2];
        }

        @Override
        public Object clone() {
            int[] newVertexIndices = new int[3];
            int[] newNormalIndices = new int[3];
            System.arraycopy( vertexIndices, 0, newVertexIndices, 0, vertexIndices.length );
            System.arraycopy( normalIndices, 0, newNormalIndices, 0, normalIndices.length );
            return new Face(newVertexIndices, newNormalIndices);
        }
    }

    @Override
    public Model clone() {
        Model m = new Model(
                cloneVector3fList(vertices),
                cloneVector3fList(normals),
                cloneFaces()
        );
        return m;
    }

    private Vector3f cloneVector3f(Vector3f v) {
        return new Vector3f(v.x, v.y, v.z);
    }

    private List<Vector3f> cloneVector3fList(List<Vector3f> vectors) {
        List<Vector3f> result = new ArrayList<Vector3f>();
        for (Vector3f v : vectors) {
            result.add(cloneVector3f(v));
        }
        return result;
    }

    private List<Face> cloneFaces() {
        List result = new ArrayList();
        for (Face f : faces) {
            result.add(f.clone());
        }
        return result;
    }

}