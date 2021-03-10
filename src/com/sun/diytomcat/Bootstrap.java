package com.sun.diytomcat;

import cn.hutool.core.util.NetUtil;
import com.sun.diytomcat.http.Request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Bootstrap {

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
                ////打开输入流，准备接受浏览器提交的信息
                //InputStream is= s.getInputStream();
                ////准备一个数组，准备接受浏览器提交的信息 把浏览器的信息读取出来放进去，（这个做法有缺陷）
                //int bufferSize = 1024;
                //byte[] buffer = new byte[bufferSize];
                //is.read(buffer);
                //
                ////转义称字符串并打印
                //String requestString = new String(buffer,"utf-8");
                //System.out.println("浏览器的输入信息： \r\n" + requestString);

                Request request = new Request(s);
                System.out.println("浏览器的输入信息： \r\n" + request.getRequestString());
                System.out.println("uri:"+request.getUri());


                //打开输出流给客户端输出信息
                OutputStream os = s.getOutputStream();
                //准备发送给客户端的数据 响应头信息和组成部分
                String response_head = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n";
                String responseString = "Hello DIY Tomcat from vicsun";
                responseString = response_head + responseString;
                //把字符转换成数组发送出去
                os.write(responseString.getBytes());
                os.flush();

                //关闭客户端对应的 socket
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
