package com.metoo.sqlite.dto;

import lombok.Data;

import java.util.List;

/**
 * elk统计结果数据
 * @author zhaozhiyuan
 * @version 1.0
 * @date 2024/10/11 20:22
 */
@Data
public class SessionInfoDto {
    private List<String> ipv4Top10;
    private Long ipv4Session;
    private Long ipv6Session;
}
