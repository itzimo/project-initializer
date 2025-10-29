package com.itzimo.project.initializer.core.dto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 分页请求参数封装类
 * 用于封装前端传递的分页参数，包括当前页码、每页大小和排序规则
 *
 * @author chenmiao
 * @date 2025/10/28
 */
@Data
public final class PageRequest {
    /**
     * 当前页码，从1开始计数
     * 默认值为1
     */
    private int current = 1;
    
    /**
     * 每页大小，即每页显示的数据条数
     * 默认值为10
     */
    private int size = 10;
    
    /**
     * 排序规则字符串
     * 格式: "field1.asc,field2.desc"，支持多个字段排序
     * asc表示升序，desc表示降序
     */
    private String orderBy;

    /**
     * 从HTTP请求上下文中获取分页参数并构建PageRequest对象
     * 会从HttpServletRequest中解析current、size和orderBy参数
     * 对参数进行基本的有效性验证和边界处理
     *
     * @return PageRequest对象，包含解析后的分页参数
     */
    @NonNull
    public static PageRequest getPageRequestFromContext() {
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
}