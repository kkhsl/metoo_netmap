package com.metoo.sqlite.gather.pool;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 12:56
 */
@Component
public class GatherDataThreadPool {

    public static final int POOL_SIZE;

    static {
        POOL_SIZE = Integer.max(Runtime.getRuntime().availableProcessors(), 15);
    }

    public static void main(String[] args) {
        int s =  Runtime.getRuntime().availableProcessors();
        int POOL_SIZE = Integer.max(Runtime.getRuntime().availableProcessors(), 20);
    }

    /**
     * CPU 密集型：核心线程数=CPU核心数（CPU核心数+1）
     * 1/O 密集型:核心线程数=2*CPU核心数( CPU核心数/ (1-阻塞系数) )
     * 混合型:核心线程数=(线程等待时间/线程CPU时间+1) *CPU核心数
     *
     */
    // ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();// 创建单线程池
    // 不推荐使用，这种方式对现成的控制粒度比较低
    ExecutorService fixedThreadPool = Executors
            .newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);//定长线程池
//     ExecutorService fixedThreadPool = Executors.newCachedThreadPool();//

//    ExecutorService executor = new ThreadPoolExecutor(
//            10,  // core pool size
//            50,  // maximum pool size
//            60L, TimeUnit.SECONDS,
//            new SynchronousQueue<>()
//    );


    private static GatherDataThreadPool pool = new GatherDataThreadPool();// 创建单例

    /**
     * 获取一个单例
     * @return
     */
    public static GatherDataThreadPool getInstance(){
        return pool;
    }

//    private static GatherDataThreadPool pool;
//
//    public static synchronized GatherDataThreadPool getInstance(){
//        if(pool == null){
//            pool = new GatherDataThreadPool();
//        }
//        return pool;
//    }

    /**
     * 向线程池添加一个任务
     * submit(): 向线程池提交单个异步任务
     * invokeAll(): 向线程池提交批量异步任务
     * @param run
     */
    public void addThread(Runnable run) {
        fixedThreadPool.execute(run);
    }

    /**
     * @description 获取线程池对象
     * @return
     */
    public ExecutorService getService() {
        return fixedThreadPool;
    }


    public ExecutorService getFixedThreadPool(int size){
        return Executors.newFixedThreadPool(size);
    }

}
