#include <jni.h>
#include <string>

#include "HelloJni.h"
extern "C" JNIEXPORT jstring JNICALL
Java_com_blog_a_jni_HelloJni_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
