#version 300 es

precision highp float;

uniform struct {
  samplerCube envTexture; 
} material;

out vec4 fragmentColor;

in vec4 rayDir;

void main(void) {
  fragmentColor = texture(material.envTexture, rayDir.xyz); 
}