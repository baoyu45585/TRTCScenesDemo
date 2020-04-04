//
// Created by mgted on 2020/2/23.
//

#ifndef ANDROID_SPHERE3D_H
#define ANDROID_SPHERE3D_H

#if defined(__ANDROID__)

#include <stdio.h>
#include <stdlib.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <GLES2/gl2platform.h>

#endif

class Sphere3D {
public:
    // 创建program
    static GLuint GetUniformLocation(int type,char *type_name);
    static GLuint GetAttribLocation(int type, char *type_name);
    static void init(int videoWidth, int videoHeight);
    static void createSphere(float radius, int rings, int sectors);
    static void createUpSphere(float radius, int rings, int sectors);
    static void createDownSphere(float radius, int rings, int sectors);
    static void createLeftSphere(float radius, int rings, int sectors);
    static void createRightSphere(float radius, int rings, int sectors);
    static void render(float *Matrix);
    static void SurfaceChanged(int width, int height);
    static void destroy();
    static void change(int type);
    static void SetSensor(float *Matrix);
    static void SetSpheretype(int type);
    static void SetSpherevideosize(int videoWidth, int videoHeight);
    static void perspective(int type,float projectionMatrix[16]);
    static void SetFingerRotation(float fingerX,float fingerY);
    static void SetTouch_Motion_Status(bool touchstatus,bool motionstatus);
private:
    Sphere3D() = default;
    virtual ~Sphere3D(){}
};
#endif //MD360PLAYER4ANDROID_SPHERE3D_H
