# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( acft-lib SHARED acft-lib.c)
add_library( apft-lib SHARED apft-lib.c)
add_library( abcp-lib SHARED abcp-lib.c)
            # Sets the name of the library.
            # Sets the library as a shared library.
            # Provides a relative path to your source file(s).


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( log-lib log )
            # Sets the name of the path variable.
            # Specifies the name of the NDK library that
            # you want CMake to locate.

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( acft-lib ${log-lib} )
target_link_libraries( apft-lib ${log-lib} )
target_link_libraries( abcp-lib ${log-lib} )
            # Specifies the target library.
            # Links the target library to the log library
            # included in the NDK.