package com.zxxwl.config.mp;


import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.zxxwl.common.random.IdUtils;
import org.springframework.stereotype.Component;

/**
 * @apiNote <a href="https://baomidou.com/pages/568eb2/#spring-boot">自定义id生成器</a>
 */
@Component
public class CustomMpIdGenerator implements IdentifierGenerator {
    /**
     * IdType.ASSIGN_ID
     *
     * @param entity 实体
     * @return nextId
     */
    @Override
    public Long nextId(Object entity) {
        //返回生成的id值即可.
        return IdUtils.getSnowFlakeNextId();
    }

    /**
     * IdType.ASSIGN_UUID
     *
     * @param entity 实体
     * @return nextUUID
     */
    @Override
    public String nextUUID(Object entity) {
        return IdUtils.getSnowFlakeId();
    }
}