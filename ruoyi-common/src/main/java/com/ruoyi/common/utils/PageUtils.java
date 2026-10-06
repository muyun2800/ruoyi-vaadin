package com.ruoyi.common.utils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.sql.SqlUtil;

import java.util.List;

/**
 * 分页工具类
 *
 * @author ruoyi
 */
public class PageUtils extends PageHelper {
    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }


    /**
     *
     * @param list
     * @return
     */
    public static long getTotal(List<?> list) {
        long total = 0;
        if (list instanceof Page) {
            total = ((Page) list).getTotal();
        } else {
            total = (long) list.size();
        }
        return total;
    }


}
