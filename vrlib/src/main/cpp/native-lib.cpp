#include <jni.h>
#include <string>
#include "common/native_log.h"
#include "vr/OpenGLUtils.h"
#include "vr/Sphere3D.h"

JavaVM *g_JavaVM;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv *env;
    g_JavaVM = vm;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK)
    {
        return -1;
    }
    return JNI_VERSION_1_4;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_createProgram(JNIEnv *env, jobject instance,jstring url1,jstring url2) {

    // TODO
    const char *c_url = NULL;

    c_url = env->GetStringUTFChars(url1, NULL);

    const char *c_url2 = NULL;

    c_url2 = env->GetStringUTFChars(url2, NULL);
    GLuint program=OpenGLUtils::createProgram(c_url,c_url2);
    return (jint)program;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_barreldistortioncreateProgram(JNIEnv *env, jobject instance,jstring url1,jstring url2) {

    // TODO

    const char *c_url = NULL;

    c_url = env->GetStringUTFChars(url1, NULL);

    const char *c_url2 = NULL;

    c_url2 = env->GetStringUTFChars(url2, NULL);
    GLuint program=OpenGLUtils::barreldistortioncreateProgram(c_url,c_url2);
    return (jint)program;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_PlanecreateProgram(JNIEnv *env, jobject instance,jstring url1,jstring url2) {

    // TODO
    const char *c_url = NULL;

    c_url = env->GetStringUTFChars(url1, NULL);

    const char *c_url2 = NULL;

    c_url2 = env->GetStringUTFChars(url2, NULL);
    GLuint program=OpenGLUtils::PlanecreateProgram(c_url,c_url2);
    return (jint)program;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_createtextureid(JNIEnv *env, jobject instance) {

    GLuint Textureid=OpenGLUtils::createTexture(GL_TEXTURE_EXTERNAL_OES);
    return (jint)Textureid;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_sphere3dinit(JNIEnv *env, jobject instance,jint videoWidth, jint videoHeight) {
    Sphere3D::init(videoWidth,videoHeight);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_sphere3drender(JNIEnv *env, jobject instance, jfloatArray matrix_) {
    jfloat *matrix = env->GetFloatArrayElements(matrix_, NULL);
    Sphere3D::render(matrix);
    env->ReleaseFloatArrayElements(matrix_, matrix, 0);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_sphere3dsurfacechange(JNIEnv *env, jobject instance,jint screenWidth, jint screenHeight) {
    Sphere3D::SurfaceChanged(screenWidth,screenHeight);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_sphere3ddestroy(JNIEnv *env, jobject instance) {
    Sphere3D::destroy();
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_sphere3dSetSensor(JNIEnv *env, jobject instance, jfloatArray matrix_) {
    jfloat *matrix = env->GetFloatArrayElements(matrix_, NULL);
    Sphere3D::SetSensor(matrix);
    env->ReleaseFloatArrayElements(matrix_, matrix, 0);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_sphere3dSettype(JNIEnv *env, jobject instance, jint type) {
    Sphere3D::SetSpheretype(type);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_sphere3dSetvideosize(JNIEnv *env, jobject instance,jint videoWidth, jint videoHeight) {
    Sphere3D::SetSpherevideosize(videoWidth,videoHeight);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_sphere3dSetFingerRotation(JNIEnv *env, jobject instance, jfloat fingerX,jfloat fingerY) {
    Sphere3D::SetFingerRotation(fingerX,fingerY);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_VrNativeUtils_sphere3dSetTouchMotionStatus(JNIEnv *env, jobject instance, jboolean touchstatus,jboolean motionstatus) {
    bool touch = touchstatus;
    bool motion = motionstatus;

    Sphere3D::SetTouch_Motion_Status(touch,motion);
    return 0;
}