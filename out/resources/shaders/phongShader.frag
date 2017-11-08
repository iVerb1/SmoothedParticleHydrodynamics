#version 120

varying vec3 vPos;
varying vec3 normal;

uniform vec4 baseColor;
uniform vec4 matDiffuse;
uniform vec4 matSpecular;
uniform float shininess;
uniform int shade;
uniform int numLights;
uniform float lightAttenuation;
uniform float lightIntensities[8];

void main()
{
    gl_FragColor = baseColor;

    if (shade == 1) {

        vec4 illumination = gl_LightModel.ambient;

        for (int i = 0; i < numLights; i++) {

            vec3 lightVector = gl_LightSource[i].position.xyz - vPos;
            vec3 lightDir = normalize(lightVector);
            vec3 eyeDirection = normalize(-vPos);
            vec3 reflectionDir = normalize(reflect(-lightDir, normal));

            //calculate diffuse term:
            float diffuse = max(dot(normal, lightDir), 0.0);
            vec4 Idiff = (gl_LightSource[i].diffuse * matDiffuse) * diffuse;
            Idiff = clamp(Idiff, 0.0, 1.0);

            //calculate specular term:
            vec4 Ispec = vec4(0, 0, 0, 1);
            if (shininess > 0) {
                float specular = max(dot(reflectionDir, eyeDirection), 0.0);
                Ispec = (gl_LightSource[i].specular * matSpecular) * pow(specular, shininess);
                Ispec = clamp(Ispec, 0.0, 1.0);
            }

            float attenuation = max(0, -(pow(length(lightVector), 2) * (0.001 * lightAttenuation)) + 1);
            illumination += (lightIntensities[i] * attenuation * (Idiff + Ispec));
         }
		 
		// write Total Color:
		gl_FragColor *= illumination;
    }
}