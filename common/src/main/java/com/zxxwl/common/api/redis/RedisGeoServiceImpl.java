package com.zxxwl.common.api.redis;

import com.zxxwl.common.number.NumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author qingyu
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class RedisGeoServiceImpl<V> implements RedisGeoService<V> {
    /**
     * 经纬度 保留 SCALE 位
     */
    private static final int SCALE = 6;
    private final RedisTemplate<String, V> redisTemplate;

    @Override
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Long add(String key, double longitude, double latitude, V member) {
        Long row = redisTemplate.opsForGeo().add(key, new Point(NumberUtil.geoDoubleScale(longitude, SCALE), NumberUtil.geoDoubleScale(latitude, SCALE)), member);
        log.debug("{}", row);
        return row;
    }

    @Override
    public Long add(String key, Map<V, Point> memberMap) {

        Long row = redisTemplate.opsForGeo().add(key, memberMap);
        log.debug("{}", row);
        return row;
    }

    @SafeVarargs
    @Override
    public final List<Point> getPointsByMember(String key, V... member) {
        List<Point> points = redisTemplate.opsForGeo().position(key, member);
        log.debug("{}", points);
        return points;
    }

    @SafeVarargs
    @Override
    public final List<String> getHashByMember(String key, V... member) {
        List<String> hash = redisTemplate.opsForGeo().hash(key, member);
        log.debug("{}", hash);
        return hash;
    }

    @Override
    public Distance getDistance(String key, V member, V member2, Metric unit) {
        Distance distance = redisTemplate.opsForGeo().distance(key, member, member2, RedisGeoCommands.DistanceUnit.METERS);
        log.debug("{}", distance);
        return distance;
    }

    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<V>> getRadiusByMember(String key, V member, double radius, Integer limit, Metric unit) {
        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending().limit(limit);
        GeoResults<RedisGeoCommands.GeoLocation<V>> radius1 = redisTemplate.opsForGeo()
                .radius(key,
                        member,
                        new Distance(radius, unit),
                        args
                );
        log.debug("{}", radius1);
        return radius1;
    }

    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<V>> getRadiusByCircle(String key, double longitude, double latitude, double radius, Integer limit, Metric unit) {
        Circle circle = new Circle(new Point(NumberUtil.geoDoubleScale(longitude, SCALE), NumberUtil.geoDoubleScale(latitude, SCALE)), new Distance(radius, unit));

        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending().limit(limit);
        GeoResults<RedisGeoCommands.GeoLocation<V>> radius1 = redisTemplate.opsForGeo()
                .radius(key,
                        circle,
                        args
                );

        log.debug("{}", radius1);
        return radius1;
    }
}
