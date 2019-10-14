package cn.edu.buaa.act.data.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

public class DataPageable implements Serializable, Pageable {
    private static final long serialVersionUID = 11111111111L;
    // 当前页
    private Integer pagenumber = 1;
    // 当前页面条数
    private Integer pagesize = 10;
    //排序条件
    private Sort sort;

    public void setSort(Sort sort) {
        this.sort = sort;
    }
    // 当前页面
    @Override
    public int getPageNumber() {
        return getPagenumber();
    }
    // 每一页显示的条数
    @Override
    public int getPageSize() {
        return getPagesize();
    }

    @Override
    public long getOffset() {
        return (getPagenumber() - 1) * getPagesize();
    }

    // 第二页所需要增加的数量
//    @Override
//    public int getOffset() {
//        return (getPagenumber() - 1) * getPagesize();
//    }
    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    public Integer getPagenumber() {
        return pagenumber;
    }
    public void setPagenumber(Integer pagenumber) {
        this.pagenumber = pagenumber;
    }
    public Integer getPagesize() {
        return pagesize;
    }
    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }
}