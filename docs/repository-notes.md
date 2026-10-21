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

*(Tableau a ajuster / completer lors de mes propres tests avec le plugin SonarQube dans IntelliJ)*

| Anomalie SonarQube for IDE | Regle / explication | Correction apportee |
| :--- | :--- | :--- |
| Utilisation de `@Data` sur les entites | Risque de boucle infinie (`StackOverflowError`) dans `toString()` / `hashCode()` a cause des relations bidirectionnelles. | Remplace par `@Getter`, `@Setter`, `@NoArgsConstructor` et `@AllArgsConstructor`. |
| Imports inutilises | Imports residuels qui encombrent le code. | Nettoye avec `Optimize Imports` dans IntelliJ. |
| Visibilite des attributs | Les champs doivent etre encapsules pour respecter les conventions Clean Code. | Verifie que tous les attributs sont bien `private`. |
