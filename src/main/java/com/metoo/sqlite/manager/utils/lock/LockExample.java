package com.metoo.sqlite.manager.utils.lock;

import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

public class LockExample {


    private final ReentrantLock lock = new ReentrantLock();

    @Test
    public void methodA() {
        lock.lock();
        try {
            // 执行需要锁保护的操作
            System.out.println("Method A is executing.");
            // 模拟长时间的操作
            Thread.sleep(10000);

            System.out.println("Method A end.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public boolean canCallMethodB() {
        return lock.isLocked() && lock.getQueueLength() > 0;
    }

    @Test
    public void methodB() {
//        if (canCallMethodB()) {
//            System.out.println("Method B can be called.");
//            // 执行方法 B 的操作
//        } else {
//            System.out.println("Method B cannot be called.");
//        }

        if (lock.tryLock()) {
            try {
                System.out.println("Method B is executing.");
                // 执行方法 B 的操作
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("Method B cannot be called because lock is held by another thread.");
        }
    }


    public static void main(String[] args) throws InterruptedException {
        LockExample example = new LockExample();

        // 启动线程 A 来调用 methodA
        Thread threadA = new Thread(example::methodA);
        threadA.start();

        // 确保 threadA 已经开始执行
        Thread.sleep(500);

        // 在主线程中检查锁的状态并尝试调用 methodB
        example.methodB();

        // 等待线程 A 完成
        threadA.join();

        Thread.sleep(500);

        // 在主线程中检查锁的状态并尝试调用 methodB
        example.methodB();
    }

}
