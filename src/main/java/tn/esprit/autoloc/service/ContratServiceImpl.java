package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date; // [SonarLint] java:S1128 - Unused import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.repository.IContratRepository;

@Service
public class ContratServiceImpl implements IContratService {

    // [SonarLint] java:S6813 / java:S3749 - Field injection is not recommended (use constructor injection instead)
    @Autowired
    private IContratRepository contratRepository;

    @Override
    public Contrat create(Contrat contrat) {
        if (contrat.getIdContrat() != null) {
            throw new IllegalArgumentException("Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        if (contrat.getMontantTotal() != null && contrat.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total d'un contrat ne peut pas être négatif");
        }
        return contratRepository.save(contrat);
    }

    @Override
    public Contrat findById(Long id) {
        // [SonarLint] java:S112 - Generic exceptions should never be thrown (use ResourceNotFoundException)
        return contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable avec l'identifiant " + id));
    }

    @Override
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    public Contrat update(Long id, Contrat contrat) {
        if (contrat.getMontantTotal() != null && contrat.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total d'un contrat ne peut pas être négatif");
        }
        Contrat existant = findById(id);
        existant.setDateSignature(contrat.getDateSignature());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setValide(contrat.isValide());
        // contratRepository.delete(existant); // [SonarLint] java:S125 - Sections of code should not be commented out
        return contratRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new RuntimeException("Contrat introuvable avec l'identifiant " + id);
        }
        contratRepository.deleteById(id);
    }
}
