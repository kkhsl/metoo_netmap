//package com.metoo.sqlite.manager.utils;
//
//
//import com.metoo.sqlite.manager.api.ApiService;
//
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.concurrent.TimeUnit;
//
//public class ConcurrentLockExample {
//
//    private final Lock lock = new ReentrantLock();
//
//    public boolean tryLockWithTimeout(long timeout, TimeUnit unit) {
//        try {
//            // 尝试获取锁，超时后返回 false
//            return lock.tryLock(timeout, unit);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); // 恢复中断状态
//            return false;
//        }
//    }
//
//    public void releaseLock() {
////        if (lock.isHeldByCurrentThread()) {
////            lock.unlock();
////        }
//        if (lock.isLocked() && lock.getHoldCount() > 0) {
//            lock.unlock();
//        }
//    }
//
//    public void criticalSection() {
//        if (tryLockWithTimeout(100, TimeUnit.MILLISECONDS)) {
//            try {
//                // 临界区代码
//                ApiService apiService = new ApiService();
//               GatherUtils gatherUtils = new GatherUtils();
//               gatherUtils.gather();
//            } finally {
//                releaseLock();
//            }
//        } else {
//            System.out.println("Unable to acquire lock, exiting.");
//        }
//    }
//
//    public static void main(String[] args) {
//        ConcurrentLockExample example = new ConcurrentLockExample();
//
//        // 创建多个线程来测试并发锁
//        Thread t1 = new Thread(example::criticalSection);
//        Thread t2 = new Thread(example::criticalSection);
//
//        t1.start();
//        t2.start();
//    }
//}
