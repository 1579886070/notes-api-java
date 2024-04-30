package com.zxxwl.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;

public class Pagination {
    public long total = 0L;
    public long page = 0L;
    public long size = 20L;

    public String items = "[]";

    private Pagination(){
        this.total = 0L;
        this.page = 0L;
        this.size = 20L;
    }

    public static Pagination init(){
        return new Pagination();
    }

    public static Pagination init(long total, long page, long size){
        Pagination pagination = new Pagination();
        pagination.total = total;
        pagination.size = size;
        pagination.page = page;

        return pagination;
    }

    public Pagination setItems(List<Map<String, Object>> items){
        this.items = JSON.toJSONString(items);
        return this;
    }

    public Pagination setItems(JSONArray items){
        this.items = items.toJSONString();
        return this;
    }

    public boolean hasNext(){
        return (int)(Math.ceil(this.total * 1.0 / this.size)) > this.page;
    }

    public long getTotal(){
        return total;
    }

    public long getPage(){
        return page;
    }

    public long getSize(){
        return size;
    }

    public String getItems(){
        return this.items;
    }
}
