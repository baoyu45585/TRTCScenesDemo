attribute vec4 av_Position;
attribute vec4 aTexCoord;
varying vec2 vTexCoord;
varying float vbarrelDistortion;
varying float vblackEdgeValue;
uniform float barrelDistortion;
uniform float blackEdgeValue;
uniform mat4 u_Matrix;
uniform mat4 uSTMatrix;

void main() {
    vTexCoord =(uSTMatrix*aTexCoord).xy;
    vbarrelDistortion = barrelDistortion;
    vblackEdgeValue= blackEdgeValue;
    gl_Position =u_Matrix*av_Position ;
}
