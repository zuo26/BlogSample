# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# 将 native-lib.cpp 构建出 so共享库，并命名为 hello
add_library( # 构建的库的名字
             hello

             # 共享库
             SHARED

             # 库的原文件，这里与 CMakeLists.txt 同目录，直接就写 hello_lib.cpp 了
             method2.cpp
             hello_lib.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

# 通过 find_library 来找到需要关联的三方库
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              # 需要关联的 so 名字
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

# 将我们的 .so 关联到我们的 hello_lib.cpp
include_directories(include)
# 导入三方库
add_library(myJniTest
            SHARED
            IMPORTED)
# 设置关联的 so 库名称、目标位置
set_target_properties(myJniTest
                      PROPERTIES IMPORTED_LOCATION
                      ${CMAKE_SOURCE_DIR}/../libs/${ANDROID_ABI}/libmyJniTest.so )

# 通过 link 可将源文件构建的库和三方库都加载进来
target_link_libraries( # 源文件库的名字
                       hello
                       # 引用的三方库
                       myJniTest

                       # Links the target library to the log library
                       # included in the NDK.
                       # 三方库
                       ${log-lib} )