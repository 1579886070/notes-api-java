package com.zxxwl.api.ad.service.impl;

import com.zxxwl.api.ad.entity.Ad;
import com.zxxwl.api.ad.mapper.AdMapper;
import com.zxxwl.api.ad.service.AdService;
import com.zxxwl.web.core.db.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
* 广告
*
* @author zhouxin
* @since 2024-05-10 23:31:56
*/
@Service
public class AdServiceImpl extends  BaseServiceImpl<AdMapper, Ad> implements AdService {

}
