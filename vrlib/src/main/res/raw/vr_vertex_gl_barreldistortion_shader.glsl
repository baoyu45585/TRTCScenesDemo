attribute vec4 av_Position;
attribute vec2 aTexCoord;
varying vec2 vTexCoord;
varying float vbarrelDistortion;
varying float vblackEdgeValue;
uniform mat4 u_Matrix;
uniform float barrelDistortion;
uniform float blackEdgeValue;

void main() {
    vTexCoord = aTexCoord;
    vbarrelDistortion = barrelDistortion;
    vblackEdgeValue= blackEdgeValue;
    gl_Position =av_Position ;
}
