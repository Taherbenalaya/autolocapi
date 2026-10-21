# Atelier 5 : Notes d'Architecture API REST, Documentation Swagger et Tests Postman

---

## A. Endpoints

Le préfixe commun configuré dans `application.properties` est `server.servlet.context-path=/api`.  
Tous les endpoints sont donc exposés sous l'URL de base : `http://localhost:8080/api`.

| Contrôleur | Verbe HTTP | Route relative | Service appelé | Code Succès | Codes Erreur |
| :--- | :--- | :--- | :--- | :---: | :---: |
| **ContratController** | `GET` | `/contrats` | `contratService.findAll()` | 200 | — |
| | `GET` | `/contrats/{id}` | `contratService.findById(id)` | 200 | 404 |
| | `POST` | `/contrats` | `contratService.create(contrat)` | 201 | 400 |
| | `PUT` | `/contrats/{id}` | `contratService.update(id, contrat)` | 200 | 400, 404 |
| | `DELETE` | `/contrats/{id}` | `contratService.deleteById(id)` | 204 | 404 |
| **PaiementController** | `GET` | `/paiements` | `paiementService.findAll()` | 200 | — |
| | `GET` | `/paiements/{id}` | `paiementService.findById(id)` | 200 | 404 |
| | `POST` *(non exposé)* | `/paiements` | — | — | 405 |
| **AgenceController** | `GET` | `/agences` | `agenceService.findAll()` | 200 | — |
| | `GET` | `/agences/{id}` | `agenceService.findById(id)` | 200 | 404 |
| | `POST` | `/agences` | `agenceService.create(agence)` | 201 | 400 |
| | `PUT` | `/agences/{id}` | `agenceService.update(id, agence)` | 200 | 400, 404 |
| | `DELETE` | `/agences/{id}` | `agenceService.deleteById(id)` | 204 | 404 |
| **EmployeController** | `GET` | `/employes` | `employeService.findAll()` | 200 | — |
| | `GET` | `/employes/{id}` | `employeService.findById(id)` | 200 | 404 |
| | `POST` | `/employes` | `employeService.create(employe)` | 201 | 400 |
| | `PUT` | `/employes/{id}` | `employeService.update(id, employe)` | 200 | 400, 404 |
| | `DELETE` | `/employes/{id}` | `employeService.deleteById(id)` | 204 | 404 |
| **VehiculeController** | `GET` | `/vehicules` | `vehiculeService.findAll()` | 200 | — |
| | `GET` | `/vehicules/{id}` | `vehiculeService.findById(id)` | 200 | 404 |
| | `POST` | `/vehicules` | `vehiculeService.create(vehicule)` | 201 | 400 |
| | `PUT` | `/vehicules/{id}` | `vehiculeService.update(id, vehicule)` | 200 | 400, 404 |
| | `DELETE` | `/vehicules/{id}` | `vehiculeService.deleteById(id)` | 204 | 404 |
| **EquipementController** | `GET` | `/equipements` | `equipementService.findAll()` | 200 | — |
| | `GET` | `/equipements/{id}` | `equipementService.findById(id)` | 200 | 404 |
| | `POST` | `/equipements` | `equipementService.create(equipement)` | 201 | 400 |
| | `PUT` | `/equipements/{id}` | `equipementService.update(id, equipement)` | 200 | 400, 404 |
| | `DELETE` | `/equipements/{id}` | `equipementService.deleteById(id)` | 204 | 404 |
| **ClientController** | `GET` | `/clients` | `clientService.findAll()` | 200 | — |
| | `GET` | `/clients/{id}` | `clientService.findById(id)` | 200 | 404 |
| | `POST` | `/clients` | `clientService.create(client)` | 201 | 400 |
| | `PUT` | `/clients/{id}` | `clientService.update(id, client)` | 200 | 400, 404 |
| | `DELETE` | `/clients/{id}` | `clientService.deleteById(id)` | 204 | 404 |
| **ReservationController**| `GET` | `/reservations` | `reservationService.findAll()` | 200 | — |
| | `GET` | `/reservations/{id}` | `reservationService.findById(id)` | 200 | 404 |
| | `POST` | `/reservations` | `reservationService.create(reservation)` | 201 | 400 |
| | `PUT` | `/reservations/{id}` | `reservationService.update(id, reservation)` | 200 | 400, 404 |
| | `DELETE` | `/reservations/{id}` | `reservationService.deleteById(id)` | 204 | 404 |
| **MaintenanceController**| `GET` | `/maintenances` | `maintenanceService.findAll()` | 200 | — |
| | `GET` | `/maintenances/{id}` | `maintenanceService.findById(id)` | 200 | 404 |
| | `POST` | `/maintenances` | `maintenanceService.create(maintenance)` | 201 | 400 |
| | `PUT` | `/maintenances/{id}` | `maintenanceService.update(id, maintenance)` | 200 | 400, 404 |
| | `DELETE` | `/maintenances/{id}` | `maintenanceService.deleteById(id)` | 204 | 404 |

---

## B. Annotations Jackson et Sérialisation

