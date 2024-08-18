package online.flowerinsnow.securetunnel.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import online.flowerinsnow.securetunnel.exception.UnexpectedException;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.object.ServerEncryptSession;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// TODO 需要重构
public class ServerSecureTunnelEncoder extends MessageToByteEncoder<PacketBase> {
    private final ServerEncryptSession session;

    public ServerSecureTunnelEncoder(ServerEncryptSession session) {
        this.session = session;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, PacketBase msg, ByteBuf out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer(256, 1024 + Short.BYTES);
        buf.writeByte(msg.getID());
        msg.write(buf);

        if (this.session.getServerPrivateKey() == null || this.session.getClientPublicKey() == null) {
            out.writeShort(buf.readableBytes());
            out.writeBytes(buf);
            buf.release();
            return;
        }

        SecretKeySpec sharedSecretKey;
        synchronized (this.session.sharedSecretKeyLock) {
            if ((sharedSecretKey = this.session.getSharedSecretKey()) == null) {
                throw new UnexpectedException();
            }
        }
        if (this.session.getC2SGCMParameter() == null) {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sharedSecretKey);
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            buf.release();
            byte[] encrypted = cipher.doFinal(bytes);
            out.writeShort(encrypted.length);
            out.writeBytes(encrypted);
            return;
        }
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, sharedSecretKey, new GCMParameterSpec(96, this.session.getC2SGCMParameter()));
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        buf.release();
        byte[] encrypted = cipher.doFinal(bytes);
        out.writeShort(encrypted.length);
        out.writeBytes(encrypted);
    }

    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, PacketBase msg, boolean preferDirect) throws Exception {
        if (preferDirect) {
            return ctx.alloc().directBuffer(256, 1024 + 12 + Short.BYTES);
        } else {
            return ctx.alloc().heapBuffer(256, 1024 + 12 + Short.BYTES);
        }
    }
}
