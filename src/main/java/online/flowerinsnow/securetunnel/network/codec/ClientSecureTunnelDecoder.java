package online.flowerinsnow.securetunnel.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import online.flowerinsnow.securetunnel.exception.UnknownPacketException;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.network.packet.PacketList;
import online.flowerinsnow.securetunnel.object.ClientSession;
import online.flowerinsnow.securetunnel.object.DefaultGCMParamSpec;
import online.flowerinsnow.securetunnel.util.buffer.BufUtils;
import online.flowerinsnow.securetunnel.util.cipher.CipherBuilder;
import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;
import java.util.Objects;

public class ClientSecureTunnelDecoder extends ByteToMessageDecoder {
    @NotNull private final ClientSession session;
    @NotNull private final PacketList packetList;

    public ClientSecureTunnelDecoder(@NotNull ClientSession session, @NotNull PacketList packetList) {
        this.session = Objects.requireNonNull(session);
        this.packetList = Objects.requireNonNull(packetList);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() <= 0) { // 数据长度为0，连数据包类型都没有，属于不明情况
            throw new UnknownPacketException();
        }

        SecretKeySpec sharedSecretKey = this.session.getSharedSecretKey();
        if (sharedSecretKey == null) { // 共享安全密钥还未合成，加密还未开始
            this.createPacket(in, out);
            return;
        }

        byte[] s2cGCMParameter = this.session.getS2CGCMParameter();
        if (s2cGCMParameter == null) { // 共享安全密钥生成完毕，还没有发送S2CGCMParameter
            byte[] data = BufUtils.readAll(in); // 数据包内容
            // 解密数据包内容
            Cipher cipher = new CipherBuilder(CipherUtils.AES.AES_ECB_PKCS7PADDING)
                    .init(Cipher.DECRYPT_MODE, sharedSecretKey)
                    .get();

            ByteBuf buf = BufUtils.fromDecrypt(ctx.alloc(), data, cipher); // 解密结果
            this.createPacket(buf, out);
            buf.release();
            return;
        }

        this.session.updateS2CGCMParameter();
        // 解密已就绪，正常解密
        byte[] data = BufUtils.readAll(in); // 数据包内容
        // 解密数据包内容
        Cipher cipher = new CipherBuilder(CipherUtils.AES.AES_GCM)
                .init(Cipher.DECRYPT_MODE, sharedSecretKey, new DefaultGCMParamSpec(s2cGCMParameter))
                .get();
        ByteBuf buf = BufUtils.fromDecrypt(ctx.alloc(), data, cipher); // 解密结果
        this.createPacket(buf, out);
        buf.release();
    }

    /**
     * <p>解析输入数据 {@code in} 为数据包，并将数据包写入输出集合 {@code out}</p>
     * <p>不会释放 {@code in}</p>
     *
     * @param in 输入数据
     * @param out 输出集合
     * @throws Exception 解析时抛出的任何异常
     */
    private void createPacket(@NotNull ByteBuf in, @NotNull List<Object> out) throws Exception {
        Objects.requireNonNull(in);
        Objects.requireNonNull(out);

        byte packetID = in.readByte(); // 读取 ID
        Class<? extends PacketBase> type = this.packetList.get(packetID); // 获取类型
        if (type == null) { // 未知数据包类型
            throw new UnknownPacketException("id " + packetID); // 抛出未知数据包类型异常
        }
        PacketBase packet = type.getConstructor().newInstance(); // 实例化新数据包
        packet.read(in); // 从输入数据阅读
        out.add(packet); // 写到输出集合
    }
}
