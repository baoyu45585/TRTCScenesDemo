//
// Created by mgted on 2020/2/23.
//
#include <math.h>
#include "Sphere3D.h"
#include "OpenGLUtils.h"
#include "vecmath.h"
#include "../common/native_log.h"
#include "math3d.h"
static int Sphere3D_barreldistortion_uMatrixHandle=-1;
static int Sphere3D_barreldistortion_aTextureCoordHandle=-1;
static int Sphere3D_avPositionHandle=0;
static int Sphere3D_barreldistortion_avPositionHandle=0;
static int Sphere3D_afPositionHandle=0;
static int Sphere3D_barreldistortion_afPositionHandle=0;
static int Sphere3D_uMatrixHandle=0;
static int Sphere3D_uSTMMatrixHandle=0;
static int Sphere3D_uTextureSamplerHandle=0;
static int Sphere3D_barreldistortion_uTextureSamplerHandle=0;
static int Sphere3D_Program=0;
static int Sphere3D_barreldistortionProgram=-1;
static int Sphere3D_PlaneProgram=-1;
static int Sphere3D_TextureID=-1;
static float matrix[16];
static float *vertexs=NULL;//球
static float *texcoords=NULL;
static short *indices=NULL;

static float *upvertexs=NULL;//上半球
static float *uptexcoords=NULL;
static short *upindices=NULL;

static float *downvertexs=NULL;//下半球
static float *downtexcoords=NULL;
static short *downindices=NULL;

static float *leftvertexs=NULL;//左半球
static float *lefttexcoords=NULL;
static short *leftindices=NULL;

static float *rightvertexs=NULL;//右半球
static float *righttexcoords=NULL;
static short *rightindices=NULL;

static int mNumIndices=0;
static float modelMatrix[16];
static float projectionMatrix[16];
static float viewMatrix[16];
static float viewMatrixlook[16];
static float modelViewMatrix[16];
static float mMVPMatrix[16];
static float mWorldRotationMatrix[16];
static float mSensorMatrix[16];
static int mDeltaX=0.0f;
static int mDeltaY=0.0f;
static int mScale=1.0f;
static int screenWidth=0;
static int screenHeight=0;
static int planevideoWidth=0;
static int planedvideoHeight=0;
static GLuint Sphere3D_framebuffe=-1;
static GLuint Sphere3D_fbotexture=-1;
static int Spheretype=0;
static int Sphere3D_barreldistortion_flagrHandle=0;
static int Sphere3D_barreldistortion_vblackEdgeValueHandle=0;
static int Sphere3D_Plane_avPositionHandle = 0;
static int Sphere3D_Plane_aTextureCoordHandle = 0;
static int Sphere3D_Plane_uTextureSamplerHandle =0;
static int Sphere3D_Plane_flagrHandle=0;
static int Sphere3D_Plane_vblackEdgeValueHandle=0;
static int Sphere3D_Plane_uMatrixHandle=0;
static int Sphere3D_Plane_uSTMMatrixHandle=0;
static float Sphere3D_fingerX=0.0f;
static float Sphere3D_fingerY=0.0f;

static bool isUsingTouch=false;//触控是否开启
static bool isUsingMotion=true;//陀螺仪是否开启

float textureVertexData []= {
        1.0f,0.0f,
        0.0f,0.0f,
        1.0f,1.0f,
        0.0f,1.0f
};

   float vertexData [] = {
           1.0f,-1.0f,0.0f,
           -1.0f,-1.0f,0.0f,
           1.0f,1.0f,0.0f,
           -1.0f,1.0f,0.0f
};

float planetextureVertexData []= {
        1.0f,0.0f,
        0.0f,0.0f,
        1.0f,1.0f,
        0.0f,1.0f
};


float planevertexData [] = {
        1.0f,-1.0f,0.0f,
        -1.0f,-1.0f,0.0f,
        1.0f,1.0f,0.0f,
        -1.0f,1.0f,0.0f
};


GLuint Sphere3D:: GetAttribLocation(int type, char *type_name) {
    return glGetAttribLocation(type,type_name);
}

GLuint Sphere3D:: GetUniformLocation(int type, char *type_name) {
    return glGetUniformLocation(type,type_name);
}
void Sphere3D::SetSpherevideosize(int videoWidth, int videoHeight)
{
     planevideoWidth=videoWidth;
     planedvideoHeight=videoHeight;
}

void Sphere3D::init(int videoWidth, int videoHeight)
{

        OpenGLUtils::createFrameBuffer(&Sphere3D_framebuffe, &Sphere3D_fbotexture, videoWidth,
                                       videoHeight);

        //球体
        Sphere3D_Program = OpenGLUtils::getProgram();
        Sphere3D_avPositionHandle = Sphere3D::GetAttribLocation(Sphere3D_Program, "av_Position");
        Sphere3D_afPositionHandle = Sphere3D::GetAttribLocation(Sphere3D_Program, "af_Position");
        Sphere3D_uTextureSamplerHandle = Sphere3D::GetUniformLocation(Sphere3D_Program, "sTexture");
        Sphere3D_uMatrixHandle = Sphere3D::GetUniformLocation(Sphere3D_Program, "u_Matrix");
        Sphere3D_uSTMMatrixHandle = Sphere3D::GetUniformLocation(Sphere3D_Program, "uSTMatrix");

        //2d贴图
        Sphere3D_barreldistortionProgram = OpenGLUtils::barreldistortiongetProgram();
        Sphere3D_barreldistortion_avPositionHandle = Sphere3D::GetAttribLocation(
                Sphere3D_barreldistortionProgram, "av_Position");
        Sphere3D_barreldistortion_aTextureCoordHandle = Sphere3D::GetAttribLocation(
                Sphere3D_barreldistortionProgram, "aTexCoord");
        Sphere3D_barreldistortion_uTextureSamplerHandle = Sphere3D::GetUniformLocation(
                Sphere3D_barreldistortionProgram, "sTexture");
        //是否畸变
        Sphere3D_barreldistortion_flagrHandle = Sphere3D::GetUniformLocation(
                Sphere3D_barreldistortionProgram, "barrelDistortion");
        //畸变参数
        Sphere3D_barreldistortion_vblackEdgeValueHandle = Sphere3D::GetUniformLocation(
                Sphere3D_barreldistortionProgram, "blackEdgeValue");

        //普通视频
        Sphere3D_PlaneProgram = OpenGLUtils::PlaneProgram();
        Sphere3D_Plane_avPositionHandle = Sphere3D::GetAttribLocation(Sphere3D_PlaneProgram,
                                                                      "av_Position");
        Sphere3D_Plane_aTextureCoordHandle = Sphere3D::GetAttribLocation(Sphere3D_PlaneProgram,
                                                                         "aTexCoord");
        Sphere3D_Plane_uTextureSamplerHandle = Sphere3D::GetUniformLocation(Sphere3D_PlaneProgram,
                                                                            "sTexture");
        Sphere3D_Plane_uMatrixHandle = Sphere3D::GetUniformLocation(Sphere3D_PlaneProgram,
                                                                    "u_Matrix");
        Sphere3D_Plane_uSTMMatrixHandle = Sphere3D::GetUniformLocation(Sphere3D_PlaneProgram,
                                                                       "uSTMatrix");
        Sphere3D_Plane_flagrHandle = Sphere3D::GetUniformLocation(Sphere3D_PlaneProgram,
                                                                  "barrelDistortion");
        //畸变参数
        Sphere3D_Plane_vblackEdgeValueHandle = Sphere3D::GetUniformLocation(Sphere3D_PlaneProgram,
                                                                            "blackEdgeValue");

    if(Spheretype==0||Spheretype==1)
    {
        Sphere3D::createSphere(18,75,150);
    }
    else if(Spheretype==4)//原视频为上下两层全屏显示
    {
        Sphere3D::createUpSphere(18,75,150);
    }
    else if(Spheretype==5)//原视频为上下两层 左右显示
    {
        Sphere3D::createUpSphere(18,75,150);
        Sphere3D::createDownSphere(18,75,150);
    }
    else if(Spheretype==6)//原视频为左右两边全屏显示
    {
        Sphere3D::createLeftSphere(18,75,150);
    }
    else if(Spheretype==7)//原视频为左右两边左右显示
    {
        Sphere3D::createLeftSphere(18,75,150);
        Sphere3D::createRightSphere(18,75,150);
    }

}

