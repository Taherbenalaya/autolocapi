package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Vehicule;
import java.util.List;
import java.util.Optional; // [SonarLint] java:S1128 - Unused import

public interface IVehiculeRepository extends JpaRepository<Vehicule, Long> {

    // [SonarLint] java:S2333 - Redundant 'public' and 'abstract' modifiers in interface
    public abstract List<Vehicule> findByMarque(String marque);
}
