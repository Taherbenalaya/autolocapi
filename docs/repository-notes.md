# Atelier 3 — Notes sur les Repositories et Qualite de code

## 1. Choix des interfaces Repository

Toutes nos interfaces dans `tn.esprit.autoloc.repository` etendent `JpaRepository<Entite, Long>`.

| Interface | Etend | Justification |
| :--- | :--- | :--- |
| `IContratRepository` | `JpaRepository<Contrat, Long>` | CRUD complet, gestion directe sous forme de `List` (plus pratique qu'Iterable) et acces aux methodes comme `saveAndFlush`. |
| `IPaiementRepository` | `JpaRepository<Paiement, Long>` | Permet de rechercher et consulter les paiements, meme si les ajouts passent par le contrat (composition). |
| `IVehiculeRepository` | `JpaRepository<Vehicule, Long>` | Permet de paginer et trier la liste des vehicules de la flotte. |
| `IClientRepository` | `JpaRepository<Client, Long>` | CRUD complet et pagination pour afficher les clients. |
| `IAgenceRepository` | `JpaRepository<Agence, Long>` | CRUD complet et recuperation de listes directes pour les formulaires. |
| `IEmployeRepository` | `JpaRepository<Employe, Long>` | Gestion et tri du personnel par agence. |
| `IEquipementRepository` | `JpaRepository<Equipement, Long>` | Lecture et manipulation de la liste des options/equipements. |
| `IReservationRepository` | `JpaRepository<Reservation, Long>` | Tri par dates et pagination des reservations. |
| `IMaintenanceRepository` | `JpaRepository<Maintenance, Long>` | Historique des maintenances avec tri par date. |

### Points importants retenus :
- `CrudRepository` renvoie des `Iterable`, alors que `JpaRepository` (via `ListCrudRepository`) renvoie directement des `List`, ce qui evite de convertir a la main.
- `PagingAndSortingRepository` est inclus dans `JpaRepository`, pratique pour la pagination.
- Attention avec `deleteAllInBatch()` : il fait un `DELETE` direct en SQL sans passer par Hibernate, donc il ne respecte pas le cascade `REMOVE` ni l'`orphanRemoval`.

---

## 2. Analyse SonarQube for IDE (SonarLint)

Anomalies presentes dans le code a observer et corriger avec le plugin SonarQube / SonarLint dans IntelliJ :

| Fichier cible | Anomalie detectee | Regle Sonar / Explication | Correction a apporter |
| :--- | :--- | :--- | :--- |
| `Vehicule.java` | Imports inutilises (`java.util.Date`, `java.io.Serializable`, `java.util.HashMap`) | `java:S1128` : Les imports inutilises polluent le code et ralentissent la maintenance. | Supprimer les lignes d'import inutilisees (ou `Ctrl + Alt + O` / `Optimize Imports`). |
| `Vehicule.java` | Champ prive inutilise (`tempCode`) | `java:S1068` : "Unused private fields should be removed" (code mort). | Supprimer l'attribut `tempCode` qui n'a ni getter ni setter. |
| `IVehiculeRepository.java` | Modificateurs redondants `public abstract` sur methode d'interface | `java:S2333` : Dans une interface Java, les methodes sont implicitement `public` et `abstract`. | Enlever `public abstract` devant la signature de methode. |
| `IContratRepository.java` | Annotation `@Repository` redondante sur interface Spring Data | Les interfaces qui etendent `JpaRepository` sont automatiquement reconnues par Spring Boot au demarrage. | Supprimer `@Repository` et son import. |
| `Client.java` | Nom de constante non conforme (`max_clients`) | `java:S115` : Les constantes `static final` doivent respecter la convention MAJUSCULE_AVEC_UNDERSCORE. | Renommer en `MAX_CLIENTS` ou supprimer si inutile. |
