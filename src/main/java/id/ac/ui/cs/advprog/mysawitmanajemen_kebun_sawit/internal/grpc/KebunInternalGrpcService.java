package id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.internal.grpc;

import id.ac.ui.cs.advprog.mysawit.grpc.kebun.GetKebunByKodeRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.GetSupirByKebunRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.GetSupirByKebunResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.KebunInternalServiceGrpc;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.KebunResponse;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.SupirAssignment;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.UserKebunRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.ValidateMandorSupirSameKebunRequest;
import id.ac.ui.cs.advprog.mysawit.grpc.kebun.ValidateMandorSupirSameKebunResponse;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.Kebun;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.model.KebunSupir;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunRepository;
import id.ac.ui.cs.advprog.mysawitmanajemen_kebun_sawit.repository.KebunSupirRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class KebunInternalGrpcService extends KebunInternalServiceGrpc.KebunInternalServiceImplBase {

    private final KebunRepository kebunRepository;
    private final KebunSupirRepository kebunSupirRepository;

    public KebunInternalGrpcService(
            KebunRepository kebunRepository,
            KebunSupirRepository kebunSupirRepository
    ) {
        this.kebunRepository = kebunRepository;
        this.kebunSupirRepository = kebunSupirRepository;
    }

    @Override
    public void getKebunByKode(
            GetKebunByKodeRequest request,
            StreamObserver<KebunResponse> responseObserver
    ) {
        KebunResponse response = kebunRepository.findById(request.getKodeKebun())
                .map(this::toGrpcKebunResponse)
                .orElseGet(() -> notFoundKebunResponse(request.getKodeKebun()));

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getKebunByMandorId(
            UserKebunRequest request,
            StreamObserver<KebunResponse> responseObserver
    ) {
        UUID mandorId;
        try {
            mandorId = parseUuid(request.getUserId(), "user_id");
        } catch (IllegalArgumentException e) {
            responseObserver.onError(invalidArgument(e.getMessage()));
            return;
        }

        KebunResponse response = kebunRepository.findByMandorId(mandorId)
                .map(this::toGrpcKebunResponse)
                .orElseGet(() -> notFoundKebunResponse(""));

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getKebunBySupirId(
            UserKebunRequest request,
            StreamObserver<KebunResponse> responseObserver
    ) {
        UUID supirId;
        try {
            supirId = parseUuid(request.getUserId(), "user_id");
        } catch (IllegalArgumentException e) {
            responseObserver.onError(invalidArgument(e.getMessage()));
            return;
        }

        KebunResponse response = kebunSupirRepository.findById(supirId)
                .flatMap(supir -> kebunRepository.findById(supir.getKodeKebun()))
                .map(this::toGrpcKebunResponse)
                .orElseGet(() -> notFoundKebunResponse(""));

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getSupirByKebun(
            GetSupirByKebunRequest request,
            StreamObserver<GetSupirByKebunResponse> responseObserver
    ) {
        List<SupirAssignment> supir = kebunSupirRepository.findByKodeKebun(request.getKodeKebun()).stream()
                .filter(item -> matchesSearchName(item, request.getSearchNama()))
                .map(this::toGrpcSupirAssignment)
                .toList();

        responseObserver.onNext(GetSupirByKebunResponse.newBuilder()
                .addAllSupir(supir)
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void validateMandorSupirSameKebun(
            ValidateMandorSupirSameKebunRequest request,
            StreamObserver<ValidateMandorSupirSameKebunResponse> responseObserver
    ) {
        UUID mandorId;
        UUID supirId;
        try {
            mandorId = parseUuid(request.getMandorId(), "mandor_id");
            supirId = parseUuid(request.getSupirId(), "supir_id");
        } catch (IllegalArgumentException e) {
            responseObserver.onError(invalidArgument(e.getMessage()));
            return;
        }

        Optional<Kebun> kebunMandor = kebunRepository.findByMandorId(mandorId);
        Optional<KebunSupir> supirAssignment = kebunSupirRepository.findById(supirId);
        Optional<Kebun> kebunSupir = supirAssignment.flatMap(supir -> kebunRepository.findById(supir.getKodeKebun()));
        String kodeKebunMandor = kebunMandor.map(Kebun::getKodeKebun).orElse("");
        String kodeKebunSupir = kebunSupir.map(Kebun::getKodeKebun).orElse("");
        boolean valid = !kodeKebunMandor.isBlank() && kodeKebunMandor.equals(kodeKebunSupir);

        ValidateMandorSupirSameKebunResponse response = ValidateMandorSupirSameKebunResponse.newBuilder()
                .setValid(valid)
                .setMandorId(request.getMandorId())
                .setSupirId(request.getSupirId())
                .setKodeKebunMandor(kodeKebunMandor)
                .setKodeKebunSupir(kodeKebunSupir)
                .setMessage(buildSameKebunMessage(kebunMandor.isPresent(), kebunSupir.isPresent(), valid))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private KebunResponse toGrpcKebunResponse(Kebun kebun) {
        return KebunResponse.newBuilder()
                .setKodeKebun(toStringOrEmpty(kebun.getKodeKebun()))
                .setNamaKebun(toStringOrEmpty(kebun.getNamaKebun()))
                .setLuasHektare(kebun.getLuasHektare() == null ? 0 : kebun.getLuasHektare())
                .setKoordinat(toStringOrEmpty(kebun.getKoordinat()))
                .setMandorId(toStringOrEmpty(kebun.getMandorId()))
                .setMandorNama(toStringOrEmpty(kebun.getMandorNama()))
                .addAllSupir(kebunSupirRepository.findByKodeKebun(kebun.getKodeKebun()).stream()
                        .map(this::toGrpcSupirAssignment)
                        .toList())
                .setFound(true)
                .build();
    }

    private KebunResponse notFoundKebunResponse(String kodeKebun) {
        return KebunResponse.newBuilder()
                .setKodeKebun(toStringOrEmpty(kodeKebun))
                .setFound(false)
                .build();
    }

    private SupirAssignment toGrpcSupirAssignment(KebunSupir supir) {
        return SupirAssignment.newBuilder()
                .setSupirId(toStringOrEmpty(supir.getSupirId()))
                .setNamaSupir(toStringOrEmpty(supir.getNamaSupir()))
                .build();
    }

    private boolean matchesSearchName(KebunSupir supir, String searchName) {
        if (searchName == null || searchName.isBlank()) {
            return true;
        }
        return supir.getNamaSupir() != null
                && supir.getNamaSupir().toLowerCase(Locale.ROOT)
                .contains(searchName.toLowerCase(Locale.ROOT));
    }

    private String buildSameKebunMessage(boolean mandorFound, boolean supirFound, boolean valid) {
        if (!mandorFound) {
            return "Kebun Mandor tidak ditemukan";
        }
        if (!supirFound) {
            return "Kebun Supir tidak ditemukan";
        }
        return valid ? "Mandor and Supir are assigned to the same kebun"
                : "Mandor and Supir are assigned to different kebun";
    }

    private UUID parseUuid(String rawValue, String fieldName) {
        try {
            return UUID.fromString(rawValue);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid UUID", e);
        }
    }

    private RuntimeException invalidArgument(String message) {
        return Status.INVALID_ARGUMENT
                .withDescription(message)
                .asRuntimeException();
    }

    private String toStringOrEmpty(Object value) {
        return value == null ? "" : value.toString();
    }
}
