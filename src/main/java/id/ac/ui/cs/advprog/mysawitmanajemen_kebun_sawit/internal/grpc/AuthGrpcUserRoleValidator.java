package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.internal.grpc;

import id.ac.ui.cs.advprog.mysawit.grpc.auth.AuthInternalServiceGrpc;
import id.ac.ui.cs.advprog.mysawit.grpc.auth.GetUserByIdRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.auth.ValidateUserRoleRequest;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service.UserRoleValidator;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(prefix = "internal.grpc.auth", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuthGrpcUserRoleValidator implements UserRoleValidator {
    private final ManagedChannel channel;
    private final AuthInternalServiceGrpc.AuthInternalServiceBlockingStub authStub;

    AuthGrpcUserRoleValidator(ManagedChannel channel, AuthInternalServiceGrpc.AuthInternalServiceBlockingStub authStub) {
        this.channel = channel;
        this.authStub = authStub;
    }

    public AuthGrpcUserRoleValidator(AuthGrpcProperties properties) {
        channel = ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort())
                .usePlaintext()
                .build();
        authStub = AuthInternalServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public boolean isValidRole(UUID userId, String expectedRole) {
        if (userId == null || expectedRole == null || expectedRole.isBlank()) {
            return false;
        }

        return authStub.validateUserRole(ValidateUserRoleRequest.newBuilder()
                        .setUserId(userId.toString())
                        .setExpectedRole(expectedRole)
                        .build())
                .getValid();
    }

    @Override
    public String getFullname(UUID userId) {
        if (userId == null) {
            return null;
        }

        var response = authStub.getUserById(GetUserByIdRequest.newBuilder()
                .setUserId(userId.toString())
                .build());
        return response.getFound() ? response.getFullname() : null;
    }

    @PreDestroy
    void shutdown() throws InterruptedException {
        channel.shutdown();
        if (!channel.awaitTermination(3, TimeUnit.SECONDS)) {
            channel.shutdownNow();
        }
    }
}
