package com.blog.a.jni;

public class HelloJni {
    /* hello 库已经引用了 libmyJniTest.so，所以当 hello.so 加载后，myJniTest 自动就会被关联*/
    // static { System.loadLibrary("myJniTest"); }
    public native String stringFromJNI();
}
