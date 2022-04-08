package gaozhi.online.ubtp.im.service;

import com.google.gson.Gson;
import gaozhi.online.ubtb.core.net.UBTPSocket;
import gaozhi.online.ubtb.core.net.UCommunicationType;
import gaozhi.online.ubtb.core.net.UMsg;
import gaozhi.online.ubtb.core.net.UMsgType;
import gaozhi.online.ubtb.core.util.ByteUtil;
import gaozhi.online.ubtb.core.util.StringUtils;
import gaozhi.online.ubtp.im.config.UBTPConfig;
import gaozhi.online.ubtp.im.entity.UServer;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogDelegateFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO UBTP通信服务器的心跳协议
 * @date 2022/3/3 16:11
 */
@Service
public class UServerCenterProtocolService implements UBTPSocket.UMsgConsumer {

    private final Log log = LogDelegateFactory.getHiddenLog(UServerCenterProtocolService.class);
    private final Gson gson = new Gson();

    //注册中心列表
    private final List<UServer> centerList = new LinkedList<>();
    //发送数据单元的服务
    private final UMsgService msgService;
    //服务器的数量
    private int serverSize;
    //发给注册中心的消息
    private final UMsg beat = new UMsg();

    {
        beat.setToId(-1);
        beat.setMsgType(UMsgType.S2Center_BEAT_REQUEST.getType());
    }

    @Autowired
    public UServerCenterProtocolService(UMsgService uMsgService, UBTPConfig config) {
        this.msgService = uMsgService;
        uMsgService.addUMsgConsumer(this);
        String[] centers = config.getCenters().split(",");
        for (String server : centers) {
            String[] ipPort = server.split(":");
            UServer center = new UServer();
            center.setIp(ipPort[0]);
            center.setPort(Integer.parseInt(ipPort[1]));
            center.setSocketAddress(new InetSocketAddress(center.getIp(), center.getPort()));
            centerList.add(center);
        }
    }

    @Override
    public void accept(UMsg uMsg, SocketAddress socketAddress) {
        //确保是注册中心向服务器发送的消息
        if (UCommunicationType.getType(uMsg.getFromId(), uMsg.getToId()) != UCommunicationType.Center2S) {
            return;
        }
        if (uMsg.getMsgType() != UMsgType.Center2S_BEAT_RESPONSE.getType()) {
            return;
        }

        //是注册中心发送过来的心跳回执消息
        //log.info(uMsg);
        serverSize = (int) uMsg.getParam4();
        UServer next = gson.fromJson(ByteUtil.bytesToString(uMsg.getData()), UServer.class);
        if (next == null) {
            return;
        }

        UServer nextUServer = msgService.getNextUServer();

        if (!StringUtils.equals(next.getIp(), nextUServer.getIp()) || next.getPort() != nextUServer.getPort()) {
            nextUServer.setSocketAddress(new InetSocketAddress(next.getIp(), next.getPort()));
        }
        nextUServer.setId(next.getId());
        nextUServer.setPort(next.getPort());
        nextUServer.setHost(next.getHost());
        nextUServer.setIp(next.getIp());
        nextUServer.setRemark(next.getRemark());
        nextUServer.setUpdateTime(System.currentTimeMillis());
        int serverId = (int) uMsg.getToId();
        msgService.setServerId(serverId);
        log.info(" 来自注册中心(心跳回执),nextServer: " + nextUServer + " || 本im服务器编号: " + serverId);
    }

    @Scheduled(fixedDelay = 30000)
    public void register() throws IOException {
        log.info(" 向注册中心列表发送心跳 ");
        beat.setFromId(msgService.getServerId());
        for (UServer center : centerList) {
            msgService.send(beat, center.getSocketAddress());
        }
    }

    /**
     * 获取服务器的数量
     * @return
     */
    public int getServerSize() {
        return serverSize;
    }
}
