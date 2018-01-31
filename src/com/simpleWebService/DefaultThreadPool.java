package com.simpleWebService;

import com.com.thread.pojo.Job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: GanDuo
 * @Description: 线程安全的简单线程池
 * @Date: 22:15 2018/1/18
 * @param null
 */
public class DefaultThreadPool{
    //final域,在对象创建后,对所有线程可见.
    //如果全局变量只读,不修改 那么就不需要做可见性保证.如果要对其进行修改的
    //  1在构造方法中,多线程环境下,如果是普通域对象那么要小心构造方法中的重排序
    //  2直接对域对象进行赋值,那么在对象构造完成之后对所有线程可见.
    //最大工作者线程数
    private static final int MAX_WORKER_NUMBERS = 10;
    //默认工作者线程数
    private static final int DEFAULT_WORKER_NUMBERS = 5;
    //最小工作者线程数
    private static final int MIN_WORKER_NUMBERS = 1;
    //工作任务列表,LinkedList ,线程安全
    private LinkedList<Job> jobs = new LinkedList<>();
    //工作者列表, ArrayList 线程不安全, 在多线程环境下 , 需要对其进行删除,和添加
    private List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
    //工作者线程数量
    int workerNum = DEFAULT_WORKER_NUMBERS;
    //线程编号生产
    AtomicLong threadNum = new AtomicLong();
    //执行一个Job,这个Job需要实现Runable
    public void execute(Job job){
        synchronized (jobs){
            jobs.add(job);
            //叫醒工作者起来执行任务
            jobs.notify();
        }
    }

    public DefaultThreadPool() {
        this.initializeWokers(workerNum);
    }

    public DefaultThreadPool(int workerNum){
        //防止越界
        this.workerNum = workerNum > MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS :
                workerNum < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : workerNum;
        this.initializeWokers(workerNum);
    }
    //初始化工作者列表
    private void initializeWokers(int workerNum) {
       for (int i = 0 ;i < workerNum; i++){
           Worker worker = new Worker();
           Thread thread = new Thread(worker , "workerThread: " + threadNum.incrementAndGet());
           workers.add(worker);
           thread.start();
       }
    }

    //关闭线程池
    public void shutdown(){
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

    //增加工作者线程
    public void addWorker(int number){
        //防止越界
        number = number + this.workerNum >= MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS - this.workerNum : number;
        initializeWokers(number);
    }

    //减少工作者线程
    public void removeWorker(int number){
         //防止越界
         if(number >= this.workerNum){
             throw new IllegalArgumentException("beyond worknum");
         }
         //按照给定数量减少worker,防止为负数
         int count = 0;
         if(count <= number) {
            for (int i = 0 ; i < number ; i++){
                workers.get(0).shutdown();
            }
         }
    }

    //得到正在等待执行的任务数量
    public int getJobSize(){
        return jobs.size();
    }

    //工作者线程,负责消息工作任务
    class Worker implements Runnable{
        //是否执行任务
        private boolean flag;

        @Override
        public void run() {
            Job job = null;
            while(flag){
                //如果没有任务,等待
                synchronized (jobs){
                    try {
                        if(jobs.isEmpty()){
                            jobs.wait();
                        }
                    } catch (InterruptedException e) {
                        //感知到外部对该线程进行中断操作
                        Thread.currentThread().interrupt();
                        //结束线程
                        return;
                    }
                    job = jobs.removeFirst();
                }
                if(job != null){
                    try {
                        job.run(); //不是启动线程,而是工作者线程去做这件事情
                    } catch (Exception e) {
                        //忽略job中的Exception
                    }
                }
            }

        }

        public void shutdown(){
            //不再进行工作
            flag = false;
        }
    }
}

