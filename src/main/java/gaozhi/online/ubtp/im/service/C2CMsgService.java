package gaozhi.online.ubtp.im.service;

import gaozhi.online.ubtb.core.net.UBTPSocket;
import gaozhi.online.ubtb.core.net.UCommunicationType;
import gaozhi.online.ubtb.core.net.UMsg;
import gaozhi.online.ubtp.im.entity.UClient;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * 客户端与客户端之间的消息转发
 */
@Service
public class C2CMsgService implements UBTPSocket.UMsgConsumer {
    private final Log log = LogDelegateFactory.getHiddenLog(C2CMsgService.class);
    private final UMsgService msgService;
    private final ClientContainerService clientContainerService;
    private final UServerCenterProtocolService uServerCenterProtocolService;

    @Autowired
    public C2CMsgService(UMsgService msgService, ClientContainerService clientContainerService, UServerCenterProtocolService uServerCenterProtocolService) {
        this.msgService = msgService;
        this.uServerCenterProtocolService = uServerCenterProtocolService;
        msgService.addUMsgConsumer(this);
        this.clientContainerService = clientContainerService;
    }


    @Override
    public void accept(UMsg uMsg, SocketAddress socketAddress) {
        if (UCommunicationType.getType(uMsg.getFromId(), uMsg.getToId()) != UCommunicationType.C2C) {
            return;
        }
        int validateServer = uServerCenterProtocolService.getServerSize();

        if (uMsg.getRouteCount() > validateServer) {
            log.info("c2c消息路由次数超过服务器的数量：" + validateServer);
            return;
        }
        uMsg.setRouteCount(uMsg.getRouteCount() + 1);
        UClient client = clientContainerService.getClient(uMsg.getToId());
        if (client != null) {
            try {
                int len = msgService.send(uMsg, client.getSocketAddress());
                log.info("c2c转发长度为" + len + "的消息，到客户端：" + client);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            //如果下一跳服务器不是自己就转发，如果是自己就不转发
            int len = msgService.send2NextServer(uMsg);
            log.info("c2c转发长度为" + len + "的消息，到服务器" + msgService.getNextUServer());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
