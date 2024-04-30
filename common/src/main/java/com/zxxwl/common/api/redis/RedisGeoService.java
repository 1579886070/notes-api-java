package com.zxxwl.common.api.redis;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;

import java.util.List;
import java.util.Map;

/**
 * @author qingyu 2023.03.17
 */
public interface RedisGeoService<V> {
    Boolean del(String key);

    Long add(String key, double longitude, double latitude, V member);

    Long add(String key, Map<V, Point> memberMap);

    List<Point> getPointsByMember(String key, V... member);

    List<String> getHashByMember(String key, V... member);

    Distance getDistance(String key, V member, V member2, Metric unit);

    GeoResults<RedisGeoCommands.GeoLocation<V>> getRadiusByMember(String key, V member, double radius, Integer limit, Metric unit);

    GeoResults<RedisGeoCommands.GeoLocation<V>> getRadiusByCircle(String key, double longitude, double latitude, double radius, Integer limit, Metric unit);
}
