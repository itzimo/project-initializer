package com.itzimo.project.initializer.core.dto;

import lombok.Data;

/**
 * 页面请求
 *
 * @author chenmiao
 * @date 2025/10/28
 */
@Data
public final class PageRequest {
    private int current = 1;
    private int size = 10;
    // 格式: "field1.asc,field2.desc"
    private String orderBy;
}
