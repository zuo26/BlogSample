package com.blog.cpp;

public class HelloCMakeJni {
    static {
        System.loadLibrary("hello");
    }
    public native String stringFromCmakeJNI();
}