void Sphere3D::render(float *Matrix)
{
    float mTempMatrix[16];
    float mRotationMatrix[16];
    float IdentityMatrix[16];
    float PlaneMatrix[16];

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    glUseProgram(Sphere3D_Program);
    float screenRatio=(float)1.0f*screenWidth/screenHeight;
    float videoRatio=(float)1.0f*planevideoWidth/planedvideoHeight;
    m3dLoadIdentity44(modelMatrix);

#if 0
    m3dLoadIdentity44(mTempMatrix);
    m3dLoadIdentity44(modelMatrix);
    m3dLoadIdentity44(mWorldRotationMatrix);
    m3dLoadIdentity44(mRotationMatrix);
    m3dLoadIdentity44(IdentityMatrix);
    m3dLoadIdentity44(viewMatrix);

    m3dsetRotateM(mRotationMatrix, 0, 0.0f, 1.0f, 0.0f, 0.0f);
    m3dsetRotateM(mRotationMatrix, 0, 0.0f, 0.0f, 1.0f, 0.0f);
    m3dsetRotateM(mRotationMatrix, 0, 0.0f, 0.0f, 0.0f, 1.0f);
    m3dtranslateM(mRotationMatrix, 0,  0.0f, 0.0f, 0.0f);
    m3dsetRotateM(mRotationMatrix, 0, 0.0f, 1.0f, 0.0f, 0.0f);
    m3dsetRotateM(mRotationMatrix, 0, 90.0f, 0.0f, 1.0f, 0.0f);
    m3dsetRotateM(mRotationMatrix, 0,0.0f, 0.0f, 0.0f, 1.0f);




    m3dRotationMatrix44(mWorldRotationMatrix,-mDeltaY, 1.0f, 0.0f, 0.0f);
    m3dRotationMatrix44(modelMatrix,-mDeltaX, 0.0f, 1.0f, 0.0f);

    float screenRatio=(float)1.0f*screenWidth/screenHeight;
    float videoRatio=(float)1.0f*planevideoWidth/planedvideoHeight;

    /*if (videoRatio>screenRatio)
    {
        Matrix4::orthoM(projectionMatrix,0,-1.0f,1.0f,1.0f*-videoRatio/screenRatio,1.0f*videoRatio/screenRatio,-1.0f,1.0f);
    }else
    {
        Matrix4::orthoM(projectionMatrix,0,1.0f*-screenRatio/videoRatio,1.0f*screenRatio/videoRatio,-1.0f,1.0f,-1.0f,1.0f);
    }*/


   // Sphere3D::perspective(projectionMatrix,70.0f,1.0f*screenHeight/screenWidth/2, 1.0f, 500.0f);
   // Sphere3D::perspective(projectionMatrix,55.0f,1.0f*screenWidth/screenHeight/2, 1.0f, 500.0f);

    if(Spheretype==0)
    {

        m3dfrustumM(projectionMatrix,0,1.0f*-screenRatio/2,1.0f*screenRatio/2,-0.5f,0.5f,0.4f,500.f);//全屏模式
    }else if(Spheretype==1)
    {
        m3dfrustumM(projectionMatrix,0,1.0f*-screenRatio/4,1.0f*screenRatio/4,-0.5f,0.5f,0.4f,500.f);//半屏模式
    }

    if(Spheretype==0||Spheretype==1)
    {

        m3dMatrixMultiply44(mTempMatrix, modelMatrix, mRotationMatrix);
        m3dMatrixMultiply44(modelMatrix, mSensorMatrix, mTempMatrix);
        m3dMatrixMultiply44(mTempMatrix, mWorldRotationMatrix, modelMatrix);

        m3dMatrixMultiply44(viewMatrix, viewMatrixlook, mTempMatrix);

        m3dMatrixMultiply44(modelViewMatrix, viewMatrix, IdentityMatrix);


        m3dMatrixMultiply44(mMVPMatrix, projectionMatrix, modelViewMatrix);


        glUniformMatrix4fv(Sphere3D_uMatrixHandle, 1, false, mMVPMatrix);

        glUniformMatrix4fv(Sphere3D_uSTMMatrixHandle, 1, false, Matrix);



        glEnableVertexAttribArray(Sphere3D_avPositionHandle);
        glVertexAttribPointer(Sphere3D_avPositionHandle, 3, GL_FLOAT, false,0, vertexs);
        OpenGLUtils::checkGLError("Sphere3D_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_afPositionHandle);
        glVertexAttribPointer(Sphere3D_afPositionHandle, 2, GL_FLOAT, false, 0, texcoords);
        OpenGLUtils::checkGLError("Sphere3D_afPositionHandle");
    }
#endif


#if 1
    if(Spheretype==0||Spheretype==1)
    {

        float near=0.1f;
        float far=1000.0f;
        if(Spheretype==0)
        m3dMakePerspectiveMatrix(projectionMatrix,75.0f,1.0f*screenWidth/ screenHeight,near,far);
        if(isUsingTouch==true&&isUsingMotion==true)
        {
            float rotatex[16];
            float rotatey[16];
            m3dLoadIdentity44(rotatex);
            m3dLoadIdentity44(rotatey);
            m3dRotationMatrix44(rotatex, -Sphere3D_fingerY, 1.0f, 0.0f, 0.0f);
            m3dRotationMatrix44(rotatey, -Sphere3D_fingerX, 0.0f, 1.0f, 0.0f);
            m3dMatrixMultiply44(modelMatrix, rotatex, rotatey);
            m3dMatrixMultiply44(modelMatrix, modelMatrix, mSensorMatrix);
        }

        if(isUsingTouch==false&&isUsingMotion==true)
        {
            m3dMatrixMultiply44(modelMatrix, modelMatrix, mSensorMatrix);
        }

        if(isUsingTouch==true&&isUsingMotion==false)
        {
            float rotatex[16];
            float rotatey[16];
            m3dLoadIdentity44(rotatex);
            m3dLoadIdentity44(rotatey);
            m3dRotationMatrix44(rotatex, -Sphere3D_fingerY, 1.0f, 0.0f, 0.0f);
            m3dRotationMatrix44(rotatey, -Sphere3D_fingerX, 0.0f, 1.0f, 0.0f);
            m3dMatrixMultiply44(modelMatrix, rotatex, rotatey);
        }



        m3dMatrixMultiply44(modelViewMatrix,viewMatrixlook,modelMatrix);

        m3dMatrixMultiply44(mMVPMatrix,projectionMatrix,modelViewMatrix);

        glUniformMatrix4fv(Sphere3D_uMatrixHandle, 1, false, mMVPMatrix);

        glUniformMatrix4fv(Sphere3D_uSTMMatrixHandle, 1, false, Matrix);



        glEnableVertexAttribArray(Sphere3D_avPositionHandle);
        if(vertexs==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_avPositionHandle, 3, GL_FLOAT, false,0, vertexs);
        OpenGLUtils::checkGLError("Sphere3D_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_afPositionHandle);
        if(texcoords==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_afPositionHandle, 2, GL_FLOAT, false, 0, texcoords);
        OpenGLUtils::checkGLError("Sphere3D_afPositionHandle");


        //球体fbo
        glBindFramebuffer(GL_FRAMEBUFFER, Sphere3D_framebuffe);
        glActiveTexture(GL_TEXTURE0);
        Sphere3D_TextureID = OpenGLUtils::getTextureID();
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, Sphere3D_TextureID);
        OpenGLUtils::checkGLError("glBindTexture");
        glUniform1i(Sphere3D_uTextureSamplerHandle, 0);
        OpenGLUtils::checkGLError("Sphere3D_uTextureSamplerHandle");
        glViewport(0, 0, screenWidth, screenHeight);
        OpenGLUtils::checkGLError("glViewport");
        if(indices==NULL&&mNumIndices==0) {
            return;
        }
        glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_SHORT, indices);
        OpenGLUtils::checkGLError("glDrawElements");

        glBindTexture(GL_TEXTURE_2D, 0);
        OpenGLUtils::checkGLError("glBindTexture1");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        OpenGLUtils::checkGLError("glBindFramebuffer");

        //2d贴图
        glUseProgram(Sphere3D_barreldistortionProgram);


        if (Spheretype == 0) {
            float flag = 0.0f;
            glUniform1f(Sphere3D_barreldistortion_flagrHandle, flag);
            OpenGLUtils::checkGLError("Sphere3D_barreldistortion_flagrHandle");
        } else {
            float flag = 1.0f;
            float value = 0.9f;
            glUniform1f(Sphere3D_barreldistortion_flagrHandle, flag);
            glUniform1f(Sphere3D_barreldistortion_vblackEdgeValueHandle, value);

        }

        glEnableVertexAttribArray(Sphere3D_barreldistortion_avPositionHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_avPositionHandle, 3, GL_FLOAT, false,
                              0, vertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_barreldistortion_aTextureCoordHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_aTextureCoordHandle, 2, GL_FLOAT, false, 0,
                              textureVertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_aTextureCoordHandle");
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, Sphere3D_fbotexture);

        OpenGLUtils::checkGLError("GL_TEXTURE_EXTERNAL_OES");
        glUniform1i(Sphere3D_barreldistortion_uTextureSamplerHandle, 0);

        if (Spheretype == 0) {
            glViewport(0, 0, screenWidth, screenHeight);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            OpenGLUtils::checkGLError("glDrawArrays GL_TRIANGLE_STRIP");
        } else {
           /* glViewport(0,0,screenWidth,screenHeight/2);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            glViewport(0,screenHeight/2,screenWidth,screenHeight/2);*/


            glViewport(0, 0, screenWidth / 2, screenHeight);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            glViewport(screenWidth / 2, 0, screenWidth - screenWidth / 2, screenHeight);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        }
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    else if(Spheretype==2||Spheretype==3)
    {
        //2d贴图
        glUseProgram(Sphere3D_PlaneProgram);

        float screenRatio=(float)1.0f*screenWidth/screenHeight;
        float videoRatio=(float)1.0f*planevideoWidth/planedvideoHeight;

        if (videoRatio>screenRatio)
        {
            Matrix4::orthoM(PlaneMatrix,0,-1.0f,1.0f,1.0f*-videoRatio/screenRatio,1.0f*videoRatio/screenRatio,-1.0f,1.0f);
        }else
        {
            Matrix4::orthoM(PlaneMatrix,0,1.0f*-screenRatio/videoRatio,1.0f*screenRatio/videoRatio,-1.0f,1.0f,-1.0f,1.0f);
        }

        glUniformMatrix4fv(Sphere3D_Plane_uMatrixHandle, 1, false, PlaneMatrix);
        glUniformMatrix4fv(Sphere3D_Plane_uSTMMatrixHandle, 1, false, Matrix);
        OpenGLUtils::checkGLError("Sphere3D_Plane_uSTMMatrixHandle");


        glEnableVertexAttribArray(Sphere3D_Plane_avPositionHandle);
        glVertexAttribPointer(Sphere3D_Plane_avPositionHandle, 3, GL_FLOAT, false,
                              0, planevertexData);
        OpenGLUtils::checkGLError("Sphere3D_Plane_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_Plane_aTextureCoordHandle);
        glVertexAttribPointer(Sphere3D_Plane_aTextureCoordHandle, 2, GL_FLOAT, false, 0,
                              planetextureVertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_aTextureCoordHandle");

        if (Spheretype == 2) {
            float flag = 0.0f;
            glUniform1f(Sphere3D_Plane_flagrHandle, flag);
            OpenGLUtils::checkGLError("Sphere3D_barreldistortion_flagrHandle");
        } else {
            float flag = 1.0f;
            float value = 0.9f;
            glUniform1f(Sphere3D_Plane_flagrHandle, flag);
            glUniform1f(Sphere3D_Plane_vblackEdgeValueHandle, value);

        }

        glActiveTexture(GL_TEXTURE0);
        Sphere3D_TextureID = OpenGLUtils::getTextureID();
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, Sphere3D_TextureID);

        OpenGLUtils::checkGLError("glBindTexture Sphere3D_TextureID");
        glUniform1i(Sphere3D_Plane_uTextureSamplerHandle, 0);

        if (Spheretype == 2) {
            glViewport(0, 0, screenWidth, screenHeight);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            OpenGLUtils::checkGLError("glDrawArrays GL_TRIANGLE_STRIP");
        } else {

            glViewport(0, 0, screenWidth / 2, screenHeight);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            glViewport(screenWidth / 2, 0, screenWidth - screenWidth / 2, screenHeight);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        }
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    else if (Spheretype==5)
    {

        float near=0.1f;
        float far=1000.0f;
        m3dMakePerspectiveMatrix(projectionMatrix,70.0f,1.0f*screenWidth * 0.5f/ screenHeight,near,far);

        if(isUsingTouch==true&&isUsingMotion==true)
        {
            float rotatex[16];
            float rotatey[16];
            m3dLoadIdentity44(rotatex);
            m3dLoadIdentity44(rotatey);
            m3dRotationMatrix44(rotatex, -Sphere3D_fingerY, 1.0f, 0.0f, 0.0f);
            m3dRotationMatrix44(rotatey, -Sphere3D_fingerX, 0.0f, 1.0f, 0.0f);
            m3dMatrixMultiply44(modelMatrix, rotatex, rotatey);
            m3dMatrixMultiply44(modelMatrix, modelMatrix, mSensorMatrix);
        }

        if(isUsingTouch==false&&isUsingMotion==true)
        {
            m3dMatrixMultiply44(modelMatrix, modelMatrix, mSensorMatrix);
        }

        if(isUsingTouch==true&&isUsingMotion==false)
        {
            float rotatex[16];
            float rotatey[16];
            m3dLoadIdentity44(rotatex);
            m3dLoadIdentity44(rotatey);
            m3dRotationMatrix44(rotatex, -Sphere3D_fingerY, 1.0f, 0.0f, 0.0f);
            m3dRotationMatrix44(rotatey, -Sphere3D_fingerX, 0.0f, 1.0f, 0.0f);
            m3dMatrixMultiply44(modelMatrix, rotatex, rotatey);
        }

        m3dMatrixMultiply44(modelViewMatrix,viewMatrixlook,modelMatrix);

        m3dMatrixMultiply44(mMVPMatrix,projectionMatrix,modelViewMatrix);

        glUniformMatrix4fv(Sphere3D_uMatrixHandle, 1, false, mMVPMatrix);

        glUniformMatrix4fv(Sphere3D_uSTMMatrixHandle, 1, false, Matrix);

        //上半球体fbo
        glEnableVertexAttribArray(Sphere3D_avPositionHandle);
        if(upvertexs==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_avPositionHandle, 3, GL_FLOAT, false,0, upvertexs);
        OpenGLUtils::checkGLError("Sphere3D_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_afPositionHandle);
        if(uptexcoords==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_afPositionHandle, 2, GL_FLOAT, false, 0, uptexcoords);
        OpenGLUtils::checkGLError("Sphere3D_afPositionHandle");

        glBindFramebuffer(GL_FRAMEBUFFER, Sphere3D_framebuffe);
        glActiveTexture(GL_TEXTURE0);
        Sphere3D_TextureID = OpenGLUtils::getTextureID();
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, Sphere3D_TextureID);
        OpenGLUtils::checkGLError("glBindTexture");
        glUniform1i(Sphere3D_uTextureSamplerHandle, 0);
        OpenGLUtils::checkGLError("Sphere3D_uTextureSamplerHandle");
        glViewport(0, 0, screenWidth, screenHeight);
        OpenGLUtils::checkGLError("glViewport");
        if(upindices==NULL&&mNumIndices==0) {
            return;
        }
        glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_SHORT, upindices);
        OpenGLUtils::checkGLError("glDrawElements");

        glBindTexture(GL_TEXTURE_2D, 0);
        OpenGLUtils::checkGLError("glBindTexture1");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        OpenGLUtils::checkGLError("glBindFramebuffer");

        //2d贴图
        glUseProgram(Sphere3D_barreldistortionProgram);


        float flag = 1.0f;
        float value = 0.9f;
        glUniform1f(Sphere3D_barreldistortion_flagrHandle, flag);
        glUniform1f(Sphere3D_barreldistortion_vblackEdgeValueHandle, value);


        glEnableVertexAttribArray(Sphere3D_barreldistortion_avPositionHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_avPositionHandle, 3, GL_FLOAT, false,
                              0, vertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_barreldistortion_aTextureCoordHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_aTextureCoordHandle, 2, GL_FLOAT, false, 0,
                              textureVertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_aTextureCoordHandle");
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, Sphere3D_fbotexture);

        OpenGLUtils::checkGLError("GL_TEXTURE_EXTERNAL_OES");
        glUniform1i(Sphere3D_barreldistortion_uTextureSamplerHandle, 0);

        glViewport(0, 0, screenWidth / 2, screenHeight);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindTexture(GL_TEXTURE_2D, 0);


        //下半球体fbo
        glEnableVertexAttribArray(Sphere3D_avPositionHandle);
        if(downvertexs==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_avPositionHandle, 3, GL_FLOAT, false,0, downvertexs);
        OpenGLUtils::checkGLError("Sphere3D_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_afPositionHandle);
        if(downtexcoords==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_afPositionHandle, 2, GL_FLOAT, false, 0, downtexcoords);
        OpenGLUtils::checkGLError("Sphere3D_afPositionHandle");

        glBindFramebuffer(GL_FRAMEBUFFER, Sphere3D_framebuffe);
        glActiveTexture(GL_TEXTURE0);
        Sphere3D_TextureID = OpenGLUtils::getTextureID();
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, Sphere3D_TextureID);
        OpenGLUtils::checkGLError("glBindTexture");
        glUniform1i(Sphere3D_uTextureSamplerHandle, 0);
        OpenGLUtils::checkGLError("Sphere3D_uTextureSamplerHandle");
        glViewport(0, 0, screenWidth, screenHeight);
        OpenGLUtils::checkGLError("glViewport");
        if(downindices==NULL&&mNumIndices==0) {
            return;
        }
        glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_SHORT, downindices);
        OpenGLUtils::checkGLError("glDrawElements");

        glBindTexture(GL_TEXTURE_2D, 0);
        OpenGLUtils::checkGLError("glBindTexture1");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        OpenGLUtils::checkGLError("glBindFramebuffer");

        //2d贴图
        glUseProgram(Sphere3D_barreldistortionProgram);


        glUniform1f(Sphere3D_barreldistortion_flagrHandle, flag);
        glUniform1f(Sphere3D_barreldistortion_vblackEdgeValueHandle, value);


        glEnableVertexAttribArray(Sphere3D_barreldistortion_avPositionHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_avPositionHandle, 3, GL_FLOAT, false,
                              0, vertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_barreldistortion_aTextureCoordHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_aTextureCoordHandle, 2, GL_FLOAT, false, 0,
                              textureVertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_aTextureCoordHandle");
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, Sphere3D_fbotexture);

        OpenGLUtils::checkGLError("GL_TEXTURE_EXTERNAL_OES");
        glUniform1i(Sphere3D_barreldistortion_uTextureSamplerHandle, 0);

        glViewport(screenWidth / 2, 0, screenWidth - screenWidth / 2, screenHeight);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindTexture(GL_TEXTURE_2D, 0);
    }else if(Spheretype==4)
    {
       // m3dfrustumM(projectionMatrix,0,1.0f*-screenRatio/2,1.0f*screenRatio/2,-0.5f,0.5f,0.4f,500.f);//全屏模式

        float near=0.1f;
        float far=1000.0f;

        m3dMakePerspectiveMatrix(projectionMatrix,75.0f,1.0f*screenWidth/ screenHeight,near,far);

        if(isUsingTouch==true&&isUsingMotion==true)
        {
            float rotatex[16];
            float rotatey[16];
            m3dLoadIdentity44(rotatex);
            m3dLoadIdentity44(rotatey);
            m3dRotationMatrix44(rotatex, -Sphere3D_fingerY, 1.0f, 0.0f, 0.0f);
            m3dRotationMatrix44(rotatey, -Sphere3D_fingerX, 0.0f, 1.0f, 0.0f);
            m3dMatrixMultiply44(modelMatrix, rotatex, rotatey);
            m3dMatrixMultiply44(modelMatrix, modelMatrix, mSensorMatrix);
        }

        if(isUsingTouch==false&&isUsingMotion==true)
        {
            m3dMatrixMultiply44(modelMatrix, modelMatrix, mSensorMatrix);
        }

        if(isUsingTouch==true&&isUsingMotion==false)
        {
            float rotatex[16];
            float rotatey[16];
            m3dLoadIdentity44(rotatex);
            m3dLoadIdentity44(rotatey);
            m3dRotationMatrix44(rotatex, -Sphere3D_fingerY, 1.0f, 0.0f, 0.0f);
            m3dRotationMatrix44(rotatey, -Sphere3D_fingerX, 0.0f, 1.0f, 0.0f);
            m3dMatrixMultiply44(modelMatrix, rotatex, rotatey);
        }

        m3dMatrixMultiply44(modelViewMatrix,viewMatrixlook,modelMatrix);

        m3dMatrixMultiply44(mMVPMatrix,projectionMatrix,modelViewMatrix);

        glUniformMatrix4fv(Sphere3D_uMatrixHandle, 1, false, mMVPMatrix);

        glUniformMatrix4fv(Sphere3D_uSTMMatrixHandle, 1, false, Matrix);

        //上半球体fbo
        glEnableVertexAttribArray(Sphere3D_avPositionHandle);
        if(upvertexs==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_avPositionHandle, 3, GL_FLOAT, false,0, upvertexs);
        OpenGLUtils::checkGLError("Sphere3D_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_afPositionHandle);
        if(uptexcoords==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_afPositionHandle, 2, GL_FLOAT, false, 0, uptexcoords);
        OpenGLUtils::checkGLError("Sphere3D_afPositionHandle");

        glBindFramebuffer(GL_FRAMEBUFFER, Sphere3D_framebuffe);
        glActiveTexture(GL_TEXTURE0);
        Sphere3D_TextureID = OpenGLUtils::getTextureID();
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, Sphere3D_TextureID);
        OpenGLUtils::checkGLError("glBindTexture");
        glUniform1i(Sphere3D_uTextureSamplerHandle, 0);
        OpenGLUtils::checkGLError("Sphere3D_uTextureSamplerHandle");
        glViewport(0, 0, screenWidth, screenHeight);
        OpenGLUtils::checkGLError("glViewport");
        if(upindices==NULL&&mNumIndices==0) {
            return;
        }
        glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_SHORT, upindices);
        OpenGLUtils::checkGLError("glDrawElements");

        glBindTexture(GL_TEXTURE_2D, 0);
        OpenGLUtils::checkGLError("glBindTexture1");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        OpenGLUtils::checkGLError("glBindFramebuffer");

        //2d贴图
        glUseProgram(Sphere3D_barreldistortionProgram);


        float flag = 0.0f;
        float value = 0.9f;
        glUniform1f(Sphere3D_barreldistortion_flagrHandle, flag);
        glUniform1f(Sphere3D_barreldistortion_vblackEdgeValueHandle, value);


        glEnableVertexAttribArray(Sphere3D_barreldistortion_avPositionHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_avPositionHandle, 3, GL_FLOAT, false,
                              0, vertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_barreldistortion_aTextureCoordHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_aTextureCoordHandle, 2, GL_FLOAT, false, 0,
                              textureVertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_aTextureCoordHandle");
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, Sphere3D_fbotexture);

        OpenGLUtils::checkGLError("GL_TEXTURE_EXTERNAL_OES");
        glUniform1i(Sphere3D_barreldistortion_uTextureSamplerHandle, 0);

        glViewport(0, 0, screenWidth , screenHeight);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindTexture(GL_TEXTURE_2D, 0);
    }else if(Spheretype==6)
    {
        float near=0.1f;
        float far=1000.0f;

        m3dMakePerspectiveMatrix(projectionMatrix,75.0f,1.0f*screenWidth/ screenHeight,near,far);
        if(isUsingTouch==true&&isUsingMotion==true)
        {
            float rotatex[16];
            float rotatey[16];
            m3dLoadIdentity44(rotatex);
            m3dLoadIdentity44(rotatey);
            m3dRotationMatrix44(rotatex, -Sphere3D_fingerY, 1.0f, 0.0f, 0.0f);
            m3dRotationMatrix44(rotatey, -Sphere3D_fingerX, 0.0f, 1.0f, 0.0f);
            m3dMatrixMultiply44(modelMatrix, rotatex, rotatey);
            m3dMatrixMultiply44(modelMatrix, modelMatrix, mSensorMatrix);
        }

        if(isUsingTouch==false&&isUsingMotion==true)
        {
            m3dMatrixMultiply44(modelMatrix, modelMatrix, mSensorMatrix);
        }

        if(isUsingTouch==true&&isUsingMotion==false)
        {
            float rotatex[16];
            float rotatey[16];
            m3dLoadIdentity44(rotatex);
            m3dLoadIdentity44(rotatey);
            m3dRotationMatrix44(rotatex, -Sphere3D_fingerY, 1.0f, 0.0f, 0.0f);
            m3dRotationMatrix44(rotatey, -Sphere3D_fingerX, 0.0f, 1.0f, 0.0f);
            m3dMatrixMultiply44(modelMatrix, rotatex, rotatey);
        }
        m3dMatrixMultiply44(modelViewMatrix,viewMatrixlook,modelMatrix);

        m3dMatrixMultiply44(mMVPMatrix,projectionMatrix,modelViewMatrix);

        glUniformMatrix4fv(Sphere3D_uMatrixHandle, 1, false, mMVPMatrix);

        glUniformMatrix4fv(Sphere3D_uSTMMatrixHandle, 1, false, Matrix);

        //上半球体fbo
        glEnableVertexAttribArray(Sphere3D_avPositionHandle);
        if(leftvertexs==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_avPositionHandle, 3, GL_FLOAT, false,0, leftvertexs);
        OpenGLUtils::checkGLError("Sphere3D_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_afPositionHandle);
        if(lefttexcoords==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_afPositionHandle, 2, GL_FLOAT, false, 0, lefttexcoords);
        OpenGLUtils::checkGLError("Sphere3D_afPositionHandle");

        glBindFramebuffer(GL_FRAMEBUFFER, Sphere3D_framebuffe);
        glActiveTexture(GL_TEXTURE0);
        Sphere3D_TextureID = OpenGLUtils::getTextureID();
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, Sphere3D_TextureID);
        OpenGLUtils::checkGLError("glBindTexture");
        glUniform1i(Sphere3D_uTextureSamplerHandle, 0);
        OpenGLUtils::checkGLError("Sphere3D_uTextureSamplerHandle");
        glViewport(0, 0, screenWidth, screenHeight);
        OpenGLUtils::checkGLError("glViewport");
        if(leftindices==NULL&&mNumIndices==0) {
            return;
        }
        glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_SHORT, leftindices);
        OpenGLUtils::checkGLError("glDrawElements");

        glBindTexture(GL_TEXTURE_2D, 0);
        OpenGLUtils::checkGLError("glBindTexture1");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        OpenGLUtils::checkGLError("glBindFramebuffer");

        //2d贴图
        glUseProgram(Sphere3D_barreldistortionProgram);


        float flag = 0.0f;
        float value = 0.9f;
        glUniform1f(Sphere3D_barreldistortion_flagrHandle, flag);
        glUniform1f(Sphere3D_barreldistortion_vblackEdgeValueHandle, value);


        glEnableVertexAttribArray(Sphere3D_barreldistortion_avPositionHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_avPositionHandle, 3, GL_FLOAT, false,
                              0, vertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_barreldistortion_aTextureCoordHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_aTextureCoordHandle, 2, GL_FLOAT, false, 0,
                              textureVertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_aTextureCoordHandle");
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, Sphere3D_fbotexture);

        OpenGLUtils::checkGLError("GL_TEXTURE_EXTERNAL_OES");
        glUniform1i(Sphere3D_barreldistortion_uTextureSamplerHandle, 0);

        glViewport(0, 0, screenWidth , screenHeight);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    else if(Spheretype==7)
    {
        float near=0.1f;
        float far=1000.0f;
        m3dMakePerspectiveMatrix(projectionMatrix,70.0f,1.0f*screenWidth * 0.5f/ screenHeight,near,far);

        if(isUsingTouch==true&&isUsingMotion==true)
        {
            float rotatex[16];
            float rotatey[16];
            m3dLoadIdentity44(rotatex);
            m3dLoadIdentity44(rotatey);
            m3dRotationMatrix44(rotatex, -Sphere3D_fingerY, 1.0f, 0.0f, 0.0f);
            m3dRotationMatrix44(rotatey, -Sphere3D_fingerX, 0.0f, 1.0f, 0.0f);
            m3dMatrixMultiply44(modelMatrix, rotatex, rotatey);
            m3dMatrixMultiply44(modelMatrix, modelMatrix, mSensorMatrix);
        }

        if(isUsingTouch==false&&isUsingMotion==true)
        {
            m3dMatrixMultiply44(modelMatrix, modelMatrix, mSensorMatrix);
        }

        if(isUsingTouch==true&&isUsingMotion==false)
        {
            float rotatex[16];
            float rotatey[16];
            m3dLoadIdentity44(rotatex);
            m3dLoadIdentity44(rotatey);
            m3dRotationMatrix44(rotatex, -Sphere3D_fingerY, 1.0f, 0.0f, 0.0f);
            m3dRotationMatrix44(rotatey, -Sphere3D_fingerX, 0.0f, 1.0f, 0.0f);
            m3dMatrixMultiply44(modelMatrix, rotatex, rotatey);
        }

        m3dMatrixMultiply44(modelViewMatrix,viewMatrixlook,modelMatrix);

        m3dMatrixMultiply44(mMVPMatrix,projectionMatrix,modelViewMatrix);

        glUniformMatrix4fv(Sphere3D_uMatrixHandle, 1, false, mMVPMatrix);

        glUniformMatrix4fv(Sphere3D_uSTMMatrixHandle, 1, false, Matrix);

        //上半球体fbo
        glEnableVertexAttribArray(Sphere3D_avPositionHandle);
        if(leftvertexs==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_avPositionHandle, 3, GL_FLOAT, false,0, leftvertexs);
        OpenGLUtils::checkGLError("Sphere3D_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_afPositionHandle);
        if(lefttexcoords==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_afPositionHandle, 2, GL_FLOAT, false, 0, lefttexcoords);
        OpenGLUtils::checkGLError("Sphere3D_afPositionHandle");

        glBindFramebuffer(GL_FRAMEBUFFER, Sphere3D_framebuffe);
        glActiveTexture(GL_TEXTURE0);
        Sphere3D_TextureID = OpenGLUtils::getTextureID();
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, Sphere3D_TextureID);
        OpenGLUtils::checkGLError("glBindTexture");
        glUniform1i(Sphere3D_uTextureSamplerHandle, 0);
        OpenGLUtils::checkGLError("Sphere3D_uTextureSamplerHandle");
        glViewport(0, 0, screenWidth, screenHeight);
        OpenGLUtils::checkGLError("glViewport");
        if(leftindices==NULL&&mNumIndices==0) {
            return;
        }
        glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_SHORT, leftindices);
        OpenGLUtils::checkGLError("glDrawElements");

        glBindTexture(GL_TEXTURE_2D, 0);
        OpenGLUtils::checkGLError("glBindTexture1");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        OpenGLUtils::checkGLError("glBindFramebuffer");

        //2d贴图
        glUseProgram(Sphere3D_barreldistortionProgram);


        float flag = 1.0f;
        float value = 0.9f;
        glUniform1f(Sphere3D_barreldistortion_flagrHandle, flag);
        glUniform1f(Sphere3D_barreldistortion_vblackEdgeValueHandle, value);


        glEnableVertexAttribArray(Sphere3D_barreldistortion_avPositionHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_avPositionHandle, 3, GL_FLOAT, false,
                              0, vertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_barreldistortion_aTextureCoordHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_aTextureCoordHandle, 2, GL_FLOAT, false, 0,
                              textureVertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_aTextureCoordHandle");
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, Sphere3D_fbotexture);

        OpenGLUtils::checkGLError("GL_TEXTURE_EXTERNAL_OES");
        glUniform1i(Sphere3D_barreldistortion_uTextureSamplerHandle, 0);

        glViewport(0, 0, screenWidth / 2, screenHeight);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindTexture(GL_TEXTURE_2D, 0);


        //下半球体fbo
        glEnableVertexAttribArray(Sphere3D_avPositionHandle);
        if(rightvertexs==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_avPositionHandle, 3, GL_FLOAT, false,0, rightvertexs);
        OpenGLUtils::checkGLError("Sphere3D_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_afPositionHandle);
        if(righttexcoords==NULL) {
            return;
        }
        glVertexAttribPointer(Sphere3D_afPositionHandle, 2, GL_FLOAT, false, 0, righttexcoords);
        OpenGLUtils::checkGLError("Sphere3D_afPositionHandle");

        glBindFramebuffer(GL_FRAMEBUFFER, Sphere3D_framebuffe);
        glActiveTexture(GL_TEXTURE0);
        Sphere3D_TextureID = OpenGLUtils::getTextureID();
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, Sphere3D_TextureID);
        OpenGLUtils::checkGLError("glBindTexture");
        glUniform1i(Sphere3D_uTextureSamplerHandle, 0);
        OpenGLUtils::checkGLError("Sphere3D_uTextureSamplerHandle");
        glViewport(0, 0, screenWidth, screenHeight);
        OpenGLUtils::checkGLError("glViewport");
        if(rightindices==NULL&&mNumIndices==0) {
            return;
        }
        glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_SHORT, rightindices);
        OpenGLUtils::checkGLError("glDrawElements");

        glBindTexture(GL_TEXTURE_2D, 0);
        OpenGLUtils::checkGLError("glBindTexture1");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        OpenGLUtils::checkGLError("glBindFramebuffer");

        //2d贴图
        glUseProgram(Sphere3D_barreldistortionProgram);


        glUniform1f(Sphere3D_barreldistortion_flagrHandle, flag);
        glUniform1f(Sphere3D_barreldistortion_vblackEdgeValueHandle, value);

        glEnableVertexAttribArray(Sphere3D_barreldistortion_avPositionHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_avPositionHandle, 3, GL_FLOAT, false,
                              0, vertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_avPositionHandle");
        glEnableVertexAttribArray(Sphere3D_barreldistortion_aTextureCoordHandle);
        glVertexAttribPointer(Sphere3D_barreldistortion_aTextureCoordHandle, 2, GL_FLOAT, false, 0,
                              textureVertexData);
        OpenGLUtils::checkGLError("Sphere3D_barreldistortion_aTextureCoordHandle");
        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, Sphere3D_fbotexture);

        OpenGLUtils::checkGLError("GL_TEXTURE_EXTERNAL_OES");
        glUniform1i(Sphere3D_barreldistortion_uTextureSamplerHandle, 0);

        glViewport(screenWidth / 2, 0, screenWidth - screenWidth / 2, screenHeight);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
