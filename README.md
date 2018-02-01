# The Art of java Concurrency Programming
***
### 第一章并发编程的挑战    
当串行执行次数多时,只有一个线程执行,而多个线程可以同时执行任务,但是在次数少时要考虑到并发的线程上下文切换所消耗的时间  
#### 死锁  

- 当发生死锁, 业务是可感知的, 因为不能继续再提供服务了, 可以通过dump线程来查看哪个线程出了问题  
使用jps查看java程序pid  
使用[jstack](https://www.cnblogs.com/duanxz/p/5487576.html) pid 查看线程状态  
- 避免死锁的几个常见方法  
对于数据库锁,加锁和解锁必须在一个数据库连接里,否则会出现解锁失败的情况  
尝试使用定时锁, 使用lock.tryLock(timeout)来替代使用内部锁机制  
避免一个线程在锁内同时占用多个资源, 尽量保证每个锁只占用一个资源  
避免一个线程同时获得多个锁  
***
### 第二章 java并发机制的底层实现
#### volatile
如果volatile变量修饰符使用恰当的话，它比synchronized的使用和执行成本更低，因为它不会引起线程上下文的切换和调度  

- 实现原理  
为了提高处理速度，处理器不直接和内存进行通信，而是先将系统内存的数据读到内部缓存（L1，L2或其他）后再进行操作，但操作完不知道何时会写到内存.  

- 同步锁  
 monitorenter指令是在编译后插入到同步代码块的开始位置，而monitorexit是插入到方法结
 束处和异常处，JVM要保证每个monitorenter必须有对应的monitorexit与之配对。任何对象都有
 一个monitor与之关联，当且一个monitor被持有后，它将处于锁定状态。线程执行到monitorenter
 指令时，将会尝试获取对象所对应的monitor的所有权，即尝试获得对象的锁。  
- 处理器如何实现原子操作  
 使用总线锁保证原子性  
 使用缓存锁保证原子性(了解)
- Java如何实现原子操作 
 使用循环CAS实现原子操作(了解)  
 使用锁机制实现原子操作  
 锁机制保证了只有获得锁的线程才能够操作锁定的内存区域。JVM内部实现了很多种锁
 机制，有偏向锁、轻量级锁和互斥锁  
### 第三章 Java中的锁  
#### Lock接口  
<pre>Lock lock = new ReentrantLock();
lock.lock();
try{
}finally {
	lock.unlock(); //保证最终能被释放
}
</pre>
- 特性  
尝试非阻塞性的获取锁(立即返回结果)  
能被中断的获取锁(在等待队列中时可进行中断退出等待队列)  
超时获取锁  
- Lock的API  
lock() 获取锁,当未获取到同步状态将当前线程加入同步队列  
void lockInterruptibly() 能被中断的获取锁  
boolean trylock() 非阻塞获取锁   
unlock() 释放锁  
Condition newCondition() 获取等待通知组件(wait())  
- 列队同步器 AbstractQueuedSynchronizer  
构建锁或其他同步组件的基础框架,使用int表示同步状态,通过内置FIFO队列完成线程的排队工作  
主要使用方式是集成,实现其抽象方法管理同步状态, 使用3个方法对同步状态进行更改  
getState() 获取同步状态  
setState(int newState) 设置当前同步状态  
comparaAndSetState(int expect,int update)使用CAS设置当前状态,保证原子性  

- 同步队列  
同步队列会将当前线程以及等待信息构造成为一个节点,并阻塞线程,当同步状态释放会唤醒首节点中的线程  
同步队列中的节点用来保存获取同步状态失败的线程引用,等待状态及前驱和后继节点  

- 独占锁  
同一时刻只有一个线程获取到锁  

- 共享锁  
同一时刻可以有多个线程同时获取到同步状态