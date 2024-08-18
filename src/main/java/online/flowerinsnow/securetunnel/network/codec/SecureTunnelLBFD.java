package online.flowerinsnow.securetunnel.network.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class SecureTunnelLBFD extends LengthFieldBasedFrameDecoder {
    public SecureTunnelLBFD() {
        /*
        1024 (base)
        + 12 (tLen)
        + 2 (length field)
         */
        super(1024 + 12 + Short.BYTES, 0, 2, 0, 2);
    }
}
