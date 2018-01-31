package com.company;

import com.com.thread.utils.SleepUtils;

import java.util.concurrent.locks.Lock;

/**
 * @Author: GanDuo
 * @Description: 等待通知,也叫生产消费者模式,修改变量的为生产者,执行任务的为消费者
 * @Date: 22:37 2018/1/16
 * @param null
 */
public class WaitNotify {
    static Object lock = new Object();
    static boolean flag = true;
    static volatile boolean b = true;
    public static void main(String[] args) {
        new Thread(new Wait(),"Wait").start();
        new Thread(new Notify(),"Notify").start();
        SleepUtils.second(2);
        update();
    }

    public static void update(){
        b =false;
    }
}
/**
 * @Author: GanDuo
 * @Description: 消费者
 * @Date: 22:39 2018/1/16
 * @param null
 */
class Wait implements Runnable{

    @Override
    public void run() {
        //使用同一把锁进行同步
        synchronized (WaitNotify.lock){
            try {
                //循环 不满足条件等待
                while(WaitNotify.flag){
                    WaitNotify.lock.wait();
                }
                System.out.println("开始执行任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //执行任务
}

class Notify implements Runnable{

    @Override
    public void run() {
        //使用同一把锁
        synchronized (WaitNotify.lock){
            //如果条件不满足,等待
            while (WaitNotify.b){
                SleepUtils.second(1);
            }
            //如果满足条件,修改标记共享变量
            WaitNotify.flag = false;
            //通知消费者
            WaitNotify.lock.notifyAll();
        }

    }
}

