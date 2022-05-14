package com.blog.service;

import com.blog.service.BlogInfo;
import com.blog.service.IBlogListener;
import com.blog.service.AbstractBlogInfo;

interface IBlogManager {
    String pullFromService();
    void pushToService(in BlogInfo info);
    void registerBlogListener(IBlogListener listener);
    void unregisterBlogListener(IBlogListener listener);
    int getVersionCode();
}



