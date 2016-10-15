package rendering.shade3D;

import rendering.Camera3D;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static util.Util.*;

/**
 * Created by s113427 on 8-3-2015.
 */
public class ShaderUtils {

    public static int loadShaderProgram(int... shaderPrograms) {
        int shaderProgram = glCreateProgram();

        for (int p : shaderPrograms) {
            glAttachShader(shaderProgram, p);
        }

        glLinkProgram(shaderProgram);
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Shader program wasn't linked correctly.");
            System.err.println(glGetProgramInfoLog(shaderProgram, 1024));
            return -1;
        }

        for (int p : shaderPrograms) {
            glDeleteShader(p);
        }

        return shaderProgram;
    }

    public static int loadShader(String location, int shaderType) {
        int shader = glCreateShader(shaderType);
        StringBuilder shaderSource = new StringBuilder();
        BufferedReader shaderFileReader = null;

        try {
            shaderFileReader = new BufferedReader(new FileReader(location));
            String line;
            while ((line = shaderFileReader.readLine()) != null) {
                shaderSource.append(line).append('\n');
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        finally {
            if (shaderFileReader != null) {
                try {
                    shaderFileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        glShaderSource(shader, shaderSource);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Shader wasn't able to be compiled correctly. Error log:");
            System.err.println(glGetShaderInfoLog(shader, 1024));
        }

        return shader;
    }

    public static void initializeShader(boolean doShade, Material material) {

        int curProg = glGetInteger(GL_CURRENT_PROGRAM);

        int shadeLoc = glGetUniformLocation(curProg, "shade");
        glUniform1i(shadeLoc, (doShade) ? 1 : 0);

        int numLightsLoc = glGetUniformLocation(curProg, "numLights");
        glUniform1i(numLightsLoc, LightSourceManager.lightSources.size());

        int colorLoc = glGetUniformLocation(curProg, "baseColor");
        glUniform4(colorLoc, asFloatBuffer(asArray(material.color)));

        int matDiffuseLoc = glGetUniformLocation(curProg, "matDiffuse");
        glUniform4(matDiffuseLoc, asFloatBuffer(asArray(material.diffuseColor)));

        int matSpecularLoc = glGetUniformLocation(curProg, "matSpecular");
        glUniform4(matSpecularLoc, asFloatBuffer(asArray(material.specularColor)));

        int shininessLoc = glGetUniformLocation(curProg, "shininess");
        glUniform1f(shininessLoc, material.shininess);

        int attenuationLoc = glGetUniformLocation(curProg, "lightAttenuation");
        glUniform1f(attenuationLoc, Camera3D.LIGHT_ATTENUATION);

        FloatBuffer lightIntensities = BufferUtils.createFloatBuffer(LightSourceManager.lightIntensities.length);
        lightIntensities.put(LightSourceManager.lightIntensities);
        lightIntensities.rewind();
        int lightIntensitiesLoc = glGetUniformLocation(curProg, "lightIntensities");
        glUniform1(lightIntensitiesLoc, lightIntensities);
    }

}


