package gaozhi.online.ubtp.im.service;

import com.google.gson.Gson;
import gaozhi.online.ubtb.core.net.UBTPSocket;
import gaozhi.online.ubtb.core.net.UCommunicationType;
import gaozhi.online.ubtb.core.net.UMsg;
import gaozhi.online.ubtb.core.net.UMsgType;
import gaozhi.online.ubtb.core.util.ByteUtil;
import gaozhi.online.ubtp.im.entity.UClient;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端容器
 */
@Service
public class ClientContainerService implements UBTPSocket.UMsgConsumer {
    private final Log log = LogDelegateFactory.getHiddenLog(ClientContainerService.class);
    private final Map<Long, UClient> clientMap = new ConcurrentHashMap<>();
    private final UMsgService msgService;
    private final Gson gson = new Gson();
    @Autowired
    public ClientContainerService(UMsgService msgService) {
        this.msgService = msgService;
        msgService.addUMsgConsumer(this);
    }

    @Override
    public void accept(UMsg uMsg, SocketAddress socketAddress) {
        //c2s
        if (UCommunicationType.getType(uMsg.getFromId(), uMsg.getToId()) != UCommunicationType.C2S) {
            return;
        }
        //c2s beat
        if (uMsg.getMsgType() != UMsgType.C2S__BEAT_REQUEST.getType()) {
            return;
        }
        UClient client = clientMap.get(uMsg.getFromId());
        if (client == null) {
            client = new UClient();
        }
        client.setId(uMsg.getFromId());
        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        client.setSocketAddress(socketAddress);
        client.setIp(inetSocketAddress.getAddress().getHostAddress());
        client.setPort(inetSocketAddress.getPort());
        client.setUpdateTime(System.currentTimeMillis());

        clientMap.put(client.getId(), client);
        log.info("客户端心跳："+client);
        //回复心跳
        UMsg beat_response = new UMsg();
        beat_response.setMsgType(UMsgType.S2Center_BEAT_REQUEST.getType());
        beat_response.setFromId(msgService.getServerId());
        beat_response.setToId(client.getId());
        beat_response.setData(ByteUtil.stringToByte(gson.toJson(client)));
        try {
            msgService.send(beat_response,socketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 30000)
    public void deleteInvalidate() {
        log.info("before 检查客户端是否存活 client num："+clientMap.size());
        clientMap.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue().getUpdateTime() > 30000);
        log.info("after 检查客户端是否存活 client num："+clientMap.size());
    }

    /**
     * 获取客户端实例
     * @param id
     * @return
     */
    public UClient getClient(long id){
        return clientMap.get(id);
    }
}
