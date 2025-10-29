package com.itzimo.project.initializer.repository.api.util;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzimo.project.initializer.core.dto.PageRequest;
import com.itzimo.project.initializer.core.dto.PageResponse;
import com.itzimo.project.initializer.core.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 分页工具类
 *
 * @author chenmiao
 * @date 2025/10/28
 */
public final class PageUtil {
    /**
     * 获取分页参数并构建分页对象
     * ?current=1&size=10&orderBy=field1.asc,field2.desc
     *
     * @return {@link Page }<{@link T }>
     */
    @NonNull
    public static <T> Page<T> page() {
        PageRequest pageRequest = getPageRequestFromContext();
        return buildPage(pageRequest);
    }

    @NonNull
    private static PageRequest getPageRequestFromContext() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

            PageRequest pageRequest = new PageRequest();

            // 从请求参数中获取分页信息
            String currentStr = request.getParameter("current");
            String sizeStr = request.getParameter("size");
            String orderBy = request.getParameter("orderBy");

            if (StringUtils.hasText(currentStr)) {
                try {
                    pageRequest.setCurrent(Math.max(Integer.parseInt(currentStr), 1));
                } catch (NumberFormatException e) {
                    pageRequest.setCurrent(1);
                }
            }

            if (StringUtils.hasText(sizeStr)) {
                try {
                    pageRequest.setSize(Math.min(Math.max(Integer.parseInt(sizeStr), 1), 100));
                } catch (NumberFormatException e) {
                    pageRequest.setSize(10);
                }
            }

            if (StringUtils.hasText(orderBy)) {
                pageRequest.setOrderBy(orderBy);
            }

            return pageRequest;
        }

        return new PageRequest();
    }

    @NonNull
    public static <T> Page<T> buildPage(@NonNull PageRequest pageRequest) {
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

    /**
     * 分页结果
     *
     * @param page 分页数据
     * @return {@link PageResponse }<{@link T }>
     */
    @NonNull
    public static <T> PageResponse<T> pageResult(@NonNull Page<T> page) {
        return PageResponse.page(
                ErrorCode.SUCCESS,
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize()
        );
    }

    /**
     * 获取分页参数并根据list构建分页结果
     * ?current=1&size=10&orderBy=field1.asc,field2.desc
     *
     * @param all 全部数据
     * @return {@link PageResponse }<{@link T }>
     */
    @NonNull
    public static <T> PageResponse<T> pageResult(@NonNull List<T> all) {
        return pageResult(page(), all);
    }

    /**
     * 分页结果
     *
     * @param page 分页请求参数
     * @param all  全部数据
     * @return {@link PageResponse }<{@link T }>
     */
    @NonNull
    public static <T> PageResponse<T> pageResult(@NonNull Page<T> page, @NonNull List<T> all) {
        // 对传入的完整列表进行本地分页处理
        int total = all.size();
        int current = (int) page.getCurrent();
        int size = (int) page.getSize();

        // 计算分页起始和结束位置
        int fromIndex = (current - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);

        // 防止越界
        List<T> pagedList = (total > 0 && fromIndex < total) ?
                all.subList(fromIndex, toIndex) :
                java.util.Collections.emptyList();

        return PageResponse.page(
                ErrorCode.SUCCESS,
                pagedList,
                total,
                current,
                size
        );
    }
}