#else
    glActiveTexture(GL_TEXTURE0);
    Sphere3D_TextureID=OpenGLUtils::getTextureID();
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, Sphere3D_TextureID);
    OpenGLUtils::checkGLError("glBindTexture");
    glUniform1i(Sphere3D_uTextureSamplerHandle, 0);
    OpenGLUtils::checkGLError("Sphere3D_uTextureSamplerHandle");
    glViewport(0,0,screenWidth,screenHeight);
    OpenGLUtils::checkGLError("glViewport");
    glDrawElements(GL_TRIANGLES, mNumIndices,GL_UNSIGNED_SHORT, indices);
    OpenGLUtils::checkGLError("glDrawElements");
}
#endif
}



void Sphere3D::perspective(int type,float projectionMatrix[16])
{
#if 0
    const double DEG2RAD = 3.14159265 / 180;
    float fov;
    float aspect;
    float near=0.1f;
    float far=1000.0f;
    float _width=1.0f*getCuurenScreenWidth();
    float _height=1.0f*getCurrenScreenHeight();

    Sphere3D::swapt(&_width, &_height);


        if(type==0)//全屏
        {
            if(getCurrenOrientation()==1||getCurrenOrientation()==2)
            {
            aspect = 1.0f*_width/_height;
            }
            else
            {
            // TODO
            aspect = 1.0f*_height/_width;
            //            aspect = 1.0*getCurrenScreenHeight()/getCuurenScreenWidth();
            }

            fov = 75.0f;
        }
        else//半屏
        {
            if(getCurrenOrientation()==1||getCurrenOrientation()==2)
            {
            aspect = 1.0f*(_width * 0.5f)/_height;
            }
            else
            {
            aspect = 1.0f* _height/(_width * 0.5f);
            }

            fov = 70.0f;
        }
        float tangent = tanf(fov / 2.f * DEG2RAD);   // tangent of half fovY
        float height = near * tangent;           // half height of near plane
        float width = height * aspect;       // half width of near plane

        float l = -width, r = width, b = -height, t = height, n = near, f = far;
        // params: left, right, bottom, top, near, far
        projectionMatrix[0]  =  2 * n / (r - l);
        projectionMatrix[5]  =  2 * n / (t - b);
        projectionMatrix[8]  =  (r + l) / (r - l);
        projectionMatrix[9]  =  (t + b) / (t - b);
        projectionMatrix[10] = -(f + n) / (f - n);
        projectionMatrix[11] = -1;
        projectionMatrix[14] = -(2 * f * n) / (f - n);
        projectionMatrix[15] =  0;
#endif
}

