package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.internal.grpc;

import id.ac.ui.cs.advprog.mysawit.grpc.auth.AuthInternalServiceGrpc;
import id.ac.ui.cs.advprog.mysawit.grpc.auth.GetUserByIdRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.auth.ValidateUserRoleRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.auth.ValidateUserRoleResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.service.UserRoleValidator;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthGrpcUserRoleValidatorTest {

    @Mock
    private AuthInternalServiceGrpc.AuthInternalServiceBlockingStub authStub;

    @Mock
    private ManagedChannel managedChannel;

    private AuthGrpcUserRoleValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AuthGrpcUserRoleValidator(managedChannel, authStub);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        validator.shutdown();
    }

    @Test
    void isValidRole_ReturnsTrue_WhenGrpcReturnsValid() {
        when(authStub.validateUserRole(any(ValidateUserRoleRequest.class)))
                .thenReturn(ValidateUserRoleResponse.newBuilder().setValid(true).build());

        UUID userId = UUID.randomUUID();
        boolean result = validator.isValidRole(userId, "MANDOR");

        assertTrue(result);
        verify(authStub).validateUserRole(ValidateUserRoleRequest.newBuilder()
                .setUserId(userId.toString())
                .setExpectedRole("MANDOR")
                .build());
    }

    @Test
    void isValidRole_ReturnsFalse_WhenGrpcReturnsInvalid() {
        when(authStub.validateUserRole(any(ValidateUserRoleRequest.class)))
                .thenReturn(ValidateUserRoleResponse.newBuilder().setValid(false).build());

        UUID userId = UUID.randomUUID();
        boolean result = validator.isValidRole(userId, "MANDOR");

        assertFalse(result);
    }

    @Test
    void isValidRole_ReturnsFalse_WhenUserIdIsNull() {
        assertFalse(validator.isValidRole(null, "MANDOR"));
        verifyNoInteractions(authStub);
    }

    @Test
    void isValidRole_ReturnsFalse_WhenExpectedRoleIsNull() {
        UUID userId = UUID.randomUUID();
        assertFalse(validator.isValidRole(userId, null));
        verifyNoInteractions(authStub);
    }

    @Test
    void isValidRole_ReturnsFalse_WhenExpectedRoleIsBlank() {
        UUID userId = UUID.randomUUID();
        assertFalse(validator.isValidRole(userId, "   "));
        verifyNoInteractions(authStub);
    }

    @Test
    void isValidRole_ReturnsFalse_WhenBothAreNull() {
        assertFalse(validator.isValidRole(null, null));
        verifyNoInteractions(authStub);
    }

    @Test
    void getFullname_ReturnsFullname_WhenUserFound() {
        var userResponse = mock(id.ac.ui.cs.advprog.mysawit.grpc.auth.UserResponse.class);
        when(userResponse.getFound()).thenReturn(true);
        when(userResponse.getFullname()).thenReturn("John Doe");
        when(authStub.getUserById(any(GetUserByIdRequest.class))).thenReturn(userResponse);

        UUID userId = UUID.randomUUID();
        String result = validator.getFullname(userId);

        assertEquals("John Doe", result);
        verify(authStub).getUserById(GetUserByIdRequest.newBuilder()
                .setUserId(userId.toString())
                .build());
    }

    @Test
    void getFullname_ReturnsNull_WhenUserNotFound() {
        var userResponse = mock(id.ac.ui.cs.advprog.mysawit.grpc.auth.UserResponse.class);
        when(userResponse.getFound()).thenReturn(false);
        when(authStub.getUserById(any(GetUserByIdRequest.class))).thenReturn(userResponse);

        UUID userId = UUID.randomUUID();
        String result = validator.getFullname(userId);

        assertNull(result);
    }

    @Test
    void getFullname_ReturnsNull_WhenUserIdIsNull() {
        assertNull(validator.getFullname(null));
        verifyNoInteractions(authStub);
    }

    @Test
    void shutdown_AwaitsTermination_WhenNotTerminated() throws InterruptedException {
        when(managedChannel.awaitTermination(anyLong(), any())).thenReturn(false);
        when(managedChannel.shutdownNow()).thenReturn(managedChannel);

        validator.shutdown();

        verify(managedChannel).shutdown();
        verify(managedChannel).shutdownNow();
    }

    @Test
    void shutdown_DoesNotShutdownNow_WhenAlreadyTerminated() throws InterruptedException {
        when(managedChannel.awaitTermination(anyLong(), any())).thenReturn(true);

        validator.shutdown();

        verify(managedChannel, never()).shutdownNow();
    }
}