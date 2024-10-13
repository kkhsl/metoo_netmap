package com.metoo.sqlite.gather.strategy;

import com.metoo.sqlite.entity.Device;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.CountDownLatch;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 15:45
 */
@ApiModel("")
@Data
@Accessors(chain = true)
public class Context<T> {

//    private Device device;

    private T entity;

    private String createTime;

    private CountDownLatch latch;

    private String path;
    private Integer logId;
    public Context() {
    }

    public Context(T entity, String createTime, CountDownLatch latch) {
        this.entity = entity;
        this.createTime = createTime;
        this.latch = latch;
    }

    public Context(T entity, String createTime, CountDownLatch latch, String path) {
        this.entity = entity;
        this.createTime = createTime;
        this.latch = latch;
        this.path = path;
    }
}
