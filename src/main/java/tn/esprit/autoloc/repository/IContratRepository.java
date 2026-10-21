package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // [SonarLint] Redundant annotation import
import tn.esprit.autoloc.domain.Contrat;
import java.util.Collections; // [SonarLint] java:S1128 - Unused import

@Repository // [SonarLint] Spring Data repository interfaces do not need @Repository
public interface IContratRepository extends JpaRepository<Contrat, Long> {
}
