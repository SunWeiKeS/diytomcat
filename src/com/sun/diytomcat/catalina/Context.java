package com.sun.diytomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;

public class Context {
    private String path;
    private String docBase;

    public Context(String path,String docBase){
        /**
         * 新建的这个类表示一个应用
         * 它有两个属性 path和docBase 以及对应的getter setter
         * path表示访问路径
         * docBase表示对应在文件系统中的位置
         *
         *
         *
         */
        TimeInterval timeInterval = DateUtil.timer();
        this.path=path;
        this.docBase=docBase;
        LogFactory.get().info("Deploying web application directory {}",this.docBase);
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms",
                this.docBase,timeInterval.intervalMs());

    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDocBase() {
        return docBase;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }
}
