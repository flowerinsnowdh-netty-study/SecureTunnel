package online.flowerinsnow.securetunnel.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import online.flowerinsnow.securetunnel.exception.MagicNumberMismatchException;

/**
 * <p>魔数解码器</p>
 * <p>会读取一次魔数并判断正确与否</p>
 */
public class MagicNumberDecoder extends ChannelInboundHandlerAdapter {
    /**
     * <p>已完成状态</p>
     */
    private boolean done;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.done) {
            return;
        }
        if (msg instanceof ByteBuf buf) {
            int magicNumber = buf.readInt();
            if (0x360B7CC2 == magicNumber) {
                this.done = true;
            } else {
                throw new MagicNumberMismatchException("The received magic number (" + Integer.toHexString(magicNumber) + ") does not match 0x360B7CC2");
            }
        } else {
            throw new MagicNumberMismatchException("The received packet class (" + msg.getClass().getName() + " does not match " + ByteBuf.class.getName());
        }
    }
}
