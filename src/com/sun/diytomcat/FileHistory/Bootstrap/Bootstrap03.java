package com.sun.diytomcat.FileHistory.Bootstrap;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.sun.diytomcat.http.Request;
import com.sun.diytomcat.http.Response;
import com.sun.diytomcat.util.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Bootstrap03 {

    public static void main(String[] args) {//访问 http://127.0.0.1:18080/

        try {
            int port = 18080;//表示本服务的使用端口号

            if(!NetUtil.isUsableLocalPort(port)) {//用来判断端口是否被占用
                System.out.println(port +" 端口已经被占用了，请关闭本地端口占用");
                return;
            }
            //在端口上启动ServerSocket服务，浏览器和服务端通过socket进行通信
            ServerSocket ss = new ServerSocket(port);

            while(true) {//处理掉一个Socket链接请求之后，再处理下一个链接请求。
                //表示收到浏览器客户端的请求
                Socket s =  ss.accept();
                Request request = new Request(s);
                System.out.println("浏览器的输入信息： \r\n" + request.getRequestString());
                System.out.println("uri:"+request.getUri());

                Response response = new Response();
                String html="Hello DIY Tomcat from vicsun";
                response.getWriter().println(html);
                handle200(s,response);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_202;
        headText = StrUtil.format(headText, contentType);
        byte[] head = headText.getBytes();

        byte[] body = response.getBody();

        byte[] responseBytes = new byte[head.length + body.length];
        ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
        ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
        s.close();
    }
}


























