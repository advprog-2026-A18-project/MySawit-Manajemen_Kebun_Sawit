package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.internal.grpc;

import id.ac.ui.cs.advprog.mysawit.grpc.kebun.GetKebunByKodeRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.GetSupirByKebunRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.GetSupirByKebunResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.UserKebunRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.ValidateMandorSupirSameKebunRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.ValidateMandorSupirSameKebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.KebunSupir;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunRepository;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunSupirRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KebunInternalGrpcServiceTest {

    @Mock
    private KebunRepository kebunRepository;

    @Mock
    private KebunSupirRepository kebunSupirRepository;

    @Mock
    private StreamObserver<KebunResponse> kebunResponseObserver;

    @Mock
    private StreamObserver<GetSupirByKebunResponse> supirResponseObserver;

    @Mock
    private StreamObserver<ValidateMandorSupirSameKebunResponse> sameKebunResponseObserver;

    private KebunInternalGrpcService grpcService;
    private UUID mandorId;
    private UUID supirId;
    private Kebun kebun;
    private KebunSupir supir;

    @BeforeEach
    void setUp() {
        grpcService = new KebunInternalGrpcService(kebunRepository, kebunSupirRepository);
        mandorId = UUID.randomUUID();
        supirId = UUID.randomUUID();
        kebun = createKebun("KB001", "Kebun Makmur", mandorId);
        supir = createSupir(supirId, "KB001", "Budi Supir");
    }

    @Test
    void getKebunByKode_WhenFound_ReturnsKebunWithSupir() {
        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(List.of(supir));

        grpcService.getKebunByKode(
                GetKebunByKodeRequest.newBuilder().setKodeKebun("KB001").build(),
                kebunResponseObserver
        );

        KebunResponse response = captureKebunResponse();
        assertTrue(response.getFound());
        assertEquals("KB001", response.getKodeKebun());
        assertEquals("Kebun Makmur", response.getNamaKebun());
        assertEquals(mandorId.toString(), response.getMandorId());
        assertEquals(1, response.getSupirCount());
        assertEquals(supirId.toString(), response.getSupir(0).getSupirId());
    }

    @Test
    void getKebunByKode_WhenNotFound_ReturnsFoundFalse() {
        when(kebunRepository.findById("KB999")).thenReturn(Optional.empty());

        grpcService.getKebunByKode(
                GetKebunByKodeRequest.newBuilder().setKodeKebun("KB999").build(),
                kebunResponseObserver
        );

        KebunResponse response = captureKebunResponse();
        assertFalse(response.getFound());
        assertEquals("KB999", response.getKodeKebun());
    }

    @Test
    void getKebunByMandorId_WhenFound_ReturnsKebun() {
        when(kebunRepository.findByMandorId(mandorId)).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(List.of());

        grpcService.getKebunByMandorId(
                UserKebunRequest.newBuilder().setUserId(mandorId.toString()).build(),
                kebunResponseObserver
        );

        KebunResponse response = captureKebunResponse();
        assertTrue(response.getFound());
        assertEquals("KB001", response.getKodeKebun());
    }

    @Test
    void getKebunByMandorId_WhenInvalidUuid_ReturnsInvalidArgument() {
        grpcService.getKebunByMandorId(
                UserKebunRequest.newBuilder().setUserId("not-a-uuid").build(),
                kebunResponseObserver
        );

        assertInvalidArgument(kebunResponseObserver);
    }

    @Test
    void getKebunBySupirId_WhenFound_ReturnsKebun() {
        when(kebunSupirRepository.findById(supirId)).thenReturn(Optional.of(supir));
        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(List.of(supir));

        grpcService.getKebunBySupirId(
                UserKebunRequest.newBuilder().setUserId(supirId.toString()).build(),
                kebunResponseObserver
        );

        KebunResponse response = captureKebunResponse();
        assertTrue(response.getFound());
        assertEquals("KB001", response.getKodeKebun());
    }

    @Test
    void getKebunBySupirId_WhenInvalidUuid_ReturnsInvalidArgument() {
        grpcService.getKebunBySupirId(
                UserKebunRequest.newBuilder().setUserId("not-a-uuid").build(),
                kebunResponseObserver
        );

        assertInvalidArgument(kebunResponseObserver);
    }

    @Test
    void getSupirByKebun_WithSearchName_ReturnsMatchingSupirOnly() {
        KebunSupir otherSupir = createSupir(UUID.randomUUID(), "KB001", "Agus Supir");
        when(kebunSupirRepository.findByKodeKebun("KB001")).thenReturn(List.of(supir, otherSupir));

        grpcService.getSupirByKebun(
                GetSupirByKebunRequest.newBuilder()
                        .setKodeKebun("KB001")
                        .setSearchNama("budi")
                        .build(),
                supirResponseObserver
        );

        ArgumentCaptor<GetSupirByKebunResponse> captor = ArgumentCaptor.forClass(GetSupirByKebunResponse.class);
        verify(supirResponseObserver).onNext(captor.capture());
        verify(supirResponseObserver).onCompleted();

        GetSupirByKebunResponse response = captor.getValue();
        assertEquals(1, response.getSupirCount());
        assertEquals("Budi Supir", response.getSupir(0).getNamaSupir());
    }

    @Test
    void validateMandorSupirSameKebun_WhenSameKebun_ReturnsValid() {
        when(kebunRepository.findByMandorId(mandorId)).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findById(supirId)).thenReturn(Optional.of(supir));
        when(kebunRepository.findById("KB001")).thenReturn(Optional.of(kebun));

        grpcService.validateMandorSupirSameKebun(
                ValidateMandorSupirSameKebunRequest.newBuilder()
                        .setMandorId(mandorId.toString())
                        .setSupirId(supirId.toString())
                        .build(),
                sameKebunResponseObserver
        );

        ValidateMandorSupirSameKebunResponse response = captureSameKebunResponse();
        assertTrue(response.getValid());
        assertEquals("KB001", response.getKodeKebunMandor());
        assertEquals("KB001", response.getKodeKebunSupir());
    }

    @Test
    void validateMandorSupirSameKebun_WhenDifferentKebun_ReturnsInvalid() {
        Kebun otherKebun = createKebun("KB002", "Kebun Jauh", UUID.randomUUID());
        KebunSupir otherSupir = createSupir(supirId, "KB002", "Budi Supir");
        when(kebunRepository.findByMandorId(mandorId)).thenReturn(Optional.of(kebun));
        when(kebunSupirRepository.findById(supirId)).thenReturn(Optional.of(otherSupir));
        when(kebunRepository.findById("KB002")).thenReturn(Optional.of(otherKebun));

        grpcService.validateMandorSupirSameKebun(
                ValidateMandorSupirSameKebunRequest.newBuilder()
                        .setMandorId(mandorId.toString())
                        .setSupirId(supirId.toString())
                        .build(),
                sameKebunResponseObserver
        );

        ValidateMandorSupirSameKebunResponse response = captureSameKebunResponse();
        assertFalse(response.getValid());
        assertEquals("Mandor and Supir are assigned to different kebun", response.getMessage());
    }

    @Test
    void validateMandorSupirSameKebun_WhenInvalidUuid_ReturnsInvalidArgument() {
        grpcService.validateMandorSupirSameKebun(
                ValidateMandorSupirSameKebunRequest.newBuilder()
                        .setMandorId("not-a-uuid")
                        .setSupirId(supirId.toString())
                        .build(),
                sameKebunResponseObserver
        );

        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(sameKebunResponseObserver).onError(captor.capture());
        verify(sameKebunResponseObserver, never()).onNext(any());
        verify(sameKebunResponseObserver, never()).onCompleted();

        StatusRuntimeException error = assertInstanceOf(StatusRuntimeException.class, captor.getValue());
        assertEquals(Status.INVALID_ARGUMENT.getCode(), error.getStatus().getCode());
    }

    private KebunResponse captureKebunResponse() {
        ArgumentCaptor<KebunResponse> captor = ArgumentCaptor.forClass(KebunResponse.class);
        verify(kebunResponseObserver).onNext(captor.capture());
        verify(kebunResponseObserver).onCompleted();
        return captor.getValue();
    }

    private ValidateMandorSupirSameKebunResponse captureSameKebunResponse() {
        ArgumentCaptor<ValidateMandorSupirSameKebunResponse> captor =
                ArgumentCaptor.forClass(ValidateMandorSupirSameKebunResponse.class);
        verify(sameKebunResponseObserver).onNext(captor.capture());
        verify(sameKebunResponseObserver).onCompleted();
        return captor.getValue();
    }

    private void assertInvalidArgument(StreamObserver<KebunResponse> observer) {
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(observer).onError(captor.capture());
        verify(observer, never()).onNext(any());
        verify(observer, never()).onCompleted();

        StatusRuntimeException error = assertInstanceOf(StatusRuntimeException.class, captor.getValue());
        assertEquals(Status.INVALID_ARGUMENT.getCode(), error.getStatus().getCode());
    }

    private Kebun createKebun(String kodeKebun, String namaKebun, UUID mandorId) {
        Kebun item = new Kebun();
        item.setKodeKebun(kodeKebun);
        item.setNamaKebun(namaKebun);
        item.setLuasHektare(100);
        item.setKoordinat("{}");
        item.setMandorId(mandorId);
        item.setMandorNama("Andi Mandor");
        return item;
    }

    private KebunSupir createSupir(UUID id, String kodeKebun, String namaSupir) {
        KebunSupir item = new KebunSupir();
        item.setSupirId(id);
        item.setKodeKebun(kodeKebun);
        item.setNamaSupir(namaSupir);
        return item;
    }
}
