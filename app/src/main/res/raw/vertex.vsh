#version 300 es
layout(location = 0) vec4 aPosition;
layout(location = 1) vec4 aColor;

out vec4 color;

void main(){
    gl_Position = aPosition;
    color = aColor;
}