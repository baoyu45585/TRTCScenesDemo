//
//  Created by mgted on 2020/2/23.
//

#include "OpenGLUtils.h"
#include "../common/native_log.h"

static GLuint ProgramID=-1;
static GLuint TextureID=-1;
static GLuint barreldistortionProgramID=-1;
static GLuint fboTextureID=-1;
static GLuint fboframbuffers=-1;
static GLuint PlaneProgramID=-1;

GLuint OpenGLUtils::createProgram(const char *vertexShader, const char *fragShader) {
    GLuint vertex;
    GLuint fragment;
    GLuint program;
    GLint linked;

    //加载顶点shader
    vertex = loadShader(GL_VERTEX_SHADER, vertexShader);
    if (vertex == 0) {
        ALOGE("1111111111 loadShader==0");
        return 0;
    }
    // 加载片元着色器
    fragment = loadShader(GL_FRAGMENT_SHADER, fragShader);
    if (fragment == 0) {
        ALOGE("1111111111 fragment==0");
        glDeleteShader(vertex);
        return 0;
    }
    // 创建program
    program = glCreateProgram();
    if (program == 0) {
        ALOGE("1111111111 glCreateProgram==0");
        glDeleteShader(vertex);
        glDeleteShader(fragment);
        return 0;
    }
    // 绑定shader
    glAttachShader(program, vertex);
    glAttachShader(program, fragment);

    // 链接program程序
    glLinkProgram(program);
    // 检查链接状态
    glGetProgramiv(program, GL_LINK_STATUS, &linked);
    if (!linked) {
        ALOGE("1111111111 linked==0");
        GLint infoLen = 0;
        // 检查日志信息长度
        glGetProgramiv(program, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 1) {
            // 分配一个足以存储日志信息的字符串
            char *infoLog = (char *) malloc(sizeof(char) * infoLen);
            // 检索日志信息
            glGetProgramInfoLog(program, infoLen, NULL, infoLog);
         //   ALOGE("Error linking program:\n%s\n", infoLog);
            // 使用完成后需要释放字符串分配的内存
            free(infoLog);
        }
        // 删除着色器释放内存
        glDetachShader(program, vertex);
        glDeleteShader(vertex);
        glDetachShader(program, fragment);
        glDeleteShader(fragment);
        glDeleteProgram(program);
        return 0;
    }
    // 删除着色器释放内存
    glDetachShader(program, vertex);
    glDeleteShader(vertex);
    glDetachShader(program, fragment);
    glDeleteShader(fragment);
    ProgramID=program;
    return program;
}

GLuint OpenGLUtils::barreldistortioncreateProgram(const char *vertexShader, const char *fragShader) {
    GLuint vertex;
    GLuint fragment;
    GLuint program;
    GLint linked;

    //加载顶点shader
    vertex = loadShader(GL_VERTEX_SHADER, vertexShader);
    if (vertex == 0) {
        ALOGE("1111111111 loadShader==0");
        return 0;
    }
    // 加载片元着色器
    fragment = loadShader(GL_FRAGMENT_SHADER, fragShader);
    if (fragment == 0) {
        ALOGE("1111111111 fragment==0");
        glDeleteShader(vertex);
        return 0;
    }
    // 创建program
    program = glCreateProgram();
    if (program == 0) {
        ALOGE("1111111111 glCreateProgram==0");
        glDeleteShader(vertex);
        glDeleteShader(fragment);
        return 0;
    }
    // 绑定shader
    glAttachShader(program, vertex);
    glAttachShader(program, fragment);

    // 链接program程序
    glLinkProgram(program);
    // 检查链接状态
    glGetProgramiv(program, GL_LINK_STATUS, &linked);
    if (!linked) {
        ALOGE("1111111111 linked==0");
        GLint infoLen = 0;
        // 检查日志信息长度
        glGetProgramiv(program, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 1) {
            // 分配一个足以存储日志信息的字符串
            char *infoLog = (char *) malloc(sizeof(char) * infoLen);
            // 检索日志信息
            glGetProgramInfoLog(program, infoLen, NULL, infoLog);
            //   ALOGE("Error linking program:\n%s\n", infoLog);
            // 使用完成后需要释放字符串分配的内存
            free(infoLog);
        }
        // 删除着色器释放内存
        glDetachShader(program, vertex);
        glDeleteShader(vertex);
        glDetachShader(program, fragment);
        glDeleteShader(fragment);
        glDeleteProgram(program);
        return 0;
    }
    // 删除着色器释放内存
    glDetachShader(program, vertex);
    glDeleteShader(vertex);
    glDetachShader(program, fragment);
    glDeleteShader(fragment);
    barreldistortionProgramID=program;
    return program;
}

