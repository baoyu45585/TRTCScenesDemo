cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             vr-so
             SHARED
             src/main/cpp/native-lib.cpp
             src/main/cpp/vr/OpenGLUtils.cpp
             src/main/cpp/vr/Sphere3D.cpp
             src/main/cpp/vr/vecmath.cpp
             src/main/cpp/vr/math3d.cpp

             src/main/cpp/effect-jni.cpp
             src/main/cpp/effect/EffectOpenGLRender.cpp
             src/main/cpp/effect/EffectOpenGLUtils.cpp
             src/main/cpp/effect/Effectvecmath.cpp
             src/main/cpp/common/GlShaders.cpp

             )

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -frtti")
find_library( # Sets the name of the path variable.
              log-lib
              log )

find_library(android-lib android)

target_link_libraries( # Specifies the target library.
                       vr-so
                       EGL
                       GLESv2
                       ${android-lib}
                       ${log-lib} )