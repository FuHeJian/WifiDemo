cmake_minimum_required(VERSION 3.4.1)


#.so文件输出目录
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})


add_library( # Sets the name of the library.
        histogram-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/test.cpp
        )