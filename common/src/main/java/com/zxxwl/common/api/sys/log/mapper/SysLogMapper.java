package com.zxxwl.common.api.sys.log.mapper;

import com.zxxwl.common.api.sys.log.entity.SysLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysLogMapper {
    @Insert("""
            INSERT INTO system_log(id, member_id, type,
            source, title, content,
            df, create_time, update_time)
            VALUE (#{id},#{memberId},#{type},
            #{source},#{title},#{content},
            #{df},#{createTime},#{updateTime})
            """)
    int add(SysLog sysLog);
}
