package gaozhi.online.ubtp.im.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 协议配置
 * @date 2022/3/3 16:04
 */
@Component
public class UBTPConfig {

    @Value("${ubtp.port}")
    private int port;
    @Value("${ubtp.mtu}")
    private int mtu;
    @Value("${ubtp.centers}")
    private String centers;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMtu() {
        return mtu;
    }

    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    public String getCenters() {
        return centers;
    }

    public void setCenters(String centers) {
        this.centers = centers;
    }

    @Override
    public String toString() {
        return "UBTPConfig{" +
                "port=" + port +
                ", mtu=" + mtu +
                ", centers='" + centers + '\'' +
                '}';
    }
}
