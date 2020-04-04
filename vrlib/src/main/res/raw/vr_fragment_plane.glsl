#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTexCoord;
varying float vbarrelDistortion;
varying float vblackEdgeValue;
uniform samplerExternalOES sTexture;

void main() {
        if (vbarrelDistortion <= 0.0) {
            gl_FragColor =texture2D(sTexture, vTexCoord);
        }
        else
        {
            vec2 uv=vTexCoord;
            uv = uv * 2.0 - 1.0;
            float demoScale = 1.8 - vblackEdgeValue;
            uv *= demoScale;
            float barrelDistortion1 = -0.068; // 0.441 K1 in text books
            float barrelDistortion2 = 0.320000; // 0.156 K2 in text books
            float r2 = uv.x*uv.x + uv.y*uv.y;
            uv *= 1.0 + barrelDistortion1 * r2  + barrelDistortion2 * r2 * r2;
            uv = 0.5 * (uv * 1.0 + 1.0);
            vec4 color;
            if(uv.x>1.0||uv.y>0.93||uv.x<0.0||uv.y<0.07){
            color = vec4(0.0,0.0,0.0,1.0);
            }else{
            color = texture2D(sTexture, uv);
            }
            gl_FragColor = color;
        }
}