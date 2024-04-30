package com.zxxwl.login.api.impl;


import com.zxxwl.login.api.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 登录实现类
 *
 * @author zhouxin
 * @since 2020/11/2 15:27
 */

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class LoginServiceImpl implements LoginService {
}
