package online.flowerinsnow.securetunnel.network.packet;

import io.netty.buffer.ByteBuf;
import online.flowerinsnow.securetunnel.exception.PacketInfoMissedException;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * <p>一个基础数据包，拥有读、写、获取类型 ID、获取类型方向等方法</p>
 */
public class PacketBase {
    /**
     * <p>获取当前数据包类型信息</p>
     *
     * @see #getInfo(Class)
     * @return 当前数据包类型信息
     * @throws PacketInfoMissedException 当类上缺少{@link PacketInfo} 注解时抛出
     */
    public @NotNull PacketInfo getInfo() throws PacketInfoMissedException {
        return PacketBase.getInfo(this.getClass());
    }

    /**
     * <p>获取当前数据包类型 ID</p>
     *
     * @see #getInfo()
     * @return 当前数据包类型 ID
     * @throws PacketInfoMissedException 当类上缺少{@link PacketInfo} 注解时抛出
     */
    public byte getID() throws PacketInfoMissedException {
        return PacketBase.getID(this.getClass());
    }

    /**
     * <p>获取当前数据包类型方向</p>
     *
     * @see #getSide(Class)
     * @return 当前数据包类型方向
     * @throws PacketInfoMissedException 当类上缺少{@link PacketInfo} 注解时抛出
     */
    public PacketSide getSide() throws PacketInfoMissedException {
        return PacketBase.getSide(this.getClass());
    }

    /**
     * <p>获取类的数据包类型信息</p>
     *
     * @param type 类
     * @return 类的数据包类型信息
     * @throws PacketInfoMissedException 当类上缺少{@link PacketInfo} 注解时抛出
     */
    public static PacketInfo getInfo(Class<? extends PacketBase> type) throws PacketInfoMissedException {
        return Optional.ofNullable(type.getAnnotation(PacketInfo.class))
                .orElseThrow(PacketInfoMissedException::new);
    }

    /**
     * <p>获取类的数据包类型 ID</p>
     *
     * @see #getInfo(Class)
     * @param type 类
     * @return 类的数据包类型 ID
     * @throws PacketInfoMissedException 当类上缺少{@link PacketInfo} 注解时抛出
     */
    public static byte getID(Class<? extends PacketBase> type) throws PacketInfoMissedException {
        return PacketBase.getInfo(type).id();
    }

    /**
     * <p>获取类的数据包类型方向</p>
     *
     * @param type 类
     * @return 类的数据包类型方向
     * @throws PacketInfoMissedException 当类上缺少{@link PacketInfo} 注解时抛出
     */
    public static PacketSide getSide(Class<? extends PacketBase> type) throws PacketInfoMissedException {
        return PacketBase.getInfo(type).side();
    }

    /**
     * <p>从数据缓冲中读取出数据包</p>
     *
     * @param buf 数据缓冲
     * @throws Exception 异常
     */
    public void read(ByteBuf buf) throws Exception {
    }

    /**
     * <p>将数据包写入数据缓冲</p>
     *
     * @param buf 缓冲
     * @throws Exception 异常
     */
    public void write(ByteBuf buf) throws Exception {
    }
}
