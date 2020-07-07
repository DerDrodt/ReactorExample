import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

public class ReactorTestClient {

    public static void main(String[] args) {
        Connection client = TcpClient.create()
                .host("localhost")
                .port(8080)
                .doOnConnected(con -> con
                        .addHandler(new ObjectEncoder())
                        .addHandler(new ObjectDecoder(ClassResolvers.softCachingConcurrentResolver(null)))
                ).handle((in, out) -> {
            in
                    .receiveObject()
                    .log("[Client received]")
                    .doOnNext(n -> {
                        System.out.println(n);
                    })
                    .subscribe();
            return out.send(Mono.never());
        })
                .wiretap(true)
                .connectNow();
        client.onDispose().block();
    }
}
