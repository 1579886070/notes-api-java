package com.zxxwl.common.api.redis;

import org.redisson.api.GeoEntry;
import org.redisson.api.GeoPosition;
import org.redisson.api.GeoUnit;

import java.util.List;
import java.util.Map;

/**
 * 经纬度精度报错
 * FIXME string 经纬度会进行 修改精度,double 不会修改精度
 * @author qingyu 2023.03.17
 */
public interface RedissonGeoService<V> {


    /**
     * @param key       cacheKey
     * @param longitude longitude
     * @param latitude  latitude
     * @param member    member
     * @return row
     */
    long add(String key, double longitude, double latitude, V member);
    long add(String key, String longitude, String latitude, V member);

    long add(String key, GeoEntry... entries);

    Map<V, GeoPosition> getPointsByMember(String key, V... member);

    Map<V, String> getHashByMember(String key, V... member);

    Double getDistance(String key, V member, V member2, GeoUnit unit);

    List<V> getRadiusByMember(String key, V member, double radius, Integer limit, GeoUnit unit);

    Map<V, Double> getRadiusByMemberWithDistance(String key, V member, double radius, Integer limit, GeoUnit unit);

    Map<V, GeoPosition> getRadiusByMemberWithPosition(String key, V member, double radius, Integer limit, GeoUnit unit);

    List<V> getRadiusByCircle(String key, double longitude, double latitude, double radius, Integer limit, GeoUnit unit);

    List<V> getRadiusByCircle(String key, String longitude, String latitude, double radius, Integer limit, GeoUnit unit);

    Map<V, Double> getRadiusByCircleWithDistance(String key, double longitude, double latitude, double radius, Integer limit, GeoUnit unit);

    Map<V, Double> getRadiusByCircleWithDistance(String key, String longitude, String latitude, double radius, Integer limit, GeoUnit unit);

    Map<V, GeoPosition> getRadiusByCircleWithPosition(String key, double longitude, double latitude, double radius, Integer limit, GeoUnit unit);

    boolean del(String key);
}
