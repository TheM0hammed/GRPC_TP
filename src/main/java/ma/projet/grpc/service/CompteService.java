package ma.projet.grpc.service;

import ma.projet.grpc.entities.Compte;
import ma.projet.grpc.repository.CompteRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CompteService {

    private final CompteRepository compteRepository;

    public CompteService(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }
    public List<Compte> findAllComptes() {
        return compteRepository.findAll();
    }
    public Compte findCompteById(String id) {
        return compteRepository.findById(id).orElse(null);
    }
    public Compte saveCompte(Compte compte) {
        return compteRepository.save(compte);

    }
    public void deleteComptebyId(String id) {
        compteRepository.deleteById(id);
    }
    public List<Compte> findAllByType(String type) {
        return compteRepository.findAllByType(type);
    }
}
