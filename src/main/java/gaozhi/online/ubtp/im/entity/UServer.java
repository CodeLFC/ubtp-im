package gaozhi.online.ubtp.im.entity;


import java.net.InetSocketAddress;

/**
 * 服务器
 */
public class UServer {
    /**
     * 服务器编号
     */
    private int id;
    private String remark;
    private String host;
    private String ip;
    private int port;
    /**
     * 上次心跳刷新时间
     */
    private long updateTime;

    private InetSocketAddress socketAddress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public void setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    @Override
    public String toString() {
        return "UServer{" +
                "id=" + id +
                ", remark='" + remark + '\'' +
                ", host='" + host + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", updateTime=" + updateTime +
                ", socketAddress=" + socketAddress +
                '}';
    }
}