GLuint OpenGLUtils::PlanecreateProgram(const char *vertexShader, const char *fragShader) {
    GLuint vertex;
    GLuint fragment;
    GLuint program;
    GLint linked;

    //加载顶点shader
    vertex = loadShader(GL_VERTEX_SHADER, vertexShader);
    if (vertex == 0) {
        ALOGE("1111111111 loadShader==0");
        return 0;
    }
    // 加载片元着色器
    fragment = loadShader(GL_FRAGMENT_SHADER, fragShader);
    if (fragment == 0) {
        ALOGE("1111111111 fragment==0");
        glDeleteShader(vertex);
        return 0;
    }
    // 创建program
    program = glCreateProgram();
    if (program == 0) {
        ALOGE("1111111111 glCreateProgram==0");
        glDeleteShader(vertex);
        glDeleteShader(fragment);
        return 0;
    }
    // 绑定shader
    glAttachShader(program, vertex);
    glAttachShader(program, fragment);

    // 链接program程序
    glLinkProgram(program);
    // 检查链接状态
    glGetProgramiv(program, GL_LINK_STATUS, &linked);
    if (!linked) {
        ALOGE("1111111111 linked==0");
        GLint infoLen = 0;
        // 检查日志信息长度
        glGetProgramiv(program, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 1) {
            // 分配一个足以存储日志信息的字符串
            char *infoLog = (char *) malloc(sizeof(char) * infoLen);
            // 检索日志信息
            glGetProgramInfoLog(program, infoLen, NULL, infoLog);
            //   ALOGE("Error linking program:\n%s\n", infoLog);
            // 使用完成后需要释放字符串分配的内存
            free(infoLog);
        }
        // 删除着色器释放内存
        glDetachShader(program, vertex);
        glDeleteShader(vertex);
        glDetachShader(program, fragment);
        glDeleteShader(fragment);
        glDeleteProgram(program);
        return 0;
    }
    // 删除着色器释放内存
    glDetachShader(program, vertex);
    glDeleteShader(vertex);
    glDetachShader(program, fragment);
    glDeleteShader(fragment);
    PlaneProgramID=program;
    return program;
}

GLuint OpenGLUtils::loadShader(GLenum type, const char *shaderSrc) {
    GLuint shader;
    GLint compiled;
    // 创建shader
    shader = glCreateShader(type);
    if (shader == 0) {
        return 0;
    }
    // 加载着色器的源码
    glShaderSource(shader, 1, &shaderSrc, NULL);

    // 编译源码
    glCompileShader(shader);

    // 检查编译状态
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);

    if (!compiled) {
        GLint infoLen = 0;
        // 查询日志的长度判断是否有日志产生
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);

        if (infoLen > 1) {
            // 分配一个足以存储日志信息的字符串
            char *infoLog = (char *) malloc(sizeof(char) * infoLen);
            // 检索日志信息
            glGetShaderInfoLog(shader, infoLen, NULL, infoLog);
          //  ALOGE("Error compiling shader:\n%s\n", infoLog);
            // 使用完成后需要释放字符串分配的内存
            free(infoLog);
        }
        // 删除编译出错的着色器释放内存
        glDeleteShader(shader);
        return 0;
    }
    return shader;
}

