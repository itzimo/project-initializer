package com.itzimo.project.initializer.repository.api.util;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzimo.project.initializer.core.dto.PageRequest;
import com.itzimo.project.initializer.core.dto.PageResponse;
import com.itzimo.project.initializer.core.enums.ErrorCode;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 分页工具类
 * 提供分页相关的通用方法，包括构建分页对象、处理分页结果等
 *
 * @author chenmiao
 * @date 2025/10/28
 */
public final class PageUtil {

    /**
     * 字段名校验正则表达式（只允许字母、数字、下划线）
     * 用于验证排序字段名的合法性，防止SQL注入等安全问题
     */
    private static final Pattern FIELD_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    /**
     * 实体类字段缓存，避免重复反射操作
     * Key为实体类Class对象，Value为该类所有字段名的集合（下划线格式）
     * 通过缓存提高性能，避免每次分页都进行反射操作
     */
    private static final Map<Class<?>, Set<String>> ENTITY_FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 构建基础分页对象
     * 从请求上下文中获取分页参数，创建MyBatis Plus的Page对象
     * 不包含排序信息和字段安全检查
     *
     * @param <T> 泛型参数，表示分页数据的类型
     * @return Page对象，包含分页基本信息
     */
    public static <T> Page<T> buildPage() {
        PageRequest pageRequest = PageRequest.getPageRequestFromContext();
        return new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
    }

    /**
     * 构建基于指定实体类的分页对象
     * 从请求上下文中获取分页参数，并根据指定实体类进行字段安全检查
     *
     * @param entityClass 实体类Class对象，用于获取允许排序的字段
     * @param <T> 泛型参数，表示分页数据的类型
     * @return Page对象，包含分页信息和安全过滤后的排序规则
     */
    public static <T> Page<T> buildPage(@NonNull Class<T> entityClass) {
        return buildPage(PageRequest.getPageRequestFromContext(), entityClass);
    }

    /**
     * 构建分页对象，基于实体类字段自动过滤
     * 支持数据库下划线字段名的验证和排序
     *
     * @param pageRequest 分页请求参数
     * @param entityClass 实体类Class，用于获取允许排序的字段
     * @return 分页对象
     */
    @NonNull
    public static <T> Page<T> buildPage(@NonNull PageRequest pageRequest, @NonNull Class<T> entityClass) {
        Page<T> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        // 解析排序参数
        if (StringUtils.hasLength(pageRequest.getOrderBy())) {
            String[] orders = pageRequest.getOrderBy().split(",");
            for (String order : orders) {
                String[] parts = order.split("\\.");
                if (parts.length == 2) {
                    String field = parts[0];
                    boolean isAsc = "asc".equalsIgnoreCase(parts[1]);

                    // 安全检查：验证字段名格式和是否为实体类中的字段
                    if (isValidFieldName(field) && isEntityField(field, entityClass)) {
                        // 直接使用下划线字段名进行排序
                        OrderItem orderItem = isAsc ? OrderItem.asc(field) : OrderItem.desc(field);
                        page.addOrder(orderItem);
                    }
                }
            }
        }
        return page;
    }

    /**
     * 驼峰转下划线格式
     * 例如：userName -> user_name
     *
     * @param camelStr 驼峰格式字符串
     * @return 下划线格式字符串
     */
    private static String camelToUnderline(String camelStr) {
        if (camelStr == null || camelStr.isEmpty()) {
            return camelStr;
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelStr.length(); i++) {
            char c = camelStr.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append("_");
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }

    /**
     * 从实体类中获取允许排序的字段（下划线格式）
     *
     * @param entityClass 实体类Class
     * @return 允许排序的字段集合（下划线格式）
     */
    private static Set<String> getAllowedFieldsFromEntity(Class<?> entityClass) {
        // 先从缓存中获取
        if (ENTITY_FIELD_CACHE.containsKey(entityClass)) {
            return ENTITY_FIELD_CACHE.get(entityClass);
        }

        // 通过反射获取实体类的所有字段，并转换为下划线格式
        Set<String> fields = new HashSet<>();
        Class<?> clazz = entityClass;
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                // 将驼峰字段名转换为下划线格式存储
                fields.add(camelToUnderline(field.getName()));
            }
            clazz = clazz.getSuperclass();
        }

        // 放入缓存
        ENTITY_FIELD_CACHE.put(entityClass, fields);
        return fields;
    }

    /**
     * 检查字段是否为实体类中的字段
     * 字段名应为下划线格式
     *
     * @param fieldName   字段名（下划线格式）
     * @param entityClass 实体类Class
     * @return 是否为实体类中的字段
     */
    private static boolean isEntityField(String fieldName, Class<?> entityClass) {
        Set<String> allowedFields = getAllowedFieldsFromEntity(entityClass);
        // 直接检查是否匹配（字段名已经是下划线格式）
        return allowedFields.contains(fieldName);
    }

    /**
     * 验证字段名格式是否合法
     *
     * @param fieldName 字段名
     * @return 是否合法
     */
    private static boolean isValidFieldName(String fieldName) {
        return FIELD_PATTERN.matcher(fieldName).matches();
    }

    /**
     * 根据MyBatis Plus分页结果构建统一响应格式
     *
     * @param page 分页数据
     * @return {@link PageResponse }<{@link T}>
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
     * ?current=1&size=10
     *
     * @param all 全部数据
     * @return {@link PageResponse }<{@link T }>
     */
    @NonNull
    public static <T> PageResponse<T> pageResult(List<T> all) {
        return pageResult(PageRequest.getPageRequestFromContext(), all);
    }

    /**
     * 根据分页对象和完整数据列表构建分页结果
     *
     * @param page 分页参数
     * @param all  全部数据
     * @return {@link PageResponse }<{@link T }>
     */
    @NonNull
    public static <T> PageResponse<T> pageResult(@NonNull Page<T> page, List<T> all) {
        return pageResult(all, (int) page.getCurrent(), (int) page.getSize());
    }

    /**
     * 根据分页请求和完整数据列表构建分页结果
     *
     * @param page 分页参数
     * @param all  全部数据
     * @return {@link PageResponse }<{@link T }>
     */
    @NonNull
    public static <T> PageResponse<T> pageResult(@NonNull PageRequest page, List<T> all) {
        return pageResult(all, page.getCurrent(), page.getSize());
    }

    /**
     * 分页结果
     *
     * @param all     全部数据
     * @param current 页数
     * @param size    每页大小
     * @return {@link PageResponse }<{@link T }>
     */
    @NonNull
    private static <T> PageResponse<T> pageResult(List<T> all, int current, int size) {
        if (all == null) {
            return PageResponse.page(ErrorCode.SUCCESS, List.of(), 0, current, size);
        }
        int total = all.size();
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