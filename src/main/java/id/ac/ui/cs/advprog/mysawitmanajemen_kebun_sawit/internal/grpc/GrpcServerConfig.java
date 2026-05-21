package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.internal.grpc;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GrpcServerConfig {

    @Bean
    @ConditionalOnProperty(prefix = "grpc.server", name = "enabled", havingValue = "true", matchIfMissing = true)
    SmartLifecycle kebunGrpcServer(
            KebunInternalGrpcService kebunInternalGrpcService,
            @Value("${grpc.server.port:9092}") int grpcPort
    ) {
        return new KebunGrpcServerLifecycle(kebunInternalGrpcService, grpcPort);
    }

    private static class KebunGrpcServerLifecycle implements SmartLifecycle {
        private final KebunInternalGrpcService kebunInternalGrpcService;
        private final int grpcPort;
        private Server server;
        private boolean running;

        private KebunGrpcServerLifecycle(KebunInternalGrpcService kebunInternalGrpcService, int grpcPort) {
            this.kebunInternalGrpcService = kebunInternalGrpcService;
            this.grpcPort = grpcPort;
        }

        @Override
        public void start() {
            try {
                server = NettyServerBuilder.forPort(grpcPort)
                        .addService(kebunInternalGrpcService)
                        .build()
                        .start();
                running = true;
            } catch (IOException e) {
                throw new IllegalStateException("Failed to start Kebun gRPC server on port " + grpcPort, e);
            }
        }

        @Override
        public void stop() {
            if (server != null) {
                server.shutdown();
            }
            running = false;
        }

        @Override
        public boolean isRunning() {
            return running;
        }
    }
}
