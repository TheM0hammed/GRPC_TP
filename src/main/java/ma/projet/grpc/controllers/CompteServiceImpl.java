package ma.projet.grpc.controllers;


import io.grpc.stub.StreamObserver;
import ma.projet.grpc.service.CompteService;
import ma.projet.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {

    private final Map<String, Compte> compteDB = new ConcurrentHashMap<>();

    private final CompteService compteService;

    public CompteServiceImpl(CompteService compteService) {
        this.compteService = compteService;
    }
    @Override
    public void totalSolde(GetTotalSoldeRequest request, StreamObserver<GetTotalSoldeResponse> responseObserver) {
        int count = compteDB.size();
        float sum = 0;
        for (Compte compte : compteDB.values()) {
            sum += compte.getSolde();
        }
        float average = count > 0 ? sum / count : 0;

        SoldeStats stats = SoldeStats.newBuilder()
                .setCount(count)
                .setSum(sum)
                .setAverage(average)
                .build();

        responseObserver.onNext(GetTotalSoldeResponse.newBuilder().setStats(stats).build());
        responseObserver.onCompleted();
    }
    @Override
    public void allComptes (GetAllComptesRequest request,
                            StreamObserver<GetAllComptesResponse> responseObserver){
        var comptes = compteService.findAllComptes().stream()
                .map(compte -> Compte.newBuilder()
                        .setId(compte.getId())
                        .setSolde(compte.getSolde())
                        .setDateCreation(compte.getDateCreation())
                        .setType(TypeCompte.valueOf(compte.getType()))
                        .build())
                .collect(Collectors.toList());

        responseObserver.onNext(GetAllComptesResponse.newBuilder()
                .addAllComptes(comptes).build());
        responseObserver.onCompleted();
    }
    @Override
    public void saveCompte (SaveCompteRequest request,
                            StreamObserver<SaveCompteResponse> responseObserver) {
        var compteReq = request.getCompte ();
        var compte = new ma.projet.grpc.entities.Compte();
        compte.setSolde(compteReq.getSolde());
        compte.setDateCreation(compteReq.getDateCreation());
        compte.setType (compteReq.getType().name());

        var savedCompte = compteService.saveCompte(compte);

        var grpcCompte = Compte.newBuilder()
                .setId(savedCompte.getId())
                .setSolde (savedCompte.getSolde())
                .setDateCreation (savedCompte. getDateCreation())
                .setType (TypeCompte. valueOf (savedCompte.getType()))
                .build();

        responseObserver.onNext (SaveCompteResponse.newBuilder()
                .setCompte (grpcCompte) .build());
        responseObserver. onCompleted() ;
    }

    @Override
    public void deleteCompteById(GetCompteByIdRequest request, StreamObserver<DeleteCompteResponse> responseObserver) {
        String compteId = request.getId();
        compteService.deleteComptebyId(compteId);
        DeleteCompteResponse response = DeleteCompteResponse.newBuilder()
                .setMessage("Compte supprimé.")
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void compteByType(GetCompteTypeRequest request, StreamObserver<GetCompteByTypeResponse> responseObserver) {
        var comptes = compteService.findAllByType(request.getType()).stream().map(compte -> Compte.newBuilder()
                        .setId(compte.getId())
                        .setSolde(compte.getSolde())
                        .setDateCreation(compte.getDateCreation())
                        .setType(TypeCompte.valueOf(compte.getType()))
                        .build())
                .collect(Collectors.toList());
        responseObserver.onNext(GetCompteByTypeResponse.newBuilder()
                .addAllComptes(comptes).build());
        responseObserver.onCompleted();
    }
}