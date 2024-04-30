package com.zxxwl.web.core.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zxxwl.common.utils.Console;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Arrays;
import java.util.Set;

public class WhereBuilder {

    private String relation = "AND";

    private JSON query;

    public WhereBuilder(JSONObject query){
        this.query = query;
    }

    public String parse(){
        if( query instanceof JSONArray )
            return parseArray((JSONArray) query, relation);
        else
            return parseObject((JSONObject) query, relation);
    }

    private String parseArray(JSONArray params, String relation){
        int size = params.size();

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < size; i++)
            builder.append(" ").append(relation).append(" (").append(parseObject(params.getJSONObject(i), "AND"))
                    .append(")");

        return builder.toString().substring(relation.length() + 1);
    }

    private String parseObject(JSONObject params, String relation){
        Set<String> keys = params.keySet();

        StringBuilder builder = new StringBuilder();
        for(String key : keys){
            if(key.length() > 4 && key.substring(0, 5).equals("$part")){
                builder.append(" ").append(relation).append("(")
                        .append(parseObject(params.getJSONObject(key), "AND")).append(")");
                continue;
            }

            switch (key){
                case "$and":
                    builder.append(" ").append(relation).append("(")
                            .append(parseArray(params.getJSONArray(key), "AND")).append(")");
                    break;
                case "$or":
                    builder.append(" ").append(relation).append("(")
                            .append(parseArray(params.getJSONArray(key), "OR")).append(")");
                    break;
                default:
                    builder.append(" ").append(relation).append("(").append(build(key, params.get(key))).append(")");
                    break;
            }
        }

        return builder.toString().substring(relation.length() + 1);
    }

    private String escape(Object value){
        if( value instanceof String )
            return "'" + StringEscapeUtils.escapeSql(value.toString()) + "'";
        else if( value instanceof Number )
            return value.toString();
        else if( value.getClass().isArray()){
            StringBuilder builder = new StringBuilder();
            for (Object item : (Object[]) value)
                builder.append(",").append(escape(item));

            return builder.toString().substring(1);
        } else if(value instanceof Set){
            StringBuilder builder = new StringBuilder();
            for (Object item : (Set) value)
                builder.append(",").append(escape(item));

            return builder.toString().substring(1);
        }

        return value.toString();
    }

    private String build(String key, Object value){
        StringBuilder builder = new StringBuilder();
        if( !key.equalsIgnoreCase("$exists") && !key.equalsIgnoreCase("$nexists") )
            builder.append(key);

        if( value instanceof JSONObject ){
            JSONObject params = (JSONObject) value;
            Set<String> keys = params.keySet();

            if( keys.size() < 1 )
                return "";

            String oper;
            Object val;

            String[] filter = new String[]{
                    "$exists",
                    "$nexists"
            };
            if(Arrays.asList(filter).contains(key) ) {
                oper = key;
                val = value;
            }
            else{
                oper = keys.iterator().next();
                val  = params.get(oper);
            }

            switch (oper){
                case "$nin":
                    builder.append(" NOT");
                case "$in":{
                    try{
                        JSONArray vals = (JSONArray) val;
                        int total = vals.size();
                        builder.append(" IN (");
                        for(int i = 0; i < total; i++){
                            if(i > 0)
                                builder.append(",");
                            builder.append(escape(vals.get(i)));
                        }

                        builder.append(")");
                    }catch (ClassCastException e){
                        try{
                            Object[] vals = (Object[])val;
                            int total = vals.length;
                            builder.append(" IN (");
                            for(int i = 0; i < total; i++){
                                if(i > 0)
                                    builder.append(",");
                                builder.append(escape(vals[i]));
                            }

                            builder.append(")");
                        } catch (ClassCastException e1){
                            Console.log(e.getMessage());
                        }
                    }
                } break;
                case "$nbtw":
                    builder.append(" NOT");
                case "$btw":{
                    JSONArray vals = (JSONArray) val;
                    builder.append(" BETWEEN ").append(vals.getLongValue(0)).append(" AND ")
                            .append(vals.getLongValue(1));
                }
                    break;
                case "$nregex":
                    builder.append(" NOT");
                case "$regex":
                    builder.append(" LIKE ").append(escape(val.toString().replaceAll("#", "%")));
                    break;
                case "$null":
                    if( (Boolean) val )
                        builder.append(" IS NULL");
                    else
                        builder.append(" IS NOT NULL");
                    break;
                case "$gt":
                    builder.append(" > ").append(escape(val));
                    break;
                case "$gte":
                    builder.append(" >= ").append(escape(val));
                    break;
                case "$lt":
                    builder.append(" < ").append(escape(val));
                    break;
                case "$lte":
                    builder.append(" <= ").append(escape(val));
                    break;
                case "$neq":
                    builder.append(" <> ").append(escape(val));
                    break;
                case "$nexists":
                    builder.append(" NOT");
                case "$exists":
                    JSONObject conf = null;
                    if( val instanceof String )
                        conf = JSONObject.parseObject(val.toString());
                    else if ( val instanceof JSONObject )
                        conf = (JSONObject) val;

                    if( conf != null ){
                        builder.append(" EXISTS (SELECT ").append(conf.getString("fields")).append(" FROM ")
                                .append(conf.getString("table")).append(" ")
                                .append(conf.getString("alias"))
                                .append(" WHERE ")
                                .append(conf.getString("cond")).append(")");
                    }
                    break;
                case "$exp":
                    builder.append(escape(val));
                    break;
                case "$eq":
                default:
                    builder.append(" = ").append(escape(val));
                    break;
            }

            return builder.toString();
        }
        else
            return builder.append(" = ").append(escape(value)).toString();
    }
}
