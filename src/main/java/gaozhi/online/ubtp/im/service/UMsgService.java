package gaozhi.online.ubtp.im.service;

import gaozhi.online.ubtb.core.net.UBTPSocket;
import gaozhi.online.ubtb.core.net.UMsg;
import gaozhi.online.ubtp.im.config.UBTPConfig;
import gaozhi.online.ubtp.im.entity.UServer;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 监听与发送 消息
 * @date 2022/3/3 15:59
 */
@Service
public class UMsgService extends UBTPSocket {
    private final Log log = LogDelegateFactory.getHiddenLog(UMsgService.class);
    private int serverId;
    //下一跳服务器
    private final UServer nextUServer = new UServer();

    @Autowired
    public UMsgService(UBTPConfig config) throws IOException {
        super(config.getPort(), config.getMtu());
        log.info(config);
        start();
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public UServer getNextUServer() {
        return nextUServer;
    }

    public int send2NextServer(UMsg msg) throws IOException {
        if (nextUServer.getId() == serverId) {
            return 0;
        }
        return send(msg, nextUServer.getSocketAddress());
    }
}
