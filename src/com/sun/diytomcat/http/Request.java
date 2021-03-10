package com.sun.diytomcat.http;

import cn.hutool.core.util.StrUtil;
import com.sun.diytomcat.util.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Request {//创建request对象来解析requestring和uri

    private String requestString;
    private String uri;
    private Socket socket;
    public Request(Socket socket) throws IOException {
        this.socket = socket;
        parseHttpRequest();
        if(StrUtil.isEmpty(requestString))
            return;
        parseUri();
    }

    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is);
        requestString = new String(bytes, "utf-8");
    }//parseHttpRequest用于解析http请求字符串，这里调用了MiniBrower里重构的readBytes方法

    /**
     * 例如：解析uri ，请求为 http://127.0.0.1:18080/index.html?name=gareen
     *
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

    public String getUri() {
        return uri;
    }

    public String getRequestString(){
        return requestString;
    }

}
