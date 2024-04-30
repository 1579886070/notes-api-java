package com.zxxwl.web.core.mvc;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zxxwl.common.utils.Console;
import com.zxxwl.common.utils.Constants;
import org.apache.commons.lang.StringEscapeUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Data {
    public String value;

    public Data() {
        this.value = "";
    }

    public Data(Object value) {
        if (value != null)
            this.value = value.toString();
    }

    public Data(String[] values) {
        this.value = String.join(",", values);
    }

    public JSON toJSON(boolean isArray) {
        if (isArray)
            return JSON.parseArray(value);
        else
            return JSON.parseObject(value);
    }

    public <T> List<T> toJSON(Class<T> type) {
        return JSON.parseArray(value, type);
    }

    public long toLong() {
        return toLong(0L);
    }

    public long toLong(long df) {
        if (this.value == null || this.value.isEmpty() || !Constants.isNumberString(this.value))
            return df;

        return Long.parseLong(this.value);
    }

    public int toInt() {
        return this.toInt(0);
    }

    public float toFloat() {
        return this.toFloat(0.0f);
    }

    public double toDouble() {
        return this.toDouble(0.0);
    }

    public boolean toBool() {
        if (this.value == null || this.value.equals(""))
            return false;

        return Boolean.parseBoolean(this.value);
    }

    public Serializable toSerializable() {
        return this.value;
    }

    /**
     * FIXME 名称或条件存在争议 暂不改动 2023.09.06
     *
     * @return isEmpty
     */
    public boolean isEmpty() {
        if (this.value == null || this.value.equals("") || this.value.equals("0")
                || this.value.equalsIgnoreCase("undefined"))
            return true;

        if (!this.toBool() || this.toInt(0) == 0)
            return true;

        return false;
    }


    public int toInt(int df) {
        if (this.value == null || value.equals("") || !Constants.isNumberString(this.value))
            return df;

        return Integer.parseInt(value);
    }

    public float toFloat(float df) {
        if (this.value == null || value.equals("") || !Constants.isNumberString(this.value))
            return df;

        return Float.parseFloat(value);
    }

    public double toDouble(double df) {
        if (this.value == null || value.equals("") || !Constants.isNumberString(this.value))
            return df;

        return Double.parseDouble(value);
    }

    public String toString(String df) {
        if (this.value == null || this.value.equals(""))
            return df;

        return this.value;
    }

    public String toString() {
        return this.toString("");
    }

    public static byte[] ip2blob(String ip) {
        if (ip == null || ip.equals(""))
            return new byte[]{
                    0x00, 0x00, 0x00, 0x00
            };

        String[] items = ip.split("\\.");
        int total = items.length;
        byte[] ip2 = new byte[total];

        int i;
        int temp;
        for (i = 0; i < total; i++) {
            temp = Integer.parseInt(items[i]);
            ip2[i] = (byte) (temp % 0xff);
        }

        return ip2;
    }

    public static String blob2ip(byte[] ip) {
        if (ip == null)
            return "0.0.0.0";

        return String.format(Locale.CHINESE, "%d.%d.%d.%d", ip[0], ip[1], ip[2], ip[3]);
    }

    public static String camelCase(String word) {
        if (!word.contains("_"))
            return word;

        String[] words = word.split("_");
        StringBuilder builder = new StringBuilder();

        int i;
        int total = words.length;
        for (i = 0; i < total; i++) {
            if (i == 0)
                builder.append(words[i].toLowerCase());
            else
                builder.append(words[i].substring(0, 1).toUpperCase()).append(words[i].toLowerCase().substring(1));
        }

        return builder.toString();
    }

    public static Object bind(Object bean, Map<String, Data> params) {
        Class cn = null;
        try {
            cn = Class.forName(bean.getClass().getName());
        } catch (ClassNotFoundException e) {
            return bean;
        }

        if (cn == null)
            return bean;

        Field[] fields = bean.getClass().getDeclaredFields();
        int length = fields.length;
        if (length < 1)
            return bean;

        boolean isPK = false;
        for (Field field : fields) {
            field.setAccessible(true);

            isPK = field.isAnnotationPresent(TableId.class);

            String name = field.getName();
            String type = field.getGenericType().toString();

            Method method = null;
            if (!isPK) {
                PropertyDescriptor pd = null;
                try {
                    pd = new PropertyDescriptor(name, cn);
                } catch (IntrospectionException e) {
                    Console.log(e.getMessage());
                }

                if (pd != null)
                    method = pd.getWriteMethod();
            }

            try {
                if (!params.containsKey(name)) {
                    if (!isPK) {
                        if (method != null)
                            method.invoke(name, (Object) null);
                        else
                            field.set(bean, null);
                    }

                    continue;
                }

                switch (type) {
                    case "int":
                    case "class java.lang.Integer":
                        if (method != null)
                            method.invoke(name, params.get(name).toInt());
                        else
                            field.set(bean, params.get(name).toInt());
                        break;
                    case "boolean":
                    case "class java.lang.Boolean":
                        if (method != null)
                            method.invoke(name, params.get(name).toBool());
                        else
                            field.set(bean, params.get(name).toBool());
                        break;
                    case "long":
                    case "class java.lang.Long":
                        if (method != null)
                            method.invoke(name, params.get(name).toLong());
                        else
                            field.set(bean, params.get(name).toLong());
                        break;
                    case "class java.io.Serializable":
                        if (method != null)
                            method.invoke(name, params.get(name).toSerializable());
                        else
                            field.set(bean, params.get(name).toSerializable());
                        break;
                    case "float":
                    case "class java.lang.Float":
                        if (method != null)
                            method.invoke(name, params.get(name).toFloat());
                        else
                            field.set(bean, params.get(name).toFloat());
                        break;
                    case "double":
                    case "class java.lang.Double":
                        if (method != null)
                            method.invoke(name, params.get(name).toDouble());
                        else
                            field.set(bean, params.get(name).toDouble());
                        break;
                    case "class java.lang.String":
                    default:
                        String content = params.get(name).toString();
                        if (content.contains("<") || content.contains(">"))
                            content = StringEscapeUtils.escapeHtml(content);

                        if (method != null)
                            method.invoke(name, content);
                        else
                            field.set(bean, content);
                        break;
                }
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                Console.bug(e.getMessage());
                Console.bug(name + " bind Failed");
            }
        }

        return bean;
    }
}
