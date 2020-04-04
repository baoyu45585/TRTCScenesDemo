//
// Created by mgted on 2020/2/23.
//

#ifndef EFFECTOPENGLRENDER_H
#define EFFECTOPENGLRENDER_H



#include <stdio.h>
#include <stdlib.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <GLES2/gl2platform.h>



class EffectOpenglRender {
public:
    // 创建program
    static GLuint GetUniformLocation(int type,char *type_name);
    static GLuint GetAttribLocation(int type, char *type_name);
    static void init();
    static void render(float *Matrix,GLuint Textureid);
    static void setscreenwidthheight(int width, int height);
    static void setvideowidthheight(int width, int height);
    static void destroy();
private:
    EffectOpenglRender() = default;
    virtual ~EffectOpenglRender(){}
};
#endif //EFFECTOPENGLRENDER_H