//球体
void Sphere3D::createSphere(float radius, int rings, int sectors)
{
    float PI = M3D_PI;

    float nowradius=150.0f;
    int longtitude=75;
    int latitude=150;

    int numPoints = longtitude * (latitude+1) * 3;
    int numTexcoords = longtitude * (latitude+1) * 2;
    int numIndices = (longtitude-1) * latitude * 6;

    vertexs = (float* )malloc ( sizeof(float) * numPoints);
    texcoords =  (float* )malloc ( sizeof(float) * numTexcoords);
    indices = (short* )malloc ( sizeof(short) * numIndices);

    int t = 0, v = 0, counter = 0;;
    float theta = 0.0f, phi = 0.0f;
    float longtiRatio = 1.0f;
    float latiRatio =2.0f;

    for(int i = 0; i < longtitude; i++) {
        phi =(PI/2-i/(longtitude-1+0.0)*PI)*longtiRatio;

        for(int j = 0; j < latitude+1; j++) {

            theta =(j/(latitude+0.0)*PI-PI/2)*latiRatio;
            float r = -nowradius *(float)cosf(phi);

            float x = r * (float)sinf(theta);
            float y = nowradius * (float)sinf(phi);
            float z = r * (float)cosf(theta);

            vertexs[v++] = x;  // X-axis
            vertexs[v++] = y;  // Y-axis;
            vertexs[v++] = z; // Z-axis

            // point *pos = [[point alloc] init];
            // pos.vertexPoint = GLKVector3Make(x, y, z);
            //[object3D.vectorArr addObject:pos];
            texcoords[t++] = 1.0f-(float) ((j + 0.0) / (latitude + 0.0));//:((float) ((j + 0.0) / (latitude + 0.0))); // x-axis
            texcoords[t++] = (float)((i+0.0)/(longtitude-1.0)); //y-axis
        }
    }

    for(int i = 0; i<longtitude-1; i++){
        for(int j= 0; j<latitude; j++)
        {
            //first triangle
            indices[counter++] = (short)(i*(latitude+1)+ j); //upper point
            indices[counter++] = (short) (i*(latitude+1)+j+1); // upper-right point
            indices[counter++] = (short) ((i+1)*(latitude+1) +j); // lower point

            //second triangle
            indices[counter++] = (short) ((i+1)*(latitude+1) +j);
            indices[counter++] = (short) (i*(latitude+1)+j+1);
            indices[counter++] = (short) ((i+1)*(latitude+1)+j+1); // lower-right point
        }
    }
    mNumIndices=numIndices;
}
//左半球

