#version 310
layout(location = 0) vec4 aPosition;

void main(void){
    gl_Position = aPosition;
}