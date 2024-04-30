package com.zxxwl.maps.api.service;

import com.zxxwl.common.web.http.Response;

public interface LocationResolutionService {

    Response getCoordinate(String address);

    Response getAddress(String lng, String lat, int poi);
}
