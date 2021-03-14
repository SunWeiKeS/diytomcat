package com.sun.diytomcat.test;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.sun.diytomcat.util.Constant;
import com.sun.diytomcat.util.MiniBrowser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void testTimeConsumeHtml() throws InterruptedException {
        /**
         * 因为 Bootstrap 是单线程的，来一个请求，处理一个。 处理完毕之后，才能处理下一个。
         * 所以我们在单元测试里准备一个线程池，同时模仿3个同时访问 timeConsume.html，
         * 正是因为 Bootstrap 是单线程的，所以得一个一个地处理，导致3个同时访问，最后累计时间是 3秒以上。
         *
         *  准备一个线程池，里面有20根线程。
         */
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10));
        //开始计时
        TimeInterval timeInterval = DateUtil.timer();


        //连续执行3个任务，可以简单地理解成3个任务同时开始
        for(int i = 0; i<3; i++){
            threadPool.execute(new Runnable(){
                public void run() {
                    getContentString("/timeConsume.html");
                }
            });
        }
        //shutdown 尝试关闭线程池，但是如果 线程池里有任务在运行，就不会强制关闭，直到任务都结束了，才关闭.
        threadPool.shutdown();
        //awaitTermination 会给线程池1个小时的时间去执行，如果超过1个小时了也会返回，如果在一个小时内任务结束了，就会马上返回。
        threadPool.awaitTermination(1, TimeUnit.HOURS);

        //获取经过了多长时间的毫秒数，并且断言它是超过3秒的。
        long duration = timeInterval.intervalMs();
        System.out.println(duration);
        Assert.assertTrue(duration < 3000);
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
