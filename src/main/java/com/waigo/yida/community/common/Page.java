package com.waigo.yida.community.common;

/**
 * author waigo
 * create 2021-10-01 23:57
 */

/**
 * 分页数据
 */
public class Page {
    /**
     * 当前用于分页的元素的id，比如查帖子列表的时候就是用户id，查评论列表的时候就是帖子id
     */
    private int sourceId;
    /**
     * 一页几条
     */
    private int pageSize = 10;
    /**
     * 总页数
     */
    private int pageAll;
    /**
     * 当前页
     */
    private int current;
    /**
     * 分页数据所在页面地址
     */
    private String path;

    /**
     * 当前分页中开始的页码
     */
    private int pageFrom = 1;
    /**
     * 分页组件中页数，5就是有1.2.3...5这样
     */
    private int pageHelperSize = 5;

    /**
     * 具体如何排序可以按照业务来设置这个值
     */
    private int orderMode;
    public Page() {

    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageAll() {
        return pageAll;
    }

    public void setPageAll(int pageAll) {
        this.pageAll = pageAll;
    }
    public void setPageAllByRows(long rows){
        this.pageAll = (int) (rows/pageSize);
        pageAll+=rows%pageSize==0?0:1;
    }

    /**
     * 当前页
     * @return
     */
    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void checkCurrent() {
        if (current <= 0) {
            current = 1;
        }
        if (current > pageAll) {
            current = pageAll;
        }
    }

    public int getPageFrom() {
        return pageFrom;
    }

    public void setPageFrom(int pageFrom) {
        this.pageFrom = pageFrom;
    }

    public int getPageHelperSize() {
        return pageHelperSize;
    }

    public void setPageHelperSize(int pageHelperSize) {
        this.pageHelperSize = pageHelperSize;
    }

    public int getOrderMode() {
        return orderMode;
    }

    public void setOrderMode(int orderMode) {
        this.orderMode = orderMode;
    }

    /**
     * 返回当前分页中的最后一页
     *
     * @return
     */
    public int getPageEnd() {
        return Math.min(pageAll, pageFrom + pageHelperSize - 1);
    }

    public int calcPageFrom(int pageEnd) {
        pageEnd = Math.max(pageEnd, pageAll);
        return Math.max(1, pageEnd - pageHelperSize + 1);
    }

    //下一页
    public int getNextPage() {
        return (current == pageFrom + pageHelperSize - 1) && pageFrom < pageAll ? pageFrom + 1 : pageFrom;
    }

    //上一页
    public int getPrePage() {
        return current == pageFrom && pageFrom > 1 ? pageFrom - 1 : pageFrom;
    }

    @Override
    public String toString() {
        return "Page{" + "sourceId=" + sourceId + ", pageSize=" + pageSize + ", pageAll=" + pageAll + ", current=" + current + ", path='" + path + '\'' + ", pageFrom=" + pageFrom + ", pageHelperSize=" + pageHelperSize + '}';
    }

    public void checkPageFrom() {
        pageFrom = Math.max(current - pageHelperSize + 1, pageFrom);
    }

    /**
     * 当前页的偏移量
     *
     * @return
     */
    public int getOffset() {
        int i = (current - 1) * pageSize;
        return Math.max(i,0);
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public void init(int pageAll, String path) {
        this.setPageAll(pageAll);
        this.setPath(path);
        this.checkCurrent();
        this.checkPageFrom();
    }

}