void Sphere3D::createLeftSphere(float radius, int rings, int sectors)
{
    float PI = M3D_PI;

    float nowradius=150.0f;
    int longtitude=75;
    int latitude=150;

    int numPoints = longtitude * (latitude+1) * 3;
    int numTexcoords = longtitude * (latitude+1) * 2;
    int numIndices = (longtitude-1) * latitude * 6;

    leftvertexs = (float* )malloc ( sizeof(float) * numPoints);
    lefttexcoords =  (float* )malloc ( sizeof(float) * numTexcoords);
    leftindices = (short* )malloc ( sizeof(short) * numIndices);

    int t = 0, v = 0, counter = 0;;
    float theta = 0.0f, phi = 0.0f;
    float longtiRatio = 1.0f;
    float latiRatio =2.0f;

    for(int i = 0; i < longtitude; i++) {
        phi =(PI/2-i/(longtitude-1+0.0)*PI)*longtiRatio;

        for(int j = 0; j < latitude+1; j++) {

            theta =(j/(latitude+0.0)*PI-PI/2)*latiRatio;
            float r = -nowradius *(float)cosf(phi);

            float x = r * (float)sinf(theta);
            float y = nowradius * (float)sinf(phi);
            float z = r * (float)cosf(theta);

            leftvertexs[v++] = x;  // X-axis
            leftvertexs[v++] = y;  // Y-axis;
            leftvertexs[v++] = z; // Z-axis

            // point *pos = [[point alloc] init];
            // pos.vertexPoint = GLKVector3Make(x, y, z);
            //[object3D.vectorArr addObject:pos];
            lefttexcoords[t++] = 0.5f - ((float) ((j + 0.0) / (latitude - 1.0)) * 0.5f); // x-axis
            lefttexcoords[t++] = (float) ((i + 0.0) / (longtitude - 1.0));
        }
    }

    for(int i = 0; i<longtitude-1; i++){
        for(int j= 0; j<latitude; j++)
        {
            //first triangle
            leftindices[counter++] = (short)(i*(latitude+1)+ j); //upper point
            leftindices[counter++] = (short) (i*(latitude+1)+j+1); // upper-right point
            leftindices[counter++] = (short) ((i+1)*(latitude+1) +j); // lower point

            //second triangle
            leftindices[counter++] = (short) ((i+1)*(latitude+1) +j);
            leftindices[counter++] = (short) (i*(latitude+1)+j+1);
            leftindices[counter++] = (short) ((i+1)*(latitude+1)+j+1); // lower-right point
        }
    }
    mNumIndices=numIndices;
}

