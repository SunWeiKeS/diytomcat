package com.sun.diytomcat.http;

import cn.hutool.core.util.StrUtil;
import com.sun.diytomcat.Bootstrap;
import com.sun.diytomcat.catalina.Context;
import com.sun.diytomcat.util.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Request {//创建request对象来解析requestring和uri

    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;//增加一个Context属性，以及对应的 setter：
    public Request(Socket socket) throws IOException {
        this.socket = socket;
        parseHttpRequest();
        if(StrUtil.isEmpty(requestString))
            return;
        parseUri();
        parseContext();
        /**
         * 在构造方法中调用 parseContext(), 倘若当前 Context 的路径不是 "/",
         * 那么要对 uri进行修正，比如 uri 是 /a/index.html，
         * 获取出来的 Context路径不是 "/”， 那么要修正 uri 为 /index.html。
         */
        if(!"/".equals(context.getPath()))
            uri=StrUtil.removePrefix(uri, context.getPath());
    }

    /**
     * 增加解析Context 的方法，
     * 通过获取uri 中的信息来得到 path.
     * 然后根据这个 path 来获取 Context 对象。
     * 如果获取不到，比如 /b/a.html, 对应的 path 是 /b,
     * 是没有对应 Context 的，那么就获取 "/” 对应的 ROOT Context。
     */
    private void  parseContext(){
        String path=StrUtil.subBetween(uri, "/", "/");
        if(null==path)
            path="/";
        else
            path="/"+path;
        context=Bootstrap.contextMap.get(path);
        if(null==context)
            context=Bootstrap.contextMap.get("/");
    }
    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is);
        requestString = new String(bytes, "utf-8");
    }//parseHttpRequest用于解析http请求字符串，这里调用了MiniBrower里重构的readBytes方法

    /**
     * 例如：解析uri ，请求为 http://127.0.0.1:18080/index.html?name=gareen
     */
    private void parseUri() {
        String temp;

        temp = StrUtil.subBetween(requestString, " ", " ");
        if (!StrUtil.contains(temp, '?')) {//对有参数和没参数分别处理
            uri = temp;
            return;
        }
        temp = StrUtil.subBefore(temp, '?', false);//带了问号就表示有参数
        uri = temp;
    }
    public Context getContext() {
        return context;
    }

    public String getUri() {
        return uri;
    }

    public String getRequestString(){
        return requestString;
    }

}