Dans une architecture sans DTO (où les entités JPA sont directement sérialisées par Jackson), les associations bidirectionnelles provoquent une boucle infinie (`StackOverflowError`) ou des erreurs de chargement paresseux (`ByteBuddyInterceptor`). Deux stratégies distinctes ont été appliquées :

| Association | Annotation retenue | Justification technique |
| :--- | :--- | :--- |
| **Contrat ⟷ Paiement** | `@JsonManagedReference("contrat-paiements")` sur `Contrat.paiements`<br>`@JsonBackReference("contrat-paiements")` sur `Paiement.contrat` | Vraie relation de composition (`cascade = ALL`, `orphanRemoval = true`). On souhaite inclure la liste des paiements dans le JSON du contrat lors de sa consultation ou de sa création, tout en évitant la boucle infinie vers le parent lors de la sérialisation du paiement. |
| **Agence ⟷ Vehicule** | `@JsonIgnore` sur `Agence.vehicules` | L'agence ne doit pas embarquer tout son parc automobile lors d'un simple `GET /agences`. Le véhicule référence l'agence par son identifiant (`{"agence": {"idAgence": 1}}`). |
| **Agence ⟷ Employe** | `@JsonIgnore` sur `Agence.employes` | Évite d'embarquer la liste des employés dans l'agence. L'employé référence son agence directement. |
| **Client ⟷ Reservation** | `@JsonIgnore` sur `Client.reservations` | Évite le chargement complet des réservations lors de la lecture d'un client. |
| **Vehicule ⟷ Reservation** | `@JsonIgnore` sur `Vehicule.reservations` | Évite la boucle de sérialisation et le chargement inutile de tout l'historique des réservations du véhicule. |
| **Vehicule ⟷ Maintenance** | `@JsonIgnore` sur `Vehicule.maintenances` | Évite le chargement de l'historique des opérations de maintenance lors de l'accès au véhicule. |
| **Vehicule ⟷ Equipement** | `@JsonIgnoreProperties("vehicules")` sur `Vehicule.equipements` et `@JsonIgnore` sur `Equipement.vehicules` | Association Many-to-Many : permet d'afficher les équipements d'un véhicule sans réinjecter les véhicules liés à chaque équipement. |
| **Reservation ⟷ Contrat** | `@JsonIgnore` sur `Reservation.contrat` | Évite la boucle One-to-One entre la réservation et son contrat. |

### Différence fondamentale entre `@JsonBackReference` et `@JsonIgnore` :
- **`@JsonBackReference`** : Fait partie d'une paire (`@JsonManagedReference` / `@JsonBackReference`). Le champ n'est pas sérialisé en JSON sortant (évite la récursion), **mais** lors de la désérialisation d'un objet parent contenant ses enfants, Jackson restaure automatiquement la référence arrière vers l'objet parent dans l'entité enfant Java.
- **`@JsonIgnore`** : Ignore totalement le champ aussi bien en sérialisation qu'en désérialisation par défaut. Si on l'utilisait sur `Paiement.contrat`, le champ resterait `null` en base de données lors d'une création en cascade via `Contrat`.

---

## C. Rôles et Complémentarité de Postman et Swagger

| Critère | Postman | Swagger UI (SpringDoc OpenAPI) |
| :--- | :--- | :--- |
| **Rôle principal** | Outil de test, d'automatisation, et de validation des requêtes HTTP. | Outil de documentation interactive et de formalisation du contrat d'API. |
| **Cas d'usage concret rencontré** | Permet de créer des chaînes de requêtes dynamiques : par exemple, récupérer l'identifiant généré après un `POST /api/contrats` via `pm.environment.set("contratId", pm.response.json().idContrat)` pour alimenter automatiquement les tests suivants (`GET`, `PUT`, `DELETE`). Permet aussi de valider les codes de retour négatifs (400, 404, 405) via des assertions automatisées (`pm.test`). | Permet à un développeur tiers ou frontend de visualiser en un coup d'œil l'ensemble des schémas d'objets, les paramètres acceptés, et d'exécuter un test rapide directement depuis le navigateur sur `http://localhost:8080/api/swagger-ui.html` sans installation préalable. |
| **Complémentarité** | Postman valide le comportement dynamique et la logique métier de l'API au fil des développements. | Swagger garantit que le contrat exposé reste en permanence synchronisé avec le code source Java. |

---

## D. Analyse SonarQube for IDE

L'analyse de `tn.esprit.autoloc.web.controller` met en évidence plusieurs règles de qualité :