//右半球

void Sphere3D::createRightSphere(float radius, int rings, int sectors)
{
    float PI = M3D_PI;

    float nowradius=150.0f;
    int longtitude=75;
    int latitude=150;

    int numPoints = longtitude * (latitude+1) * 3;
    int numTexcoords = longtitude * (latitude+1) * 2;
    int numIndices = (longtitude-1) * latitude * 6;

    rightvertexs = (float* )malloc ( sizeof(float) * numPoints);
    righttexcoords =  (float* )malloc ( sizeof(float) * numTexcoords);
    rightindices = (short* )malloc ( sizeof(short) * numIndices);

    int t = 0, v = 0, counter = 0;;
    float theta = 0.0f, phi = 0.0f;
    float longtiRatio = 1.0f;
    float latiRatio =2.0f;

    for(int i = 0; i < longtitude; i++) {
        phi =(PI/2-i/(longtitude-1+0.0)*PI)*longtiRatio;

        for(int j = 0; j < latitude+1; j++) {

            theta =(j/(latitude+0.0)*PI-PI/2)*latiRatio;
            float r = -nowradius *(float)cosf(phi);

            float x = r * (float)sinf(theta);
            float y = nowradius * (float)sinf(phi);
            float z = r * (float)cosf(theta);

            rightvertexs[v++] = x;  // X-axis
            rightvertexs[v++] = y;  // Y-axis;
            rightvertexs[v++] = z; // Z-axis

            // point *pos = [[point alloc] init];
            // pos.vertexPoint = GLKVector3Make(x, y, z);
            //[object3D.vectorArr addObject:pos];
            righttexcoords[t++] = 1.5f - (0.5f + (float) ((j + 0.0) / (latitude - 1.0)) * 0.5f); // x-axis
            righttexcoords[t++] = (float) ((i + 0.0) / (longtitude - 1.0));
        }
    }

    for(int i = 0; i<longtitude-1; i++){
        for(int j= 0; j<latitude; j++)
        {
            //first triangle
            rightindices[counter++] = (short)(i*(latitude+1)+ j); //upper point
            rightindices[counter++] = (short) (i*(latitude+1)+j+1); // upper-right point
            rightindices[counter++] = (short) ((i+1)*(latitude+1) +j); // lower point

            //second triangle
            rightindices[counter++] = (short) ((i+1)*(latitude+1) +j);
            rightindices[counter++] = (short) (i*(latitude+1)+j+1);
            rightindices[counter++] = (short) ((i+1)*(latitude+1)+j+1); // lower-right point
        }
    }
    mNumIndices=numIndices;
}

