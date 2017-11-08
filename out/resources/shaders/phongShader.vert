#version 120

varying vec3 vPos;
varying vec3 normal;

void main() {
    vPos = vec3(gl_ModelViewMatrix * gl_Vertex);
    normal = normalize(gl_NormalMatrix * gl_Normal);


    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

}