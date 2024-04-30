package com.zxxwl.common.api.aliyun.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface ALiYunOpenApiService {

    JsonNode idCardValidate(String idCardNo, String name);

    JsonNode idCardValidateV2(String idCardNo, String name);

    JsonNode getCoordinate(String address);

    JsonNode getAddress(String longitude, String latitude, int poi);
}
