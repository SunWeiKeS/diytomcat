package com.sun.diytomcat.FileHistory.Bootstrap;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;
import com.sun.diytomcat.http.Request;
import com.sun.diytomcat.http.Response;
import com.sun.diytomcat.util.Constant;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Bootstrap05 {

    public static void main(String[] args) {//访问 http://127.0.0.1:18080/

        try {
            logJVM();//像 tomcat 那样 一开始打印 jvm 信息
            int port = 18080;//表示本服务的使用端口号

            //把端口占用提示信息注释掉，当真正异常发生的时候，就会打印出来
            //if (!NetUtil.isUsableLocalPort(port)) {//用来判断端口是否被占用
            //    System.out.println(port + " 端口已经被占用了，请关闭本地端口占用");
            //    return;
            //}
            //在端口上启动ServerSocket服务，浏览器和服务端通过socket进行通信
            ServerSocket ss = new ServerSocket(port);

            while (true) {//处理掉一个Socket链接请求之后，再处理下一个链接请求。
                //表示收到浏览器客户端的请求
                Socket s = ss.accept();
                Request request = new Request(s);
                System.out.println("浏览器的输入信息： \r\n" + request.getRequestString());
                System.out.println("uri:" + request.getUri());

                Response response = new Response();
                String uri = request.getUri();
                if (null == uri)//首先判断 uri 是否为空，如果为空就不处理了。
                    continue;
                System.out.println(uri);
                if ("/".equals(uri)) {//如果是 "/", 那么依然返回原字符串。
                    String html = "Hello DIY Tomcat from vicsun";
                    response.getWriter().println(html);
                } else {//接着处理文件，首先取出文件名，比如访问的是 /a.html, 那么文件名就是 a.html
                    String fileName = StrUtil.removePrefix(uri, "/");
                    File file = FileUtil.file(Constant.rootFolder, fileName);//然后获取对应的文件对象 file
                    if (file.exists()) {//如果文件存在，那么获取内容并通过 response.getWriter 打印。
                        String fileContent = FileUtil.readUtf8String(file);
                        response.getWriter().println(fileContent);
                    } else {//文件不存在的时候，应该返回404，这里只是返回 File Not Found 字符串。
                        response.getWriter().println("File Not Found");
                    }
                }
                handle200(s, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void logJVM() {
        /**
         * 这里获取日志对象的方式是 LogFactory.get() ，
         * 这种方式很方便，否则就要在每个类里面写成
         * static Logger logger = Logger.getLogger(XXX.class)
         * 每个类都要写，是很繁琐的，
         */
        Map<String, String> infos = new LinkedHashMap<>();
        infos.put("Server version", "How2J DiyTomcat/1.0.1");
        infos.put("Server built", "2020-04-08 10:20:22");
        infos.put("Server number", "1.0.1");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture", SystemUtil.get("os.arch"));
        infos.put("Java Home", SystemUtil.get("java.home"));
        infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));

        Set<String> keys = infos.keySet();
        for (String key : keys) {
            LogFactory.get().info(key + ":\t\t" + infos.get(key));//可自适应不同类的变化
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

