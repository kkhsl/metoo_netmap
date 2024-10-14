package com.metoo.sqlite.entity.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * es 查询
 * @author zzy
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class IpStatisticsResult implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * IP
     */
    private String ip;

    /**
     * 访问次数
     */
    private Integer accessCount;

    /**
     * 统计时间
     */
    private String statisticsTime;

    /**
     * 类型（ipv4 或 ipv6)
     */
    private String type;

    /**
     * 排名
     */
    private Integer rank;
}