| Règle SonarQube | Type & Sévérité | Explication | Correction appliquée |
| :--- | :--- | :--- | :--- |
| **`java:S2208`** : *"Wildcard imports should not be used"* | Code Smell (Minor) | L'utilisation d'imports avec joker (`import org.springframework.web.bind.annotation.*;`) nuit à la lisibilité et peut introduire des ambiguïtés de nommage. | Remplacer les imports avec joker par les imports explicites de chaque annotation (`@RestController`, `@GetMapping`, etc.). |
| **`java:S1128`** : *"Unused imports should be removed"* | Code Smell (Minor) | Import déclaré mais non référencé dans le fichier (ex. `import java.util.ArrayList;` dans `PaiementController.java`), encombrant le code sans utilité. | Nettoyer les fichiers en supprimant les déclarations d'imports superflues (raccourci `Ctrl + Alt + O` sous IntelliJ IDEA). |
| **`java:S4684`** : *"Persistent entities should not be used as arguments of '@RequestMapping' methods"* | Security / Design (Major) | Les entités `@Entity` de JPA ne doivent pas être exposées directement en paramètres ou retours de contrôleurs REST (risque d'exposition de schéma interne et de failles Mass Assignment). | Architecture intentionnelle et temporaire pour l'Atelier 5 ; cette anomalie architecturale sera résolue dans le Chapitre 10 avec la mise en place des DTO (Data Transfer Objects) et des mappers. |

---

## E. Réponses aux Questions de Compréhension

### 1. Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur à appeler ?
Toutes les requêtes HTTP arrivant sur l'application sont réceptionnées par la **`DispatcherServlet`** (agissant en tant que *Front Controller* central de Spring MVC).  
Pour retrouver la méthode cible, la `DispatcherServlet` consulte le composant **`HandlerMapping`** (plus précisément `RequestMappingHandlerMapping`), qui maintient une table de correspondance construite au démarrage à partir des annotations `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc. en croisant l'URI et le verbe HTTP.

### 2. Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de `ContratServiceImpl` ?
- **Non de `ContratRepository`** : Pour respecter le principe de séparation des responsabilités et l'architecture en couches. Le contrôleur s'occupe de la couche de présentation (requêtes HTTP, codes de statut, sérialisation). Les règles métier, la logique de validation, et la gestion transactionnelle appartiennent exclusivement à la couche Service.
- **Non de `ContratServiceImpl`** : Pour respecter le principe d'Inversion des Dépendances (*Dependency Inversion Principle* du modèle SOLID). Le contrôleur dépend d'un contrat d'interface abstrait (`IContratService`), garantissant un couplage lâche, facilitant le remplacement de l'implémentation et permettant la réalisation facile de mocks pour les tests unitaires.

### 3. Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?
- **`POST` renvoie 201 (Created)** : Indique que la requête a abouti à la création effective d'une nouvelle ressource sur le serveur, identifiable et persistée en base.
- **`DELETE` renvoie 204 (No Content)** : Indique que l'opération de suppression demandée a été exécutée avec succès sur le serveur et qu'il n'y a aucune entité ou corps de contenu à retourner au client.
- **404 (Not Found) vs 500 (Internal Server Error)** : Une ressource absente correspond à une erreur imputable au client (l'identifiant fourni n'existe pas en base). Renvoyer une erreur 500 indiquerait faussement un dysfonctionnement interne ou un bug du serveur. L'interception de `ResourceNotFoundException` par `@RestControllerAdvice` permet de renvoyer proprement un code 404 avec un message explicatif.

### 4. Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST `/api/paiements` ?
- `Paiement` est dans une association de **composition stricte** avec `Contrat` (`cascade = CascadeType.ALL`, `orphanRemoval = true`). Les paiements n'ont pas d'existence propre hors d'un contrat : leur cycle de vie (création, modification, suppression) est entièrement piloté par `ContratController`. `PaiementController` ne sert qu'à la consultation individuelle ou globale (lecture seule).
- Un `POST` envoyé vers `/api/paiements` reçoit une réponse HTTP **`405 Method Not Allowed`**, car aucune méthode du contrôleur n'est annotée pour traiter les requêtes POST sur cette route.

### 5. Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?
- **Risques de l'exposition directe des entités JPA** :
  1. *Couplage fort* : Toute modification du schéma relationnel de la base impacte directement les clients de l'API.
  2. *Failles de sécurité (Mass Assignment)* : Un utilisateur malveillant peut injecter des champs internes qu'il ne devrait pas modifier.
  3. *Problèmes de performance et proxies* : Risque de requêtes SQL N+1 ou d'exceptions de sérialisation (`ByteBuddyInterceptor` sur les proxys `Lazy`).
  4. *Boucles infinies* : Récursivité de sérialisation JSON (`StackOverflowError`) causée par les relations bidirectionnelles.
- **Différence `@JsonManagedReference` vs `@JsonIgnore`** :
  - `Contrat.paiements` utilise `@JsonManagedReference` (combiné à `@JsonBackReference` sur `Paiement`) parce qu'il s'agit d'une composition où le contrat doit impérativement sérialiser la liste imbriquée de ses paiements dans son JSON, et permettre à Jackson de réaffecter la référence parente lors de la création conjointe.
  - `Agence.vehicules` utilise `@JsonIgnore` parce qu'une agence et un véhicule ont des cycles de vie indépendants : sérialiser tous les véhicules d'une agence alourdirait inutilement la réponse et risquerait de charger tout le graphe d'objets en mémoire. Le lien est établi de manière simple par l'identifiant côté enfant (`{"idVehicule": ..., "agence": {"idAgence": 1}}`).
