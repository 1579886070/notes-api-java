package com.zxxwl.web.core.db;

public class QueryTable {
    public String alias;
    public String name;
    public String method;
    public String cond;

    public QueryTable(String name, String alias){
        this.alias = alias;
        this.name = name;
    }

    public QueryTable(String name, String alias, String cond){
        this.method = "LEFT";
        this.name = name;
        this.alias = alias;
        this.cond = cond;
    }

    public QueryTable(String method, String name, String alias, String cond){
        this.method = method;
        this.name = name;
        this.alias = alias;
        this.cond = cond;
    }
}
