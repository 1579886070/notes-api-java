package com.zxxwl.api.ad.controller;
import com.zxxwl.api.ad.service.AdService;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import com.zxxwl.web.core.mvc.BaseController;

/**
* 广告
*
* @author zhouxin
* @since 2024-05-10 23:31:56
*/
@RequiredArgsConstructor
@RestController
@RequestMapping("/ad/ad")
public class AdController extends  BaseController<AdService> {
    private final AdService baseService;

}