//下半球体
void Sphere3D::createDownSphere(float radius, int rings, int sectors)
{
    float PI = M3D_PI;

    float nowradius=150.0f;
    int longtitude=75;
    int latitude=150;

    int numPoints = longtitude * (latitude+1) * 3;
    int numTexcoords = longtitude * (latitude+1) * 2;
    int numIndices = (longtitude-1) * latitude * 6;

    downvertexs = (float* )malloc ( sizeof(float) * numPoints);
    downtexcoords =  (float* )malloc ( sizeof(float) * numTexcoords);
    downindices = (short* )malloc ( sizeof(short) * numIndices);

    int t = 0, v = 0, counter = 0;;
    float theta = 0.0f, phi = 0.0f;
    float longtiRatio = 1.0f;
    float latiRatio =2.0f;

    for(int i = 0; i < longtitude; i++) {
        phi =(PI/2-i/(longtitude-1+0.0)*PI)*longtiRatio;

        for(int j = 0; j < latitude+1; j++) {

            theta =(j/(latitude+0.0)*PI-PI/2)*latiRatio;
            float r = -nowradius *(float)cosf(phi);

            float x = r * (float)sinf(theta);
            float y = nowradius * (float)sinf(phi);
            float z = r * (float)cosf(theta);

            downvertexs[v++] = x;  // X-axis
            downvertexs[v++] = y;  // Y-axis;
            downvertexs[v++] = z; // Z-axis

            // point *pos = [[point alloc] init];
            // pos.vertexPoint = GLKVector3Make(x, y, z);
            //[object3D.vectorArr addObject:pos];
            downtexcoords[t++] = 1.0f-(float) ((j + 0.0) / (latitude + 0.0));//:((float) ((j + 0.0) / (latitude + 0.0))); // x-axis
            downtexcoords[t++] = 0.5f + (float) ((i + 0.0) / (longtitude - 1.0)) * 0.5f; // y-axis
        }
    }

    for(int i = 0; i<longtitude-1; i++){
        for(int j= 0; j<latitude; j++)
        {
            //first triangle
            downindices[counter++] = (short)(i*(latitude+1)+ j); //upper point
            downindices[counter++] = (short) (i*(latitude+1)+j+1); // upper-right point
            downindices[counter++] = (short) ((i+1)*(latitude+1) +j); // lower point

            //second triangle
            downindices[counter++] = (short) ((i+1)*(latitude+1) +j);
            downindices[counter++] = (short) (i*(latitude+1)+j+1);
            downindices[counter++] = (short) ((i+1)*(latitude+1)+j+1); // lower-right point
        }
    }
    mNumIndices=numIndices;
}

