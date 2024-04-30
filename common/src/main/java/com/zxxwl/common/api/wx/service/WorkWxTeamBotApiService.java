package com.zxxwl.common.api.wx.service;

/**
 * 企业微信群聊机器人发送
 * <a href="https://developer.work.weixin.qq.com/document/path/99110"></a>
 */
public interface WorkWxTeamBotApiService {

    boolean sendText(String content);

    /**
     * 发送 Markdown
     * rest:{"errcode":0,"errmsg":"ok"}
     * {"errcode":0,"errmsg":"ok"}
     *
     * @param content markdown string
     * @return ok
     */
    boolean sendMarkdown(String content);
}
