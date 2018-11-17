package solinger;

import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ${good-yy} on 2018/11/17.
 */
public class WriteExceptionChannelHandler extends ChannelInboundHandlerAdapter {

    private static AtomicInteger integer = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive" + integer.incrementAndGet());
        //more than client solinger  1000 > 1
        Thread.sleep(1000);
        CompositeByteBuf byteBufs = ctx.alloc().compositeBuffer(100);
        for (int i = 0; i < 100; i++) {
            byteBufs.writeByte(1);
        }

        ctx.writeAndFlush(byteBufs);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
        System.out.println("channelRead"+integer.incrementAndGet());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception" + integer.incrementAndGet() + cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive"+integer.incrementAndGet());
    }


    @Override
    protected void finalize() throws Throwable {
        System.out.println("gc Now"+integer.incrementAndGet());
    }
}
