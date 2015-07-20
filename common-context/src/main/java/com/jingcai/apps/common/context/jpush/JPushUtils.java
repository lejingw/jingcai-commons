package com.jingcai.apps.common.context.jpush;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Title: com.jingcai.azserver.util
 * Description:
 * Copyright: Copyright (c) 2015
 * Company: iZhuan365
 *
 * @author sanq
 * @version 1.0
 * @date 2015-05-12
 */
public class JPushUtils {
    protected static final Logger LOG = LoggerFactory.getLogger(JPushUtils.class);

    private final String appKey, masterSecret;

    public JPushUtils(String appKey, String masterSecret) {
        this.appKey = appKey;
        this.masterSecret = masterSecret;
    }


    public void sendPush(String alias, String type, Map<String, String> contents, String channel) {
        PushPayload payload = null;
        if (channel == null || channel.equals("3")) {
            payload = buildPushObject_all_alias_alert(alias, type, contents);
            dopush(payload, alias, type, contents);

            payload = buildPushObject_ios_audienceMore_messageWithExtras(alias, type, contents);
            dopush(payload, alias, type, contents);
        } else if (channel.equals("1")) {
            payload = buildPushObject_all_alias_alert(alias, type, contents);
            dopush(payload, alias, type, contents);
        } else if (channel.equals("2")) {
            payload = buildPushObject_ios_audienceMore_messageWithExtras(alias, type, contents);
            dopush(payload, alias, type, contents);
        }
    }

    /**
     * 发送推送
     *
     * @param pushPayload
     * @param alias
     * @param type
     * @param contents
     */
    private void dopush(PushPayload pushPayload, String alias, String type, Map<String, String> contents) {
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, 3);
        try {
            PushResult result = jpushClient.sendPush(pushPayload);
            LOG.info("Got result - " + result);

        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);

        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
        }
    }

    /**
     * 生成Android推送内容
     *
     * @param alias    别名 服务端送studentid
     * @param type     消息类型    消息中心的消息->message
     * @param contents 消息内容<message表 title、content>
     * @return
     */
    private PushPayload buildPushObject_ios_audienceMore_messageWithExtras(String alias, String type, Map<String, String> contents) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.alias(alias))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(type)
                        .addExtras(contents)
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)
                        .build())
                .build();
    }


    /**
     * 生成IOS推送内容
     *
     * @param alias
     * @param type
     * @param contents
     * @return
     */
    private PushPayload buildPushObject_all_alias_alert(String alias, String type, Map<String, String> contents) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(contents.get("title"))
                                .setBadge(0)
                                .addExtra("msg_content", contents.get("id"))
                                .build())
                        .build())
                .setMessage(Message.content(contents.get("content")))
                .setOptions(Options.newBuilder()
                        .setApnsProduction(true)
                        .build())
                .build();
    }
}
