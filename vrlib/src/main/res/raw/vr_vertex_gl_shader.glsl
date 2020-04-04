attribute vec4 av_Position;
attribute vec4 af_Position;//S T 纹理坐标
varying vec2 v_texPosition;
uniform mat4 u_Matrix;
uniform mat4 uSTMatrix;
void main() {
    v_texPosition= (af_Position).xy;
    gl_Position = u_Matrix*av_Position ;
}
