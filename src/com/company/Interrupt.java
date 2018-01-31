package com.company;

import com.com.thread.utils.SleepUtils;

/**
 * @Author: GanDuo
 * @Description: 如何安全的中指线程
 * @Date: 22:17 2018/1/16
 * @param null
 */
public class Interrupt {

    public static void main(String[] args) {
        //使用线程终端标识结束
        Thread thread2 = new Thread(new interuptThreaa(),"InterruptThread-2");
        thread2.start();
        SleepUtils.second(2);
        thread2.interrupt();

        Thread thread1 = new Thread(new interuptThreaa(),"InterruptThread-1");
        thread1.start();
        SleepUtils.second(2);
        //调用中法结束
        interuptThreaa.interrapted();
    }
}

class interuptThreaa implements Runnable{
    //定义一个标识,如果满足条件,则跳出任务结束run方法
    //存在数据竞争,需要使用volatile
    private static volatile boolean flag = true;
    int i;
    @Override
    //只有run()线程体内才属于该线程,而全局变量属于共享变量
    // ,方法每个线程都有副本,谁调用就属于哪个线程
    public void run() {
        while(flag && !Thread.currentThread().isInterrupted()){
          i ++;
        }
        System.out.println("i Count:" + i);
    }

    public static void interrapted(){
        flag = false;
    }
}
