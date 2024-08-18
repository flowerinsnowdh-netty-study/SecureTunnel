package online.flowerinsnow.securetunnel.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import online.flowerinsnow.securetunnel.exception.UnexpectedException;
import online.flowerinsnow.securetunnel.exception.UnknownPacketException;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.network.packet.PacketList;
import online.flowerinsnow.securetunnel.object.ClientEncryptSession;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

// TODO 需要重构
public class ClientSecureTunnelDecoder extends ByteToMessageDecoder {
    private final ClientEncryptSession session;
    private final PacketList packetList;

    public ClientSecureTunnelDecoder(ClientEncryptSession session, PacketList packetList) {
        this.session = session;
        this.packetList = packetList;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() <= 0) {
            throw new UnknownPacketException();
        }
        if (this.session.getClientPrivateKey() == null || this.session.getServerPublicKey() == null) {
            this.decodeDecrypted(in, out);
            return;
        }

        SecretKeySpec sharedSecretKey;
        synchronized (this.session.sharedSecretKeyLock) {
            if ((sharedSecretKey = this.session.getSharedSecretKey()) == null) {
                throw new UnexpectedException();
            }
        }

        byte[] s2cGCMParameter = this.session.getS2CGCMParameter();
        if (s2cGCMParameter == null) {
            byte[] bytes = new byte[in.readableBytes()];
            in.readBytes(bytes);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, sharedSecretKey);
            byte[] decrypted = cipher.doFinal(bytes);
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeBytes(decrypted);
            this.decodeDecrypted(in, out);
            buffer.release();
            return;
        }

        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, sharedSecretKey, new GCMParameterSpec(96, s2cGCMParameter));
        byte[] decrypted = cipher.doFinal(bytes);
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(decrypted);
        this.decodeDecrypted(in, out);
        buffer.release();
    }

    private void decodeDecrypted(ByteBuf in, List<Object> out) throws Exception {
        byte packetID = in.readByte();
        Class<? extends PacketBase> type = this.packetList.get(packetID);
        if (type == null) {
            throw new UnknownPacketException("id " + packetID);
        }
        PacketBase packet = type.getConstructor().newInstance();
        packet.read(in);
        out.add(packet);
    }
}
