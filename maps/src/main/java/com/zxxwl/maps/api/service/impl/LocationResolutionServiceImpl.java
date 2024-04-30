package com.zxxwl.maps.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zxxwl.common.api.aliyun.service.ALiYunOpenApiService;
import com.zxxwl.common.web.http.Response;
import com.zxxwl.maps.api.service.LocationResolutionService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LocationResolutionServiceImpl implements LocationResolutionService {
    private final ALiYunOpenApiService aLiYunOpenApiService;

    @Override
    public Response getCoordinate(String address) {
        JsonNode coordinate = aLiYunOpenApiService.getCoordinate(address);
        if (coordinate.path("status").asInt() == 0) {
            String province = coordinate.path("result").path("address_components").path("province").asText();
            String city = coordinate.path("result").path("address_components").path("city").asText();
            String district = coordinate.path("result").path("address_components").path("district").asText();
            return Response.ok().content(new JSONObject() {{
                put("province", province);
                put("city", city);
                put("district", district);
            }});

        } else {
            Response.fail().message("获取经纬度失败");
        }
        return Response.ok();

    }

    @Override
    public Response getAddress(String lng, String lat, int poi) {
        JsonNode coordinate = aLiYunOpenApiService.getAddress(lng, lat, poi);

        if (coordinate.path("status").asInt() == 0) {
            String province = coordinate.path("result").path("address_component").path("province").asText();
            String city = coordinate.path("result").path("address_component").path("city").asText();
            String county = coordinate.path("result").path("address_component").path("district").asText();
            String street = coordinate.path("result").path("address_component").path("street").asText();
            String details = coordinate.path("result").path("address").asText();
            String longitude = coordinate.path("result").path("location").path("lng").asText();
            String latitude = coordinate.path("result").path("location").path("lat").asText();

            return Response.ok().content(new JSONObject() {{
                put("longitude", longitude);
                put("latitude", latitude);
                put("details", details);
                put("province", province);
                put("city", city);
                put("county", county);
                put("street", street);
            }});

        } else if (coordinate.path("status").asInt() == 356) {
            Response.fail().message("纬度不能超过±90");
        } else if (coordinate.path("status").asInt() == 366) {
            Response.fail().message("经度不能超过±180");
        }
        return Response.ok();
    }
}
