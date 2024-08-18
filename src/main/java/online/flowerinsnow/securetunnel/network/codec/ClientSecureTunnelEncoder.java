package online.flowerinsnow.securetunnel.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import online.flowerinsnow.securetunnel.exception.UnexpectedException;
import online.flowerinsnow.securetunnel.network.packet.PacketBase;
import online.flowerinsnow.securetunnel.object.ClientEncryptSession;
import online.flowerinsnow.securetunnel.util.cipher.CipherUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import java.security.KeyPair;

// TODO 需要重构
public class ClientSecureTunnelEncoder extends MessageToByteEncoder<PacketBase> {
    private final ClientEncryptSession session;

    public ClientSecureTunnelEncoder(ClientEncryptSession session) {
        this.session = session;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, PacketBase msg, ByteBuf out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer(256, 1024 + Short.BYTES);
        buf.writeByte(msg.getID());
        msg.write(buf);
        if (this.session.getClientPrivateKey() == null) {
            KeyPair keyPair = CipherUtils.genECKeyPair();
            this.session.setClientPrivateKey(keyPair.getPrivate());
            this.session.setClientPublicKey(keyPair.getPublic());
            out.writeShort(buf.readableBytes());
            out.writeBytes(buf);
            buf.release();
            return;
        }
        if (this.session.getServerPublicKey() == null) {
            out.writeShort(buf.readableBytes());
            out.writeBytes(buf);
            buf.release();
            return;
        }
        boolean flag;
        synchronized (this.session.sharedSecretKeyLock) {
            flag = this.session.getSharedSecretKey() == null;
        }
        if (flag) {
            throw new UnexpectedException();
        }
        if (this.session.getC2SGCMParameter() == null) {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, this.session.getSharedSecretKey());
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            buf.release();
            byte[] encrypted = cipher.doFinal(bytes);
            out.writeShort(encrypted.length);
            out.writeBytes(encrypted);
            return;
        }
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, this.session.getSharedSecretKey(), new GCMParameterSpec(96, this.session.getC2SGCMParameter()));
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
