package com.lock;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
/**
 * @author: GanDuo
 * @Description: 自定义Lock同步组件
 * @Date: 21:59 2018/2/1
 */
public class Mutex implements Lock{
    /**
     * @Description: 自定义同步器
     * @Date: 21:59 2018/2/1
     */
    private static class Sync extends AbstractQueuedSynchronizer{
        @Override
        /**
         * @Description: 非阻塞获取锁
         * @Date: 21:58 2018/2/1
         * @param arg
         */
        protected boolean tryAcquire(int arg) {
            //当状态为0的时候获取锁,CAS具有volatile读写语义
            if (compareAndSetState(0,1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        /**
         * @Description: 释放锁,将状态设置为0
         * @Date: 21:59 2018/2/1
         */
        protected boolean tryRelease(int arg) {
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            //写volatile
            setState(0);
            return true;
        }

        @Override
        /**
         * @Description: 是否处于占用状态
         * @Date: 21:59 2018/2/1
         */
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        Condition newCondition() {
            return new ConditionObject();
        }
    }

    private final Sync sync = new Sync();

    @Override
    public void lock() {
        //如果没有获取到同步状态将加入到等待队列自旋获取同步状态,判断条件为上一个Note为Head节点
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        //可中断获取锁
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        //非阻塞获取锁
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        //超时获取锁,与lock()方法相似,加了超时等待机制,快速自旋
        return sync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    /**
     * @Description: 测试
     * @Date: 22:08 2018/2/1
     */
    public static void main(String[] args) throws InterruptedException {
        Mutex mutex = new Mutex();
        System.out.println(mutex.tryLock());
        mutex.unlock();
        mutex.lock();
        mutex.unlock();
    }
}
