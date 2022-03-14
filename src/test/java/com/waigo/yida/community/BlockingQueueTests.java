package com.waigo.yida.community;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author waigo
 * create 2021-10-21 12:09
 */
public class BlockingQueueTests {
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(10);
        Thread proThr = new Thread(new Producer(blockingQueue));
        proThr.setName("生产者");
        proThr.start();
        Thread consumer1 = new Thread(new Consumer(blockingQueue));
        consumer1.setName("消费者1号");
        consumer1.start();
        Thread consumer2 = new Thread(new Consumer(blockingQueue));
        consumer2.setName("消费者2号");
        consumer2.start();
        Thread consumer3 = new Thread(new Consumer(blockingQueue));
        consumer3.setName("消费者3号");
        consumer3.start();
    }
}
class Producer implements Runnable{
    BlockingQueue<Integer> blockingQueue;
    public static final int N = 100;
    public static final int PRODUCE_TIME = 20;
    public Producer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        for(int i = 0;i<N;i++){
            try {
                TimeUnit.MILLISECONDS.sleep(PRODUCE_TIME);
                blockingQueue.put(i);
                System.out.println(Thread.currentThread().getName()+"生产,还剩下:"+blockingQueue.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class Consumer implements Runnable{
    BlockingQueue<Integer> blockingQueue;
    public static final int N = 100;
    public static final int CONSUMER_TIME = 100;
    public static final ReentrantLock lock = new ReentrantLock();
    public Consumer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        for(int i = 0;i<N;i++){
            try {
                TimeUnit.MILLISECONDS.sleep(CONSUMER_TIME);
                lock.lock();
                blockingQueue.take();
                System.out.println(Thread.currentThread().getName()+"消费,还剩下:"+blockingQueue.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
    }
}
