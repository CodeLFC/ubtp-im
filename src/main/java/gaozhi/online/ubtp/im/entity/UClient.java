package gaozhi.online.ubtp.im.entity;

import java.net.SocketAddress;

/**
 * 客户端类型
 */
public class UClient {
    private long id;
    private String ip;
    private int port;
    private long updateTime;

    private SocketAddress socketAddress;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    @Override
    public String toString() {
        return "UClient{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", updateTime=" + updateTime +
                '}';
    }
}
