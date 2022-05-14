package com.blog.a.cpp;

public class HelloCMakeJni {
    static {
        System.loadLibrary("hello");
    }
    public native String stringFromCmakeJNI();
}
