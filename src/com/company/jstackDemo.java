package com.company;

import com.com.thread.utils.SleepUtils;

/**
 * @Author: GanDuo
 * @Description: 使用JDK自带工具 jstack查看线程状态
 * @Date: 21:01 2018/1/16
 * @param
 */
public class jstackDemo {
    public static void main(String[] args) {
        new Thread(new TimeWaiting(),"TimeWaiting").start();
        new Thread(new Waiting(),"WaitingThread");
        new Thread(new Blocked(),"BlockedThread -1").start();
        new Thread(new Blocked(),"BlockedThread -2").start();
    }
}

class TimeWaiting implements Runnable{
    @Override
    public void run() {
        while (true){
            SleepUtils.second(100);
        }
    }
}

class Waiting implements Runnable{

    @Override
    public void run() {
        while (true){
            synchronized (Waiting.class){
                try {
                    Waiting.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class Blocked implements Runnable{

    @Override
    public void run() {
        synchronized (Blocked.class){
            while (true){
                SleepUtils.second(100);
            }
        }
    }
}

