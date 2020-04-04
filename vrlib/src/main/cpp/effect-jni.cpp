#include <jni.h>
#include <string>

#include "effect/EffectOpenGLUtils.h"
#include "effect/EffectOpenGLRender.h"

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_EffectNativeUtils_createtextureid(JNIEnv *env, jobject instance) {
    GLuint Textureid=EffectOpenGLUtils::createTexture(GL_TEXTURE_EXTERNAL_OES);
    return (jint)Textureid;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_EffectNativeUtils_effectinit(JNIEnv *env, jobject instance) {
    EffectOpenglRender::init();
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_EffectNativeUtils_effectrender(JNIEnv *env, jobject instance, jfloatArray matrix_,jint textureid) {
    jfloat *matrix = env->GetFloatArrayElements(matrix_, NULL);
    EffectOpenglRender::render(matrix,textureid);
    env->ReleaseFloatArrayElements(matrix_, matrix, 0);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_EffectNativeUtils_setscreenwidthheight(JNIEnv *env, jobject instance,jint screenWidth, jint screenHeight) {
    EffectOpenglRender::setscreenwidthheight(screenWidth,screenHeight);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_EffectNativeUtils_setvideowidthheight(JNIEnv *env, jobject instance,jint videoWidth, jint videoHeight) {
    EffectOpenglRender::setvideowidthheight(videoWidth,videoHeight);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ndk_EffectNativeUtils_destroy(JNIEnv *env, jobject instance) {
    EffectOpenglRender::destroy();
    return 0;
}