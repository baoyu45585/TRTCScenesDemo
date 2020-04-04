//
// Created by cain on 2018/2/8.
//

#include "GlShaders.h"

#ifdef __cplusplus
extern "C" {
#endif

#include <stdio.h>

static const char* lightingeffects_fragment_shader ="#extension GL_OES_EGL_image_external : require\n"
        "precision mediump float;\n"
        "varying vec2 textureCoordinate;\n"
        "uniform samplerExternalOES videoFrame;\n"
        "\n"
        "void main() {\n"
        "    //gl_FragColor=texture2D(sTexture, v_texPosition);\n"
        "    gl_FragColor = vec4(texture2D(videoFrame, textureCoordinate).rgb,texture2D(videoFrame, textureCoordinate+vec2(-0.5, 0)).r);\n"
        "}";


static const char* lightingeffects_vertex_shader = "attribute vec4 position;\n"
        "attribute vec4 inputTextureCoordinate;//S T 纹理坐标\n"
        "varying vec2 textureCoordinate;\n"
        "uniform mat4 Matrix;\n"
        "void main() {\n"
        "    textureCoordinate = vec2(inputTextureCoordinate.x, 1.0 - inputTextureCoordinate.y);\n"
        "    gl_Position = Matrix*position ;\n"
        "}";


// vertex shader
static const char vertex_shader_default[] = SHADER_STRING(
        uniform mat4 uMVPMatrix;
        attribute vec4 aPosition;
        attribute vec4 aTextureCoord;
        varying vec2 textureCoordinate;
        void main() {
            gl_Position = uMVPMatrix * aPosition;
            // 将图像倒过来，因为OpenGLES 的格式是RGBA的，而图像的格式则是ARGB
            textureCoordinate = vec2(aTextureCoord.x, 1.0 - aTextureCoord.y);
        }
);

static const char* vertex_shader_vertex = "attribute vec4 aPosition;\n"
        "attribute vec2 aTextureCoord;\n"
        "varying vec2 textureCoordinate;\n"
        "uniform mat4 uMVPMatrix;\n"
        "void main() {\n"
        "    textureCoordinate = aTextureCoord;\n"
        "    gl_Position = aPosition * uMVPMatrix;\n"
        "}";




// fragment shader solid color
static const char fragment_shader_solid[] = SHADER_STRING(
        precision mediump float;
        varying highp vec2 textureCoordinate;
        uniform vec4 u_color;
        void main() {
            gl_FragColor = u_color;
        }
);

// fragment shader(BGR to ABGR conversion)
static const char fragment_shader_bgr[] = SHADER_STRING(
        precision highp float;

        varying highp vec2 textureCoordinate;
        uniform lowp sampler2D inputTexture;

        void main()
        {
            gl_FragColor = vec4(texture2D(inputTexture, textureCoordinate).rgb, 1);
        }
);

// fragment shader(ABGR)
static const char fragment_shader_abgr[] = SHADER_STRING(
        precision highp float;

        varying highp vec2 textureCoordinate;
        uniform lowp sampler2D inputTexture;

        void main()
        {
            gl_FragColor = texture2D(inputTexture, textureCoordinate);
        }
);

// fragment shader ( ARGB to ABGR conversion)
static const char fragment_shader_argb[] = SHADER_STRING(
        precision highp float;

        varying highp vec2 textureCoordinate;
        uniform lowp sampler2D inputTexture;

        void main()
        {
            vec4 abgr = texture2D(inputTexture, textureCoordinate);

            gl_FragColor = abgr;
            gl_FragColor.r = abgr.b;
            gl_FragColor.b = abgr.r;
        }
);

// fragment shader (RGB to ABGR conversion)
static const char fragment_shader_rgb[] = SHADER_STRING(
        precision highp float;

        varying highp vec2 textureCoordinate;
        uniform lowp sampler2D inputTexture;

        void main()
        {
            vec4 abgr = texture2D(inputTexture, textureCoordinate);

            gl_FragColor = abgr;
            gl_FragColor.r = abgr.b;
            gl_FragColor.b = abgr.r;
            gl_FragColor.a = 1.0;
        }
);



// fragment shader (YUV420P/I420 to ARGB conversion)
static const char* fragment_shader_i420 =  "precision mediump float;\n"
        "varying vec2 textureCoordinate;\n"
        "uniform sampler2D inputTextureY;\n"
        "uniform sampler2D inputTextureU;\n"
        "uniform sampler2D inputTextureV;\n"
        "void main() {\n"
        "   float y,u,v;\n"
        "   y = texture2D(inputTextureY,textureCoordinate).r;\n"
        "   u = texture2D(inputTextureU,textureCoordinate).r - 0.5;\n"
        "   v = texture2D(inputTextureV,textureCoordinate).r - 0.5;\n"
        "\n"
        "   vec3 rgb;\n"
        "   rgb.r = y + 1.403 * v;\n"
        "   rgb.g = y - 0.344 * u - 0.714 * v;\n"
        "   rgb.b = y + 1.770 * u;\n"
        "\n"
        "   gl_FragColor = vec4(rgb,1);\n"
        "}";

// fragment shader (NV12 to ARGB conversion)
static const char fragment_shader_nv12[] = SHADER_STRING(
        precision mediump float;
        varying highp vec2 textureCoordinate;
        uniform lowp sampler2D inputTextureY;
        uniform lowp sampler2D inputTextureUV;

        void main() {
            mediump vec3 yuv;
            lowp vec3 rgb;
            yuv.x = texture2D(inputTextureY, textureCoordinate).r;
            yuv.yz = texture2D(inputTextureUV, textureCoordinate).ra - 0.5;
            rgb = mat3(1.0,          1.0,      1.0,
                       0.0,     -0.39465,  2.03211,
                       1.13983, -0.58060,      0.0) * yuv;
            gl_FragColor = vec4(rgb, 1.0);
        }
);

// fragment shader (NV21 to ARGB conversion)
static const char fragment_shader_nv21[] = SHADER_STRING(
        precision mediump float;
        varying highp vec2 textureCoordinate;
        uniform lowp sampler2D inputTextureY;
        uniform lowp sampler2D inputTextureUV;

        void main() {
            mediump vec3 yuv;
            lowp vec3 rgb;
            yuv.x = texture2D(inputTextureY, textureCoordinate).r;
            yuv.yz = texture2D(inputTextureUV, textureCoordinate).ar - 0.5;
            rgb = mat3(1.0,          1.0,      1.0,
                       0.0,     -0.39465,  2.03211,
                       1.13983, -0.58060,      0.0) * yuv;
            gl_FragColor = vec4(rgb, 1.0);
        }
);

/**
 * 获取shader程序
 * @param type
 * @return
 */
const char *GlShader_GetShader(ShaderType type) {
    switch(type) {
    case FRAGMENT_LIGHT:
            return lightingeffects_fragment_shader;
    case VERTEX_LIGHT:
            return lightingeffects_vertex_shader;
    case VERTEX_DEFAULT:
        return vertex_shader_default;
    case VERTEX_I420:
            return vertex_shader_vertex;
    case FRAGMENT_SOLID:
        return fragment_shader_solid;
    case FRAGMENT_ABGR:
        return fragment_shader_abgr;
    case FRAGMENT_ARGB:
        return fragment_shader_argb;
    case FRAGMENT_BGR:
        return fragment_shader_bgr;
    case FRAGMENT_RGB:
        return fragment_shader_rgb;
    case FRAGMENT_I420:
        return fragment_shader_i420;
    case FRAGMENT_NV12:
        return fragment_shader_nv12;
    case FRAGMENT_NV21:
        return fragment_shader_nv21;
        default:
            return NULL;
    }
}


#ifdef __cplusplus
};
#endif