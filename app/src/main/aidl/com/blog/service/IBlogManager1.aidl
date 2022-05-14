package com.blog.service;

import com.blog.service.BlogInfo;
import com.blog.service.IBlogListener;
import com.blog.service.AbstractBlogInfo;

interface IBlogManager1 {
    String pullFromService();
    void pushToService(in AbstractBlogInfo info);
    void registerBlogListener(IBlogListener listener);
    void unregisterBlogListener(IBlogListener listener);
    int getVersionCode();
}



