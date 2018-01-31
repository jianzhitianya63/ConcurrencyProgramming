package com.com.thread.pojo;
/**
 * @Author: GanDuo
 * @Description: 简单线程池--> 工作任务
 * @Date: 22:17 2018/1/18
 * @param null
 */
public class Job implements Runnable{
    static long count;
    @Override
    public void run() {
        count ++;
        System.out.println("我是工作任务 "+count+": 快来完成我!!!!");
    }
}
