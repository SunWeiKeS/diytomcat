package com.sun.diytomcat.util;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 这个浏览器会模拟发送 Http 协议的请求，并且获取完整的 Http 响应，
 * 通过这种方式，我们就可以更好的理解浏览器与服务器是如何通信，
 * 在后面学习 Http 协议的时候，也更容易理解了。
 * 为了模仿浏览器，不仅要获取 html 内容，
 * 更重要的是要获取 Http 响应的头信息，
 * 这样才能更好地理解 http 协议。
 */
public class MiniBrowser {//服务于http包

    public static void main(String[] args) throws Exception {
        String url = "http://static.how2j.cn/diytomcat.html";
        String contentString= getContentString(url,false);
        System.out.println(contentString);
        String httpString= getHttpString(url,false);
        System.out.println(httpString);
    }

    public static byte[] getContentBytes(String url) {
        return getContentBytes(url, false);
    }

    public static String getContentString(String url) {
        return getContentString(url,false);
    }

    public static String getContentString(String url, boolean gzip) {
        byte[] result = getContentBytes(url, gzip);
        if(null==result)
            return null;
        try {
            return new String(result,"utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getContentBytes(String url, boolean gzip) {
        byte[] response = getHttpBytes(url,gzip);
        byte[] doubleReturn = "\r\n\r\n".getBytes();

        int pos = -1;
        for (int i = 0; i < response.length-doubleReturn.length; i++) {
            byte[] temp = Arrays.copyOfRange(response, i, i + doubleReturn.length);

            if(Arrays.equals(temp, doubleReturn)) {
                pos = i;
                break;
            }
        }
        if(-1==pos)
            return null;

        pos += doubleReturn.length;

        byte[] result = Arrays.copyOfRange(response, pos, response.length);
        return result;
    }

    public static String getHttpString(String url,boolean gzip) {
        byte[]  bytes=getHttpBytes(url,gzip);
        return new String(bytes).trim();
    }

    public static String getHttpString(String url) {
        return getHttpString(url,false);
    }

    public static byte[] getHttpBytes(String url,boolean gzip) {
        byte[] result = null;
        try {
            URL u = new URL(url);
            Socket client = new Socket();
            int port = u.getPort();
            if(-1==port)
                port = 80;
            InetSocketAddress inetSocketAddress = new InetSocketAddress(u.getHost(), port);
            client.connect(inetSocketAddress, 1000);
            Map<String,String> requestHeaders = new HashMap<>();

            requestHeaders.put("Host", u.getHost()+":"+port);
            requestHeaders.put("Accept", "text/html");
            requestHeaders.put("Connection", "close");
            requestHeaders.put("User-Agent", "Vics' mini brower / java1.8");

            if(gzip)
                requestHeaders.put("Accept-Encoding", "gzip");

            String path = u.getPath();
            if(path.length()==0)
                path = "/";

            String firstLine = "GET " + path + " HTTP/1.1\r\n";

            StringBuffer httpRequestString = new StringBuffer();
            httpRequestString.append(firstLine);
            Set<String> headers = requestHeaders.keySet();
            for (String header : headers) {
                String headerLine = header + ":" + requestHeaders.get(header)+"\r\n";
                httpRequestString.append(headerLine);
            }

            PrintWriter pWriter = new PrintWriter(client.getOutputStream(), true);
            pWriter.println(httpRequestString);
            InputStream is = client.getInputStream();

            result = readBytes(is);

            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                result = e.toString().getBytes("utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }

        return result;

    }
    public static byte[] readBytes(InputStream is) throws IOException {
        int buffer_size = 1024;
        byte buffer[] = new byte[buffer_size];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while(true) {
            int length = is.read(buffer);//准备一个 1024长度的缓存，不断地从输入流读取数据到这个缓存里面去。
            if(-1==length)//如果读取到的长度是 -1，那么就表示到头了，就停止循环
                break;
            baos.write(buffer, 0, length);//把读取到的数据，根据实际长度，写出到 一个字节数组输出流里。
            if(length!=buffer_size)//如果读取到的长度小于 buffer_size, 说明也读完了
                break;
        }
        byte[] result =baos.toByteArray();
        return result;
    }

}

































