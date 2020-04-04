#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 v_texPosition;
uniform samplerExternalOES sTexture;

void main() {
        vec4 color;
        vec2 uv=v_texPosition;
        color=texture2D(sTexture, uv);
        gl_FragColor=color;
}