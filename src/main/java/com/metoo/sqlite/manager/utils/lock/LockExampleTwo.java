package com.metoo.sqlite.manager.utils.lock;

import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

public class LockExampleTwo {

    private final ReentrantLock lock = new ReentrantLock();

    private Thread threadA;

    public void methodA() {
        lock.lock();
        try {
            // 保存当前线程的引用
            threadA = Thread.currentThread();
            System.out.println("Method A is executing.");
            // 模拟长时间的操作
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("Method A was interrupted.");
            Thread.currentThread().interrupt(); // 保持中断状态
        } finally {
            lock.unlock();
        }
    }

    public void methodB() {
        if (threadA != null) {
            threadA.interrupt();
            System.out.println("Attempted to interrupt thread A.");
        } else {
            System.out.println("Thread A is not available.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LockExampleTwo example = new LockExampleTwo();

        // 启动线程 A 来调用 methodA
        Thread threadA = new Thread(example::methodA);
        threadA.start();

        // 确保 threadA 已经开始执行
        Thread.sleep(500);

        // 在主线程中尝试打断 threadA
        example.methodB();

        // 等待线程 A 完成
        threadA.join();

        System.out.println("Thread A has completed.");
    }

}