//上半球体
void Sphere3D::createUpSphere(float radius, int rings, int sectors)
{
    float PI = M3D_PI;

    float nowradius=150.0f;
    int longtitude=75;
    int latitude=150;

    int numPoints = longtitude * (latitude+1) * 3;
    int numTexcoords = longtitude * (latitude+1) * 2;
    int numIndices = (longtitude-1) * latitude * 6;

    upvertexs = (float* )malloc ( sizeof(float) * numPoints);
    uptexcoords =  (float* )malloc ( sizeof(float) * numTexcoords);
    upindices = (short* )malloc ( sizeof(short) * numIndices);

    int t = 0, v = 0, counter = 0;;
    float theta = 0.0f, phi = 0.0f;
    float longtiRatio = 1.0f;
    float latiRatio =2.0f;

    for(int i = 0; i < longtitude; i++) {
        phi =(PI/2-i/(longtitude-1+0.0)*PI)*longtiRatio;

        for(int j = 0; j < latitude+1; j++) {

            theta =(j/(latitude+0.0)*PI-PI/2)*latiRatio;
            float r = -nowradius *(float)cosf(phi);

            float x = r * (float)sinf(theta);
            float y = nowradius * (float)sinf(phi);
            float z = r * (float)cosf(theta);

            upvertexs[v++] = x;  // X-axis
            upvertexs[v++] = y;  // Y-axis;
            upvertexs[v++] = z; // Z-axis

            // point *pos = [[point alloc] init];
            // pos.vertexPoint = GLKVector3Make(x, y, z);
            //[object3D.vectorArr addObject:pos];
            uptexcoords[t++] = 1.0f-(float) ((j + 0.0) / (latitude + 0.0));//:((float) ((j + 0.0) / (latitude + 0.0))); // x-axis
            uptexcoords[t++] =(float) ((i + 0.0) / (longtitude - 1.0)) * 0.5f; // y-axis
        }
    }

    for(int i = 0; i<longtitude-1; i++){
        for(int j= 0; j<latitude; j++)
        {
            //first triangle
            upindices[counter++] = (short)(i*(latitude+1)+ j); //upper point
            upindices[counter++] = (short) (i*(latitude+1)+j+1); // upper-right point
            upindices[counter++] = (short) ((i+1)*(latitude+1) +j); // lower point

            //second triangle
            upindices[counter++] = (short) ((i+1)*(latitude+1) +j);
            upindices[counter++] = (short) (i*(latitude+1)+j+1);
            upindices[counter++] = (short) ((i+1)*(latitude+1)+j+1); // lower-right point
        }
    }
    mNumIndices=numIndices;
}

void Sphere3D::SurfaceChanged(int width, int height)
{
    screenWidth=width; screenHeight=height;

   // float ratio=(float)width/height;
   // m3dMakePerspectiveMatrix(projectionMatrix, 90, ratio/2, 1.0f, 500.0f);
    Vector3 eye;
    Vector3 center;
    Vector3 up;

    eye=Vector3(0.0f,0.0f,0.0f);
    center=Vector3(0.0f,0.0f,-1.0f);
    up=Vector3(0.0f,1.0f,0.0f);
    Matrix4::newlookAt(viewMatrixlook,eye,center,up);//ok
}

void Sphere3D::SetSensor(float *Matrix)
{
    for(int i=0;i<16;i++)
    {
        mSensorMatrix[i]=Matrix[i];
    }
}

void Sphere3D::SetFingerRotation(float fingerX,float fingerY)
{
    Sphere3D_fingerX=fingerX;
    Sphere3D_fingerY=fingerY;
}

void Sphere3D::SetSpheretype(int type)
{
    ALOGE("1111111111 SetSpheretype Spheretype:%d",type);
    Spheretype=type;
    Sphere3D::change(type);
}

void Sphere3D::change(int type)
{
    if(vertexs!=NULL)
    {
        free(vertexs);
        vertexs=NULL;
    }

    if(texcoords!=NULL)
    {
        free(texcoords);
        texcoords=NULL;
    }

    if(indices!=NULL)
    {
        free(indices);
        indices=NULL;
    }

    if(upvertexs!=NULL)
    {
        free(upvertexs);
        upvertexs=NULL;
    }

    if(uptexcoords!=NULL)
    {
        free(uptexcoords);
        uptexcoords=NULL;
    }

    if(upindices!=NULL)
    {
        free(upindices);
        upindices=NULL;
    }

    if(downvertexs!=NULL)
    {
        free(downvertexs);
        downvertexs=NULL;
    }

    if(downtexcoords!=NULL)
    {
        free(downtexcoords);
        downtexcoords=NULL;
    }

    if(downindices!=NULL)
    {
        free(downindices);
        downindices=NULL;
    }

    if(leftvertexs!=NULL)
    {
        free(leftvertexs);
        leftvertexs=NULL;
    }

    if(lefttexcoords!=NULL)
    {
        free(lefttexcoords);
        lefttexcoords=NULL;
    }

    if(leftindices!=NULL)
    {
        free(leftindices);
        leftindices=NULL;
    }

    if(rightvertexs!=NULL)
    {
        free(rightvertexs);
        rightvertexs=NULL;
    }

    if(righttexcoords!=NULL)
    {
        free(righttexcoords);
        righttexcoords=NULL;
    }

    if(rightindices!=NULL)
    {
        free(rightindices);
        rightindices=NULL;
    }

    mNumIndices=0;

    if(Spheretype==0||Spheretype==1)
    {
        Sphere3D::createSphere(18,75,150);
    }
    else if(Spheretype==4)//原视频为上下两层全屏显示
    {
        Sphere3D::createUpSphere(18,75,150);
    }
    else if(Spheretype==5)//原视频为上下两层 左右显示
    {
        Sphere3D::createUpSphere(18,75,150);
        Sphere3D::createDownSphere(18,75,150);
    }
    else if(Spheretype==6)//原视频为左右两边全屏显示
    {
        Sphere3D::createLeftSphere(18,75,150);
    }
    else if(Spheretype==7)//原视频为左右两边左右显示
    {
        Sphere3D::createLeftSphere(18,75,150);
        Sphere3D::createRightSphere(18,75,150);
    }

}

void Sphere3D::destroy()
{
    if(vertexs!=NULL)
    {
        free(vertexs);
        vertexs=NULL;
    }

    if(texcoords!=NULL)
    {
        free(texcoords);
        texcoords=NULL;
    }

    if(indices!=NULL)
    {
        free(indices);
        indices=NULL;
    }

    if(upvertexs!=NULL)
    {
        free(upvertexs);
        upvertexs=NULL;
    }

    if(uptexcoords!=NULL)
    {
        free(uptexcoords);
        uptexcoords=NULL;
    }

    if(upindices!=NULL)
    {
        free(upindices);
        upindices=NULL;
    }

    if(downvertexs!=NULL)
    {
        free(downvertexs);
        downvertexs=NULL;
    }

    if(downtexcoords!=NULL)
    {
        free(downtexcoords);
        downtexcoords=NULL;
    }

    if(downindices!=NULL)
    {
        free(downindices);
        downindices=NULL;
    }

    if(leftvertexs!=NULL)
    {
        free(leftvertexs);
        leftvertexs=NULL;
    }

    if(lefttexcoords!=NULL)
    {
        free(lefttexcoords);
        lefttexcoords=NULL;
    }

    if(leftindices!=NULL)
    {
        free(leftindices);
        leftindices=NULL;
    }

    if(rightvertexs!=NULL)
    {
        free(rightvertexs);
        rightvertexs=NULL;
    }

    if(righttexcoords!=NULL)
    {
        free(righttexcoords);
        righttexcoords=NULL;
    }

    if(rightindices!=NULL)
    {
        free(rightindices);
        rightindices=NULL;
    }

    if( Sphere3D_TextureID !=-1 ) {
        OpenGLUtils::destroyTextureID();
        Sphere3D_TextureID = -1;
    }

    if( Sphere3D_fbotexture !=-1 ) {
        OpenGLUtils::destroyfboTextureID();
        Sphere3D_fbotexture = -1;
    }

    if( Sphere3D_framebuffe !=-1 ) {
        OpenGLUtils::destroyframbuffers();
        Sphere3D_framebuffe = -1;
    }

    screenWidth=0;
    screenHeight=0;

    mNumIndices=0;

    m3dLoadIdentity44(modelMatrix);
    m3dLoadIdentity44(projectionMatrix);
    m3dLoadIdentity44(viewMatrixlook);
    m3dLoadIdentity44(modelViewMatrix);
    m3dLoadIdentity44(mMVPMatrix);
    m3dLoadIdentity44(mWorldRotationMatrix);
    m3dLoadIdentity44(mSensorMatrix);
}

void Sphere3D::SetTouch_Motion_Status(bool touchstatus,bool motionstatus)
{
    isUsingTouch=touchstatus;
    isUsingMotion=motionstatus;
}
