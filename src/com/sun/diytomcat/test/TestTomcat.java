package com.sun.diytomcat.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.sun.diytomcat.util.Constant;
import com.sun.diytomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

public class TestTomcat {
    private static int port=18080;
    private static String ip="127.0.0.1";

    @BeforeClass
    public static void beforeClass(){
        //所有测试开始前看diy tomcat 是否已经启动
        if(NetUtil.isUsableLocalPort(port)){
            System.err.println("请先启动 位于端口："+port+"的diytomcat，否则无法进行单元测试");
            System.exit(1);//1 表示非正常退出当前程序，0为正常退出
        }
        else {
            System.out.println("检测到 diy tomcat已经启动，开始进行单元测试");
        }
    }

    @Test
    public void testHelloTomcat(){
        String html = getContentString("/");
        Assert.assertEquals(html, "Hello DIY Tomcat from vicsun");

    }

    @Test
    public void testaHtml(){
        String html= getContentString("/a.html");
        Assert.assertEquals(html, "Hello DIY Tomcat from a.html");
    }


    private String getContentString(String uri){
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);
        String content = MiniBrowser.getContentString(url);
        return content;

    }

    @Test
    public void fileFound(){
        String html= getContentString("/a.html");
        String fileName = StrUtil.removePrefix(html, "/");
        System.out.println(fileName);
        File file = FileUtil.file(Constant.rootFolder);

        System.out.println(file.toString());
    }
}
