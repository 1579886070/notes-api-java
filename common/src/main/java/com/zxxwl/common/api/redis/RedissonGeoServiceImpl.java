package com.zxxwl.common.api.redis;

import com.zxxwl.common.number.NumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.OptionalGeoSearch;
import org.redisson.client.codec.Codec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 修改精度 6
 *
 * @author qingyu
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class RedissonGeoServiceImpl<V> implements RedissonGeoService<V> {
    /**
     * 经纬度 保留 SCALE 位
     */
    private static final int SCALE = 6;
    //    private static final int SCALE = 3;
    @Autowired
    private RedissonClient redissonClient;
    //    @Resource
    private Codec codec;

    /**
     * 泛型类型 需
     * com.zxxwl.user.service.config.RedissonConfig#redissonGeoService() RedissonGeoService<ObjectNode>
     * com.zxxwl.user.service.config.RedissonConfig#stringRedissonGeoService() RedissonGeoService<String>
     *
     * @param codec org.redisson.client.codec.Codec org.redisson.codec.TypedJsonJacksonCodec
     */
    public RedissonGeoServiceImpl(Codec codec) {
        this.codec = codec;
    }


    @Override
    public long add(String key, double longitude, double latitude, V member) {
        long add = redissonClient.getGeo(key).add(NumberUtil.geoDoubleScale(longitude, SCALE), NumberUtil.geoDoubleScale(latitude, SCALE), member);
        log.debug("{}", add);
        return add;
    }

    @Override
    public long add(String key, String longitude, String latitude, V member) {
        long add = redissonClient.getGeo(key).add(NumberUtil.geoStringToDouble(longitude, SCALE), NumberUtil.geoStringToDouble(latitude, SCALE), member);
        log.debug("{}", add);
        return add;
    }

    @Override
    public long add(String key, GeoEntry... entries) {
        return redissonClient.getGeo(key).add(entries);
    }

    @SafeVarargs
    @Override
    public final Map<V, GeoPosition> getPointsByMember(String key, V... member) {
        RGeo<V> geo = redissonClient.getGeo(key, codec);
        return geo.pos(member);
    }

    @SafeVarargs
    @Override
    public final Map<V, String> getHashByMember(String key, V... member) {
        RGeo<V> geo = redissonClient.getGeo(key, codec);
        return geo.hash(member);
    }

    @Override
    public Double getDistance(String key, V member, V member2, GeoUnit unit) {
        RGeo<V> geo = redissonClient.getGeo(key, codec);
        return geo.dist(member, member2, unit);
    }

    @Override
    public List<V> getRadiusByMember(String key, V member, double radius, Integer limit, GeoUnit unit) {
        RGeo<V> geo = redissonClient.getGeo(key, codec);
        OptionalGeoSearch count = GeoSearchArgs.from(member)
                .radius(radius, unit)
                .order(GeoOrder.ASC);
        if (!Objects.isNull(limit)) {
            count.count(limit);
        }
        return geo.search(count);
    }

    @Override
    public Map<V, Double> getRadiusByMemberWithDistance(String key, V member, double radius, Integer limit, GeoUnit unit) {
        RGeo<V> geo = redissonClient.getGeo(key, codec);
        OptionalGeoSearch count = GeoSearchArgs.from(member)
                .radius(radius, unit)
                .order(GeoOrder.ASC);
        if (!Objects.isNull(limit)) {
            count.count(limit);
        }
        return geo.searchWithDistance(count);
    }

    @Override
    public Map<V, GeoPosition> getRadiusByMemberWithPosition(String key, V member, double radius, Integer limit, GeoUnit unit) {
        RGeo<V> geo = redissonClient.getGeo(key, codec);
        OptionalGeoSearch count = GeoSearchArgs.from(member)
                .radius(radius, unit)
                .order(GeoOrder.ASC);
        if (!Objects.isNull(limit)) {
            count.count(limit);
        }
        return geo.searchWithPosition(count);
    }


    @Override
    public List<V> getRadiusByCircle(String key, double longitude, double latitude, double radius, Integer limit, GeoUnit unit) {
        RGeo<V> geo = redissonClient.getGeo(key, codec);
        OptionalGeoSearch count = GeoSearchArgs.from(NumberUtil.geoDoubleScale(longitude, SCALE), NumberUtil.geoDoubleScale(latitude, SCALE))
                .radius(radius, unit)
                .order(GeoOrder.ASC);
        if (!Objects.isNull(limit)) {
            count.count(limit);
        }
        return geo.search(count);
    }

    @Override
    public List<V> getRadiusByCircle(String key, String longitude, String latitude, double radius, Integer limit, GeoUnit unit) {
        return getRadiusByCircle(
                key,
                NumberUtil.geoStringToDouble(longitude, SCALE),
                NumberUtil.geoStringToDouble(latitude, SCALE),
                radius,
                limit,
                unit
        );
    }


    @Override
    public Map<V, Double> getRadiusByCircleWithDistance(String key, double longitude, double latitude, double radius, Integer limit, GeoUnit unit) {
        RGeo<V> geo = redissonClient.getGeo(key, codec);
        OptionalGeoSearch count = GeoSearchArgs.from(NumberUtil.geoDoubleScale(longitude, SCALE), NumberUtil.geoDoubleScale(latitude, SCALE))
                .radius(radius, unit)
                .order(GeoOrder.ASC);
        if (!Objects.isNull(limit)) {
            count.count(limit);
        }
        return geo.searchWithDistance(count);
    }

    @Override
    public Map<V, Double> getRadiusByCircleWithDistance(String key, String longitude, String latitude, double radius, Integer limit, GeoUnit unit) {
        return getRadiusByCircleWithDistance(key,
                NumberUtil.geoStringToDouble(longitude, SCALE),
                NumberUtil.geoStringToDouble(latitude, SCALE),
                radius,
                limit,
                unit
        );
    }

    @Override
    public Map<V, GeoPosition> getRadiusByCircleWithPosition(String key, double longitude, double latitude, double radius, Integer limit, GeoUnit unit) {
        RGeo<V> geo = redissonClient.getGeo(key, codec);
        OptionalGeoSearch count = GeoSearchArgs.from(NumberUtil.geoDoubleScale(longitude, SCALE), NumberUtil.geoDoubleScale(latitude, SCALE))
                .radius(radius, unit)
                .order(GeoOrder.ASC);
        if (!Objects.isNull(limit)) {
            count.count(limit);
        }
        return geo.searchWithPosition(count);
    }

    @Override
    public boolean del(String key) {
        RKeys keys = redissonClient.getKeys();
        return keys.delete(key) == 1L;
    }
}
