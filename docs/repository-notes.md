# Notes d'Atelier 3 — Couche Repository Spring Data JPA et Qualité de Code

## 1. Justification du choix de l'interface `JpaRepository`

Pour l'ensemble des neuf entités du domaine AutoLoc, le choix s'est porté sur **`JpaRepository<T, ID>`**.

### Tableau comparatif et justifications

| Interface | Étend | Justification |
| :--- | :--- | :--- |
| **`IContratRepository`** | `JpaRepository<Contrat, Long>` | CRUD complet, pagination, tri et méthodes spécifiques JPA (`saveAndFlush`, `flush`). Permet de gérer le cycle de vie du contrat et ses paiements associés. |
| **`IPaiementRepository`** | `JpaRepository<Paiement, Long>` | Permet la lecture directe, la pagination et le reporting des paiements, tout en respectant la règle métier qui délègue les ajouts/suppressions à l'agrégat parent `Contrat`. |
| **`IVehiculeRepository`** | `JpaRepository<Vehicule, Long>` | CRUD complet, pagination et tri indispensables pour afficher et filtrer la flotte de véhicules par statut, catégorie ou tarif. |
| **`IClientRepository`** | `JpaRepository<Client, Long>` | Gestion complète des clients, pagination pour la gestion des listes volumineuses et recherche par identifiant. |
| **`IAgenceRepository`** | `JpaRepository<Agence, Long>` | CRUD complet, retour des listes directes (`ListCrudRepository`) pour alimenter les listes déroulantes d'agences. |
| **`IEmployeRepository`** | `JpaRepository<Employe, Long>` | CRUD complet et pagination pour la gestion du personnel par agence. |
| **`IEquipementRepository`** | `JpaRepository<Equipement, Long>` | Lecture et association rapide des équipements aux véhicules (ManyToMany). |
| **`IReservationRepository`** | `JpaRepository<Reservation, Long>` | Gestion des réservations avec pagination et tri temporel (par `dateDebut`, `dateFin`). |
| **`IMaintenanceRepository`** | `JpaRepository<Maintenance, Long>` | Suivi historique des opérations d'entretien avec tri par date et pagination. |

### Rappel des différences entre interfaces Spring Data JPA

1. **`CrudRepository<T, ID>`** : Fournit les opérations CRUD de base. Les méthodes retournent des `Iterable<T>`, ce qui nécessite une conversion manuelle pour les manipulations de listes.
2. **`ListCrudRepository<T, ID>`** : Identique à `CrudRepository`, mais retourne des `List<T>`, facilitant les opérations Stream et collections.
3. **`PagingAndSortingRepository<T, ID>`** : Fournit le tri (`Sort`) et la pagination (`Pageable`).
4. **`JpaRepository<T, ID>`** : Regroupe `ListCrudRepository`, `ListPagingAndSortingRepository` et ajoute les fonctionnalités propres à JPA (`flush()`, `saveAndFlush()`, `getReferenceById()`, etc.).
5. **Attention aux suppressions en lot (`deleteAllInBatch`)** : Elles contournent le contexte de persistance (pas de cascade `REMOVE`, pas d'`orphanRemoval`), risquant de violer les contraintes de clés étrangères (par exemple entre `Contrat` et `Paiement`).

---

## 2. Analyse SonarQube for IDE (SonarLint) et corrections apportées

| Anomalie SonarQube for IDE | Règle / Explication | Correction apportée |
| :--- | :--- | :--- |
| **Utilisation de `@Data` sur des entités JPA bidirectionnelles** | *java:S2160 / Best Practices JPA* : L'annotation `@Data` de Lombok génère automatiquement `equals`, `hashCode` et `toString`. Sur des entités avec associations bidirectionnelles (ex. `Contrat` ⟷ `Paiement` ou `Vehicule` ⟷ `Reservation`), cela provoque des boucles récursives infinies (`StackOverflowError`). | Remplacement de `@Data` par des annotations ciblées : `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor` sur les 9 entités. |
| **Imports inutilisés ou redondants** | *java:S1128* : Les déclarations d'imports inutilisés polluent le code source et augmentent la dette technique. | Suppression de tous les imports superflus dans l'ensemble des classes du package `domain` et `repository`. |
| **Visibilité des attributs et encapsulation** | *java:S1068 / java:S1206* : Les champs d'entités doivent être strictement privés avec accesseurs contrôlés pour respecter le principe d'encapsulation Clean Code. | Vérification et garantie de la visibilité `private` sur l'ensemble des attributs de toutes les entités du projet. |
| **Typage précis des identifiants et montants** | *java:S2111* : Utilisation de `BigDecimal` avec précision et échelle explicites (`precision = 10, scale = 2`) pour éviter les imprécisions d'arrondi sur les valeurs monétaires (`tarifJournalier`, `montantTotal`, `montant`). | Spécification stricte de `BigDecimal` et `@Column(precision = 10, scale = 2)` sur les montants financiers. |
