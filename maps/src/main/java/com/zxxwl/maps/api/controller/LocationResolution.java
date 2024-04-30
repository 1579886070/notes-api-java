package com.zxxwl.maps.api.controller;

import com.zxxwl.common.web.http.Response;
import com.zxxwl.maps.api.service.LocationResolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/location")
public class LocationResolution {
    private final LocationResolutionService locationResolutionService;

    /**
     * 查询地址经纬度信息
     *
     * @param address 地址
     * @return R
     */
    @GetMapping("coordinate")
    public ResponseEntity<?> getCoordinate(@RequestParam(value = "address") String address) {
        Response result = this.locationResolutionService.getCoordinate(address);
        return result.send();
    }

    /**
     * 根据经纬度查询省市区
     *
     * @param lng 经度
     * @param lat 维度
     * @param poi 是否返回周围商圈
     * @return R
     */
    @GetMapping("address/info")
    public ResponseEntity<?> getAddress(@RequestParam(value = "lng") String lng,
                                        @RequestParam(value = "lat") String lat,
                                        @RequestParam(value = "poi", defaultValue = "0") int poi) {
        Response result = this.locationResolutionService.getAddress(lng, lat, poi);
        return result.send();
    }
}
