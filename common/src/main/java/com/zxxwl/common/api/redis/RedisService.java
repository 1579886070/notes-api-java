package com.zxxwl.common.api.redis;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

/**
 * 使用规范
 key,hashKey 均使用 {@code String} 类型(后期有更好方案 再替换)</p>
 value 实际会存为 {@code Object} 类型 系列化反序列化需要注意</p>
 java 泛型擦除 (jvm 不支持泛型)-> {@code RedisTemplate} 部分类型丢失 eg.: {@code Long} 变 {@code Integer}
 * 若 value 为 8种基本类型对应的包装类型时:
 * 若为 整形时 取 <blockquote><pre> Integer: [Byte,Short,Integer,Long]</pre></blockquote>
 * 若为 浮点型时 取 <blockquote><pre> Double: [Double,Float]</pre></blockquote>
 * 若使用 其他类型 建议 转为字符串后存,取时反序列化
 * 单对象 会变成 {@code LinkedHashMap}
 * {@code List<T>} 不影响(<T> 都进行了擦除)
  * 建议:
 * 使用相同的json 序列化配置,进行序列化反序列化
 * 方案一: 对象 序列化|转为 json string 存储 {@code writeValueAsString()},反序列化时 readValue;
 * 方案二: 存json ,取 {@code convertValue()}
 *
 * @author qingyu 2022.12.01,2023.02.21
 * @link <a href="https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#redis:serializer">参考spring redis:serializer</a>
 */
//@Component
public interface RedisService<V> {
    /**
     * 0|1
     *
     * @param key key
     * @return exists
     */
    boolean exists(String key);

    /**
     * 保存属性
     */
    boolean set(String key, V value, long time);

    /**
     * 保存属性
     */
    void set(String key, V value);

    /**
     * 获取属性
     */
    V get(String key);

    /**
     * 删除属性
     */
    Boolean del(String key);

    /**
     * 批量删除属性
     */
    Long del(List<String> keys);

    /**
     * 设置过期时间
     */
    Boolean expire(String key, long time);

    /**
     * 获取过期时间
     */
    Long getExpire(String key);

    /**
     * 判断是否有该属性
     */
    Boolean hasKey(String key);

    /**
     * 按delta递增
     */
    Long incr(String key, long delta);

    /**
     * 按delta递减
     */
    Long decr(String key, long delta);

    boolean hExists(String key, String hashKey);

    /**
     * 获取Hash结构中的属性
     */
    V hGet(String key, String hashKey);
    Optional<V> hGet2(String key, String hashKey);
    /**
     * 用于获取非标准json string
     * 如 无引号 json string:sss.ss.ss
     * @param key key
     * @param hashKey hashKey
     * @return string
     */
    V hGetString(String key, String hashKey);

    /**
     * 向Hash结构中放入一个属性
     */
    Boolean hSet(String key, String hashKey, V value, long time);

    /**
     * 向Hash结构中放入一个属性
     */
    void hSet(String key, String hashKey, V value);
    void hSetString(String key, String hashKey, V value);

    /**
     * 直接获取整个Hash结构
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * 直接设置整个Hash结构
     */
    Boolean hSetAll(String key, Map<String, V> map, long time);

    /**
     * 直接设置整个Hash结构
     */
    void hSetAll(String key, Map<String, V> map);

    /**
     * 删除Hash结构中的属性
     */
    void hDel(String key, Object... hashKey);

    /**
     * 判断Hash结构中是否有该属性
     */
    Boolean hHasKey(String key, String hashKey);

    /**
     * Hash结构中属性递增
     */
    Long hIncr(String key, String hashKey, Long delta);

    /**
     * Hash结构中属性递减
     */
    Long hDecr(String key, String hashKey, Long delta);


    /**
     * 向Set结构中添加属性
     */
    Long sAdd(String key, V... values);

    /**
     * 向Set结构中添加属性
     */
    Long sAdd(String key, long time, V... values);


    /**
     * 获取Set结构的长度
     */
    Long sSize(String key);

    /**
     * 删除Set结构中的属性
     */
    Long sRemove(String key, Object... values);

    /**
     * 获取List结构中的属性
     */
    List<V> lRange(String key, long start, long end);

    /**
     * 获取List结构的长度
     */
    Long lSize(String key);

    /**
     * 根据索引获取List中的属性
     */
    V lIndex(String key, long index);

    /**
     * 向List结构中添加属性
     */
    Long lPush(String key, V value);

    Long lPushAll(String key, Collection<V> value);

    /**
     * 向List结构中添加属性
     */
    Long lPush(String key, V value, long time);

    /**
     * 向List结构中批量添加属性
     */
//    Long lPushAll(String key, V... values);

    /**
     * 向List结构中批量添加属性
     */
//    Long lPushAll(String key, Long time, V... values);

    /**
     * 从List结构中移除属性
     */
    Long lRemove(String key, long count, V value);

    DataType type(String key);

    Long zSize(String key);

    Boolean zAdd(String key, V value, double score);

    Set<V> zGet(String key, long start, long stop);
    Set<ZSetOperations.TypedTuple<V>> rangeWithScores(String key, long start, long stop);
    Long zCount(String key, long start);
    Long zCount(String key, long start,long stop);
    Long zDel(String key,long stop);
    Long zDel(String key,long start,long stop);
    Long zRem(String key,V value);

    Long zDelAll(String key);

}
