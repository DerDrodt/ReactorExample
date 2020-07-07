import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

public class ReactorTestServer {
    public static void main(String[] args) {
        DisposableServer server = TcpServer.create()
                .host("localhost")
                .port(8080)
                .doOnConnection(
                        con -> con
                                .addHandler(new ObjectEncoder())
                                .addHandler(new ObjectDecoder(ClassResolvers.softCachingConcurrentResolver(null))))
                .handle((in, out) -> {
                    return out.sendObject(new ExampleMsg("My example msg"));
                })
                .wiretap(true)
                .bindNow();
        server.onDispose().block();
    }
}
