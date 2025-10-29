package com.itzimo.project.initializer.repository.api.util;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzimo.project.initializer.core.dto.PageRequest;
import com.itzimo.project.initializer.core.dto.PageResponse;
import com.itzimo.project.initializer.core.enums.ErrorCode;
import org.springframework.util.StringUtils;

/**
 * 分页工具类
 *
 * @author chenmiao
 * @date 2025/10/28
 */
public final class PageUtil {
    public <T> Page<T> page(PageRequest pageRequest) {
        Page<T> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        // 解析排序参数
        if (StringUtils.hasLength(pageRequest.getOrderBy())) {
            String[] orders = pageRequest.getOrderBy().split(",");
            for (String order : orders) {
                String[] parts = order.split("\\.");
                if (parts.length == 2) {
                    String field = parts[0];
                    boolean isAsc = "asc".equalsIgnoreCase(parts[1]);
                    OrderItem orderItem = isAsc ? OrderItem.asc(field) : OrderItem.desc(field);
                    page.addOrder(orderItem);
                }
            }
        }
        return page;
    }

    public <T> PageResponse<T> page(Page<T> page) {
        return PageResponse.page(
                ErrorCode.SUCCESS,
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
    }
}
