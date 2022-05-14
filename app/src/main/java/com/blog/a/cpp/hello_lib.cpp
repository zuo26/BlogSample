#include <jni.h>
#include <string>

#include "method2.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_blog_a_cpp_HelloCMakeJni_stringFromCmakeJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = getHelloWorld();
    return env->NewStringUTF(hello.c_str());
}
