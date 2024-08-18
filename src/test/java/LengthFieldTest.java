import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.junit.jupiter.api.Test;

public class LengthFieldTest {
    @Test
    void test() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
        buf.writeByte(4);
        buf.writeByte(4);
        buf.writeByte(4);
        buf.writeByte(4);
        buf.writeByte(4);
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(4, 0, 1, 0, 1))
                .addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                        System.out.println(msg);
                    }
                });
        channel.writeInbound(buf);
    }
}
