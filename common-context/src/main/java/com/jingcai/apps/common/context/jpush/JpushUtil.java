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
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.jingcai.apps.common.lang.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Slf4j
public class JpushUtil {
    private final String appKey, masterSecret;
    private final boolean productionFlag;
    private boolean showflag = true;

    public JpushUtil(String appKey, String masterSecret, boolean productionFlag) {
        this.appKey = appKey;
        this.masterSecret = masterSecret;
        this.productionFlag = productionFlag;
    }


    /**
     * 推送
     * @param alias
     * @param type
     * @param contents
     * @param channel   1：IOS 2：安卓 3：安卓及IOS 5：爱学贷
     */
    public void sendPush(String alias, String type, Map<String, String> contents, String channel) {
        log.debug("["+alias+"]["+type+"]["+channel+"]");
        PushPayload payload = null;
        //如果是需要在通知栏显示的发notice
        if(showflag) {
            if (channel == null || channel.equals("3")) {
                payload = buildPushObject_all_alias_alert(alias,type,contents);
            } else if (channel.equals("1")) {
                payload = buildPushObject_ios_alias_alert(alias,type,contents);
            } else if (channel.equals("2")) {
                payload = buildPushObject_android_alias_alert(alias,type,contents);
            }
        }else{
            //不需要在通知栏现实的发message
            if (channel == null || channel.equals("3")) {
                payload = buildPushObject_all_audienceMore_messageWithExtras(alias,type,contents);
            } else if (channel.equals("1")) {
                payload = buildPushObject_ios_audienceMore_messageWithExtras(alias,type,contents);
            } else if (channel.equals("2")) {
                payload = buildPushObject_android_audienceMore_messageWithExtras(alias,type,contents);
            }
        }
        if(payload!=null)
            dopush(payload, alias, type, contents);
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
            log.info("Got result - " + result);

        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);

        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            log.info("Msg ID: " + e.getMsgId());
        }
    }

    /**
     * 生成Android message内容
     *
     * @param alias    别名 服务端送studentid
     * @param type     消息类型    消息中心的消息->message
     * @param contents 消息内容<message表 title、content>
     * @return
     */
    private PushPayload buildPushObject_android_audienceMore_messageWithExtras(String alias, String type, Map<String, String> contents) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.alias(alias))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(type)
                        .addExtras(contents)
                        .addExtra("pushtime", DateUtil.getNow14())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(productionFlag)
                        .build())
                .build();
    }

    /**
     * 生成IOS message内容
     *
     * @param alias    别名 服务端送studentid
     * @param type     消息类型    消息中心的消息->message
     * @param contents 消息内容<message表 title、content>
     * @return
     */
    private PushPayload buildPushObject_ios_audienceMore_messageWithExtras(String alias, String type, Map<String, String> contents) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.alias(alias))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(type)
                        .addExtras(contents)
                        .addExtra("pushtime", DateUtil.getNow14())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(productionFlag)
                        .build())
                .build();
    }

    /**
     * 生成IOS及安卓 message内容
     *
     * @param alias    别名 服务端送studentid
     * @param type     消息类型    消息中心的消息->message
     * @param contents 消息内容<message表 title、content>
     * @return
     */
    private PushPayload buildPushObject_all_audienceMore_messageWithExtras(String alias, String type, Map<String, String> contents) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.alias(alias))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(type)
                        .addExtras(contents)
                        .addExtra("pushtime", DateUtil.getNow14())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(productionFlag)
                        .build())
                .build();
    }


    /**
     * 生成IOS Notice内容
     *
     * @param alias
     * @param type
     * @param contents
     * @return
     */
    private PushPayload buildPushObject_ios_alias_alert(String alias, String type, Map<String, String> contents) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(contents.get("title"))
                                .setBadge(0)
                                .addExtras(contents)
                                .addExtra("type", type)
                                .addExtra("pushtime", DateUtil.getNow14())
                                .build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(productionFlag)
                        .build())
                .build();
    }

    /**
     * 生成Android Notice内容
     *
     * @param alias
     * @param type
     * @param contents
     * @return
     */
    private PushPayload buildPushObject_android_alias_alert(String alias, String type, Map<String, String> contents) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(contents.get("title"))
                                .addExtras(contents)
                                .addExtra("type", type)
                                .addExtra("pushtime", DateUtil.getNow14())
                                .build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(productionFlag)
                        .build())
                .build();
    }

    /**
     * 生成IOS及Android Notice内容
     *
     * @param alias
     * @param type
     * @param contents
     * @return
     */
    private PushPayload buildPushObject_all_alias_alert(String alias, String type, Map<String, String> contents) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(contents.get("title"))
                                .setBadge(0)
                                .addExtras(contents)
                                .addExtra("type", type)
                                .addExtra("pushtime", DateUtil.getNow14())
                                .build())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(contents.get("title"))
                                .addExtras(contents)
                                .addExtra("type", type)
                                .addExtra("pushtime", DateUtil.getNow14())
                                .build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(productionFlag)
                        .build())
                .build();
    }

    public boolean isShowflag() {
        return showflag;
    }

    public void setShowflag(boolean showflag) {
        this.showflag = showflag;
    }
}
