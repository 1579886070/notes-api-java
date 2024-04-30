package com.zxxwl.web.core.db;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zxxwl.common.utils.Console;
import com.zxxwl.common.utils.Constants;
import com.zxxwl.web.core.http.Request;
import com.zxxwl.web.core.mvc.BaseEntity;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryBuilder {
    public static final String DESC = "DESC";
    public static final String ASC = "ASC";

    public static final String COUNT_NAME = "ARCP_ROWCOUNT";

    private String columns = null;

    private String params = null;

    private String countField = "id";

    protected String[] defaultOrders = null;

    private Map<String, String> orders = new LinkedHashMap<>();

    private String groupBy = null;

    private String having = null;

    private List<QueryTable> tables = null;

    private List<QueryTable> joins = null;

    public int page = 0;

    public int size = 20;

    private boolean distinct = true;

    private QueryHelper helper = null;

    public QueryBuilder setCountField(String field){
        this.countField = field;
        return this;
    }

    public QueryBuilder setDistinct(boolean distinct){
        this.distinct = distinct;
        return this;
    }

    public String getCountField(){
        StringBuilder builder = new StringBuilder();
        builder.append("COUNT(");

        if( this.distinct )
            builder.append("DISTINCT ");

        if( this.tables.size() > 0 ){
            QueryTable table = this.tables.get(0);
            if( table.alias != null )
                builder.append(table.alias).append(".").append(countField);
            else
                builder.append(countField);
        }
        else
            builder.append(countField);

        return builder
                .append(") AS ").append(COUNT_NAME)
                .toString();
    }

    public QueryBuilder setAlias(String alias){
        if( this.tables != null && this.tables.size() > 0 ) {
            QueryTable table = this.tables.get(0);
            table.alias = alias;
            this.tables.set(0, table);
        }
        return this;
    }

    public QueryBuilder addColumns(String... columns){
        if(this.columns == null || this.columns.equals(""))
            this.columns = String.join(",", columns);
        else
            this.columns += "," + String.join(",", columns);

        return this;
    }

    public QueryBuilder setColumns(String ...columns){
        this.columns = String.join(",", columns);
        return this;
    }

    public String getColumns(){
        return this.columns;
    }

    public QueryBuilder setOrders(String field, String order){
        this.orders.put(field, order);
        return this;
    }

    public boolean hasOrder(String key){
        return this.orders.containsKey(key);
    }

    public Map<String, String> getOrders(){
        return this.orders;
    }

    public QueryBuilder setLimit(int page, int size){
        this.page = page;
        this.size = size;
        return this;
    }

    public int getOffset(){
        return this.size * this.page;
    }

    public int getLimit(){
        return this.size;
    }

    public String getGroupBy(){
        return this.groupBy;
    }

    public QueryBuilder setGroupBy(String group){
        this.groupBy = group;
        return this;
    }

    public QueryBuilder setHaving(String having){
        this.having = having;
        return this;
    }

    public QueryBuilder from(String table){
        if( this.tables == null )
            this.tables = new ArrayList<>();

        this.tables.add(new QueryTable(table, null));
        return this;
    }

    public QueryBuilder addFrom(String table, String alias){
        if( this.tables == null )
            this.tables = new ArrayList<>();

        this.tables.add(new QueryTable(table, alias));
        return this;
    }

    public QueryBuilder setJoin(Class<? extends BaseEntity> entity, String alias, String cond){
        return this.setJoin(entity, alias, cond, "LEFT");
    }

    public QueryBuilder setJoin(Class<? extends BaseEntity> entity, String alias, String cond, String method){
        return this.setJoin(BaseEntity.table(entity), alias, cond, method);
    }

    public QueryBuilder setJoin(String table, String alias, String cond){
        if( this.joins == null )
            this.joins = new ArrayList<>();

        this.joins.add(new QueryTable(table, alias, cond));
        return this;
    }

    public QueryBuilder setJoin(String table, String alias, String cond, String method){
        if( this.joins == null )
            this.joins = new ArrayList<>();

        this.joins.add(new QueryTable(method, table, alias, cond));
        return this;
    }

    public String getHaving(){
        return this.having;
    }

    public List<QueryTable> getFrom(){
        return this.tables;
    }

    public QueryBuilder useRequest(Request request){
        if( request.hasQuery("page") )
            this.page = request.getQuery("page").toInt();

        if( request.hasQuery("size") )
            this.size = request.getQuery("size").toInt(20);

        if( request.hasQuery("query") ){
            String query = URLDecoder.decode(request.getQuery("query").toString(""),
                    Constants.DefaultCharset);

            if( !query.equals("") ){
                JSONObject json = null;
                try{
                    json = JSONObject.parseObject(query);
                }catch (JSONException e){
                    Console.log(e.getMessage());
                }

                if(this.helper != null)
                    this.setParams(this.helper.process(json));
                else if(json != null)
                    this.setParams(json);
            }
        } else if(this.helper != null)
            this.setParams(this.helper.process(null));

        if( request.hasQuery("order") ){
            String order = URLDecoder.decode(request.getQuery("order").toString(""),
                    Constants.DefaultCharset);

            if( !order.equals("{}") ){
                JSONObject json = null;
                try {
                    json = JSONObject.parseObject(order);
                }catch (JSONException e){
                    Console.log(e.getMessage());
                }
                if( json == null )
                    return this;

                for(String field : json.keySet()){
                    Integer sort = json.getInteger(field);
                    this.setOrders(field, (sort == null || sort == 1) ? "ASC" : "DESC");
                }
            }
        }

        if(request.hasQuery("columns"))
            this.setColumns(request.getQuery("columns").toString());

        return this;
    }

    public QueryBuilder setParams(JSONObject query){
        if(query == null)
            return this;

        StringBuilder builder = new StringBuilder();
        if( this.params != null && !this.params.equals(""))
            builder.append(this.params).append(" AND ");

        if(!query.toJSONString().equals("{}"))
            builder.append(new WhereBuilder(query).parse());

        this.params = builder.toString();

        return this;
    }

    public long count(BaseMapper mapper){
        List items = mapper.paginationQuery(getCountField(), tables, joins, params,
                null, null, null, 0, 1);

        if( items != null && items.size() > 0 ){
            Map<String, Object> rs = (Map<String, Object>) items.get(0);
//            return (long) rs.get(COUNT_NAME);
            return (long) rs.get("arcpRowcount");
        }

        return 0L;
    }

    public List execute(BaseMapper mapper, String ...columns){
        String fields = null;
        if( columns.length > 0 )
            fields = String.join(",", columns);
        else
            fields = this.columns;

        StringBuilder orderBy = new StringBuilder();
        if( this.defaultOrders != null )
            for(String order : defaultOrders)
                orderBy.append(",").append(order);

        if( orders != null && orders.size() > 0 ){
            for (String name : orders.keySet())
                orderBy.append(",").append(name).append(" ").append(orders.get(name));
        }

        String order = orderBy.toString();
        return mapper.paginationQuery(
                    fields,
                    tables,
                    joins,
                    params,
                    order.length() == 0 ? null : order.substring(1),
                    groupBy,
                    having,
                    page * size,
                    size
                );
    }

    public abstract static class QueryHelper{
        protected abstract JSONObject process(JSONObject params);
    }

    public QueryBuilder setQueryHelper(QueryHelper helper){
        this.helper = helper;
        return this;
    }
}
