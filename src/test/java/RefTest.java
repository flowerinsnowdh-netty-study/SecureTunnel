import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

public class RefTest {
    @Test
    void test() {
        EmbeddedChannel channel = new EmbeddedChannel();
        ByteBuf buf1 = channel.alloc().buffer();
        ByteBuf buf2 = channel.alloc().buffer();
        buf1.writeByte(10);
        buf2.writeBytes(buf1);
        System.out.println(buf1.refCnt());
        buf1.release();
    }
}
