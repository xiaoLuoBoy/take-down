package com.horqian.api.util;


import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.horqian.api.constant.CodeConstants;
import org.springframework.util.StringUtils;

/**
 * @author bz
 */
public class PageUtils<T> {

    private final Integer page;

    private final Integer limit;

    private final String field;

    private final String order;

    private final String asc = "asc";
    private final String desc = "desc";

    public PageUtils(Integer page, Integer limit, String field, String order) {
        this.page = page == null ? 1 : page;
        this.limit = limit == null ? 10 : limit;
        this.field = StringUtils.hasText(field) ? CommonUtils.camelCaseToUnderline(field) : "id";
        this.order = StringUtils.hasText(order) ? order : "desc";
    }

    public Page<T> getPage() {

        var pageBean = new Page<T>(page, limit);

        if (asc.equals(order)) {
            pageBean.addOrder(OrderItem.asc(field));
        } else if (desc.equals(order)) {
            pageBean.addOrder(OrderItem.desc(field));
        }

        return pageBean;
    }

    public <T> Page<T> getPage(T bean) {

        var pageBean = new Page<T>(page, limit);


        if (asc.equals(order)) {
            pageBean.addOrder(OrderItem.asc(field));
        } else if (desc.equals(order)) {
            pageBean.addOrder(OrderItem.desc(field));
        }

        return pageBean;

    }

    public Page<T> getExcelPage(T bean) {

        var pageBean = new Page<T>(page, limit);

        if (asc.equals(order)) {
            pageBean.addOrder(OrderItem.asc(field));
        } else if (desc.equals(order)) {
            pageBean.addOrder(OrderItem.desc(field));
        }

        pageBean.setSize(CodeConstants.EXCEL_MAX);

        return pageBean;
    }

    @Override
    public String toString() {
        return "PageUtils [field=" + field + ", limit=" + limit + ", order=" + order + ", page=" + page + "]";
    }

}