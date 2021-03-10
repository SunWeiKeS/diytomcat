package com.sun.diytomcat.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

public class Response {
    private StringWriter stringWriter;//创建一个Response类 存放返回的html文本
    private PrintWriter writer;
    private String contentType; //contentType就是对应响应头信息里的 Content-type ，默认是 "text/html"。

    public Response() {
        this.stringWriter= new StringWriter();
        this.writer= new PrintWriter(stringWriter);
        this.contentType="text/html";
    }

    /**
     * 用于提供一个 getWriter() 方法，
     * 这样就可以像 HttpServletResponse
     * 那样写成 response.getWriter().println(); 这种风格了。
     * @return writer
     */
    public PrintWriter getWriter() {
        return writer;
    }

    public String getContentType() {
        return contentType;
    }

    //getBody方法返回html 的字节数组
    public byte[] getBody() throws UnsupportedEncodingException{
        String content = stringWriter.toString();
        byte[] body = content.getBytes("utf-8");
        return body;
    }
}
