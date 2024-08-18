package online.flowerinsnow.securetunnel.network.packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>声明数据包类型 ID，用在一个继承了 {@link PacketBase} 的类上</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketInfo {
    /**
     * <p>数据包类型 ID</p>
     * @return 数据包类型 ID
     */
    byte id();

    /**
     * <p>数据包方向</p>
     * @return 数据包方向
     */
    PacketSide side();
}