void OpenGLUtils::checkActiveUniform(GLuint program) {
    GLint maxLen;
    GLint numUniforms;
    char *uniformName;

    glGetProgramiv(program, GL_ACTIVE_UNIFORMS, &numUniforms);
    glGetProgramiv(program, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, &maxLen);

    uniformName = (char *) malloc(sizeof(char) * maxLen);

    for (int i = 0; i < numUniforms; ++i) {
        GLint size;
        GLenum type;
        GLint location;

        glGetActiveUniform(program, i, maxLen, NULL, &size, &type, uniformName);

        location = glGetUniformLocation(program, uniformName);

      //  ALOGD("location:", location);

        switch (type) {
            case GL_FLOAT: {
             //   ALOGD("type : GL_FLOAT");
                break;
            }
            case GL_FLOAT_VEC2: {
               // ALOGD("type : GL_FLOAT_VEC2");
                break;
            }
            case GL_FLOAT_VEC3: {
              //  ALOGD("type : GL_FLOAT_VEC3");
                break;
            }
            case GL_FLOAT_VEC4: {
              //  ALOGD("type : GL_FLOAT_VEC4");
                break;
            }
            case GL_INT: {
              //  ALOGD("type : GL_INT");
                break;
            }
        }
    }
}
//GL_TEXTURE_2D
GLuint OpenGLUtils::createTexture(GLenum type) {
    GLuint textureId;
    // 设置解包对齐
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
    // 创建纹理
    glGenTextures(1, &textureId);
    // 绑定纹理
    glBindTexture(type, textureId);
    // 设置放大缩小模式
    glTexParameterf(type, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameterf(type, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameterf(type, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(type, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    TextureID=textureId;
    ALOGE("xxxxxxxxxxcreateTexture:%d\n", textureId);
    OpenGLUtils::checkGLError("createTexture");
    return textureId;
}

GLuint OpenGLUtils::createTextureWithBytes(unsigned char *bytes, int width, int height) {
    GLuint textureId;
    if (bytes == NULL) {
        return 0;
    }
    // 创建Texture
    glGenTextures(1, &textureId);
    // 绑定类型
    glBindTexture(GL_TEXTURE_2D, textureId);
    // 利用像素创建texture
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bytes);
    // 设置放大缩小模式
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    return textureId;
}

GLuint OpenGLUtils::createTextureWithOldTexture(GLuint texture, unsigned char *bytes, int width,
                                                int height) {
    if (texture == 0) {
        return createTextureWithBytes(bytes, width, height);
    }
    // 绑定到当前的Texture
    glBindTexture(GL_TEXTURE_2D, texture);
    // 更新Texture数据
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_NONE, GL_TEXTURE_2D, bytes);
    return texture;
}

void OpenGLUtils::createFrameBuffer(GLuint *framebuffer, GLuint *texture, int width, int height) {
    createFrameBuffers(framebuffer, texture, width, height, 1);
}

int OpenGLUtils::createFrameBuffers(GLuint *frambuffers, GLuint *textures, int width, int height,
                                     int size) {
    // 创建并初始化 FBO 纹理
    glGenTextures(1, textures);
    OpenGLUtils::checkGLError("createFrameBuffers1");
    glBindTexture(GL_TEXTURE_2D, *textures);
    OpenGLUtils::checkGLError("createFrameBuffers2");
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glBindTexture(GL_TEXTURE_2D, GL_NONE);
    OpenGLUtils::checkGLError("createFrameBuffers3");
    // 创建并初始化 FBO
    glGenFramebuffers(1, frambuffers);
    OpenGLUtils::checkGLError("createFrameBuffers4");
    glBindFramebuffer(GL_FRAMEBUFFER, *frambuffers);
    OpenGLUtils::checkGLError("createFrameBuffers5");


   /* GLuint renderbufferIds;
    glGenRenderbuffers(1, &renderbufferIds);
    glBindRenderbuffer(GL_RENDERBUFFER, renderbufferIds);
    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT16, width, height);*/



    glBindTexture(GL_TEXTURE_2D, *textures);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, nullptr);
    OpenGLUtils::checkGLError("createFrameBuffers6");
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, *textures, 0);
    OpenGLUtils::checkGLError("createFrameBuffers7");
   // glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderbufferIds);
    //OpenGLUtils::checkGLError("createFrameBuffers8");
    if (glCheckFramebufferStatus(GL_FRAMEBUFFER)!= GL_FRAMEBUFFER_COMPLETE) {
        ALOGE("FBOSample::CreateFrameBufferObj glCheckFramebufferStatus status :%d",glCheckFramebufferStatus(GL_FRAMEBUFFER));
        return -1;
    }
    fboTextureID=*textures;
    fboframbuffers=*frambuffers;
    glBindTexture(GL_TEXTURE_2D, GL_NONE);
    glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE);
    return 0;
}

void OpenGLUtils::checkGLError(const char *op) {
    for (GLint error = glGetError(); error; error = glGetError()) {
        ALOGE("xxxxxxxxxx[GLES2] after %s() glError (0x%x)\n", op, error);
    }
}

void OpenGLUtils::bindTexture(int location, int texture, int index) {
    bindTexture(location, texture, index, GL_TEXTURE_2D);
}

void OpenGLUtils::bindTexture(int location, int texture, int index, int textureType) {
    glActiveTexture(GL_TEXTURE0 + index);
    glBindTexture(textureType, texture);
    glUniform1i(location, index);
}

GLuint OpenGLUtils::getProgram()
{
    return ProgramID;
}

GLuint OpenGLUtils::barreldistortiongetProgram()
{
    return barreldistortionProgramID;
}

GLuint OpenGLUtils::PlaneProgram()
{
    return PlaneProgramID;
}


GLuint OpenGLUtils::getTextureID()
{
    return TextureID;
}

void OpenGLUtils::destroyTextureID()
{
    glDeleteTextures(1,&TextureID);
}

void OpenGLUtils::destroyfboTextureID()
{
    glDeleteTextures(1,&fboTextureID);
}

void OpenGLUtils::destroyframbuffers()
{
    glDeleteFramebuffers(1,&fboframbuffers);
}