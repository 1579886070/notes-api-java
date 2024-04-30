package com.zxxwl.common.utils.page;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * 伪代码：
 * 初始化： PageUtil<Object> pageUtil = new PageUtil<>(1, 5, 20L);
 * sql 分页： LIMIT this.currentRow,this.limit
 * { page:当前页,limit：每页数据大小,currentPageSize：当前页数量,pages：总页数,count：总个数/总记录数,currentRow：当前行 limit currentRow,limit,data:数据容器}
 *
 * @param <T>
 * @author qingyu
 */
@Data
@Accessors(chain = true)
public class PageUtil<T> {

    /**
     * 当前页
     */
    private Integer page = 1;

    /**
     * 每页数据大小
     */
    private Integer limit = 10;

    /**
     * 当前页数量
     */
    private Integer currentPageSize;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 总个数/总记录数
     */
    private Long count;

    /**
     * 当前行 limit currentRow,limit
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private Long currentRow;

    /**
     * 数据容器
     */
    private List<T> data;

    /**
     * 获取总页数
     * 向上取整
     */
    public void setPages() {
        // this.pages = new Double(Math.ceil(this.count / (double) this.limit)).intValue();
        this.pages = (int) ((this.count + this.limit - 1) / this.limit);
    }

    /**
     * 当前行 LIMIT currentRow,limit
     */
    public void setCurrentRow() {
        // if (this.page > 0 && this.page <= this.pages) {
        if (this.page > 0) {
            this.currentRow = (long) (this.page - 1) * this.limit;
        } else {
            this.currentRow = 0L;
        }
    }

    public void setData(List<T> data) {
        if (data == null || data.isEmpty()) {
            // 保证无数据返回 空集合
            this.data = Collections.emptyList();
            this.currentPageSize = 0;
        } else {
            this.data = data;
            this.currentPageSize = data.size();
        }
    }

    private PageUtil() {
    }

    /**
     * @param page  当前页
     * @param limit 每页数据大小
     * @param count 总记录数
     */
    public PageUtil(Integer page, Integer limit, Long count) {
        this.page = page;
        this.limit = limit;
        this.count = count;
        this.setPages();
        this.setCurrentRow();
    }

    /**
     * 是否超出范围,超出范围返回空数据
     *
     * @return isOutOfRange
     */
    public boolean isOutOfRange() {
        page = page < 1 ? 1 : page;
        limit = limit < 1 ? 1 : limit;
        return page > pages;
    }

    /**
     * 空数据
     *
     * @return com.zxxwl.user.service.utils.PageUtil
     */
    public static PageUtil<?> getEmptyPages() {
        PageUtil<?> empty = new PageUtil<>();
        empty.setPage(1);
        empty.setLimit(10);
        empty.setCount(0L);
        empty.setPages(1);
        empty.setData(Collections.emptyList());
        return empty;
    }
}
