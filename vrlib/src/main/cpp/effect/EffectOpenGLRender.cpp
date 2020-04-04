//
// Created by mgted on 2020/2/23.
//
#include "EffectOpenGLRender.h"
#include "EffectOpenGLUtils.h"

#include "EffectVecmath.h"
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include "../common/GlShaders.h"

static const GLfloat squareVertices[] = {
    1.0f, -1.0f, 0.0f,
    -1.0f, -1.0f, 0.0f,
    1.0f, 1.0f, 0.0f,
    -1.0f,  1.0f, 0.0f,
};

static const GLfloat textureVertices[] = {
    1.0f,  0.0f,
    0.5f,  0.0f,
    1.0f, 1.0f,
    0.5f, 1.0f,
};





static GLuint EffectProgramID=-1;
static int EffectPositionHandle=-1;
static int EffectInputTextureCoordinateHandle=-1;
static int EffectVideoFrameHandle=-1;
static int EffectMatrixHandle=-1;
static int screenWidth=0;
static int screenHeight=0;
static int videoWidth=0;
static int videoHeight=0;
static float PlaneMatrix[16]={ 1.0f, 0.0f, 0.0f, 0.0f,
                               0.0f, 1.0f, 0.0f, 0.0f,
                               0.0f, 0.0f, 1.0f, 0.0f,
                               0.0f, 0.0f, 0.0f, 1.0f };

GLuint EffectOpenglRender:: GetAttribLocation(int type, char *type_name) {
    return glGetAttribLocation(type,type_name);
}

GLuint EffectOpenglRender:: GetUniformLocation(int type, char *type_name) {
    return glGetUniformLocation(type,type_name);
}

void EffectOpenglRender::init()
{

    EffectOpenGLUtils::createProgram(GlShader_GetShader(VERTEX_LIGHT),GlShader_GetShader(FRAGMENT_LIGHT));
    EffectOpenGLUtils::checkGLError("createProgram::init");
    EffectProgramID=EffectOpenGLUtils::getProgram();
    EffectOpenGLUtils::checkGLError("createProgram");
    EffectPositionHandle = EffectOpenglRender::GetAttribLocation(EffectProgramID, "position");
    EffectOpenGLUtils::checkGLError("EffectPositionHandle1");
    EffectInputTextureCoordinateHandle = EffectOpenglRender::GetAttribLocation(EffectProgramID, "inputTextureCoordinate");
    EffectOpenGLUtils::checkGLError("inputTextureCoordinate1");
    EffectVideoFrameHandle =EffectOpenglRender::GetUniformLocation(EffectProgramID, "videoFrame");
    EffectMatrixHandle = EffectOpenglRender::GetUniformLocation(EffectProgramID, "Matrix");

}

void EffectOpenglRender::render(float *Matrix,GLuint Textureid)
{
    EffectOpenGLUtils::checkGLError("EffectOpenglRender::render");
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
    glUseProgram(EffectProgramID);
    glEnableVertexAttribArray(EffectPositionHandle);
    glVertexAttribPointer(EffectPositionHandle, 3, GL_FLOAT, false,0, squareVertices);
    EffectOpenGLUtils::checkGLError("EffectPositionHandle");
    
    glEnableVertexAttribArray(EffectInputTextureCoordinateHandle);
    glVertexAttribPointer(EffectInputTextureCoordinateHandle, 2, GL_FLOAT, false, 0, textureVertices);
    EffectOpenGLUtils::checkGLError("EffectInputTextureCoordinateHandle");

    float screenRatio=(float)1.0f*screenWidth/screenHeight;
    float videoRatio=(float)1.0f*videoWidth/videoHeight;
    if (videoRatio>screenRatio)
    {
         screenRatio=(float)1.0f*screenWidth/screenHeight*2;
         videoRatio=(float)1.0f*videoWidth/videoHeight;
        EffectMatrix4::orthoM(PlaneMatrix,0,-1.0f,1.0f,1.0f*-videoRatio/screenRatio,1.0f*videoRatio/screenRatio,-1.0f,1.0f);
    }else
    {
        screenRatio=(float)1.0f*screenWidth/screenHeight*2;
        videoRatio=(float)1.0f*videoWidth/videoHeight;
        EffectMatrix4::orthoM(PlaneMatrix,0,1.0f*-screenRatio/videoRatio,1.0f*screenRatio/videoRatio,-1.0f,1.0f,-1.0f,1.0f);
    }
    glUniformMatrix4fv(EffectMatrixHandle, 1, false, PlaneMatrix);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, Textureid);
    EffectOpenGLUtils::checkGLError("GL_TEXTURE_2D");
    glUniform1i(EffectVideoFrameHandle, 0);
    glViewport(0,0,screenWidth,screenHeight);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    EffectOpenGLUtils::checkGLError("glDrawArrays");
    glBindTexture(GL_TEXTURE_2D, 0);
}

void EffectOpenglRender::setscreenwidthheight(int width, int height)
{
    screenWidth=width;
    screenHeight=height;
}

void EffectOpenglRender::setvideowidthheight(int width, int height)
{
    videoWidth=width;
    videoHeight=height;
}

void EffectOpenglRender::destroy()
{
    if( EffectOpenGLUtils::getTextureID() !=-1 ) {
        EffectOpenGLUtils::destroyTextureID();
    }
}