# Notes sur la Couche Service, l'IoC et l'Injection de Dépendances

## A. Modes d'injection (Constructeur vs Attribut)

On choisit l'injection par **constructeur** (plutôt que `@Autowired` sur les attributs) car :
* C'est plus sûr : on peut mettre les variables en `final` (elles ne bougeront plus).
* C'est plus clair : on voit tout de suite de quoi la classe a besoin.
* C'est facile à tester : on peut créer l'objet avec un simple `new` dans nos tests.
* SonarQube préfère ça et ne lève plus d'alerte.

## B. Implémentation de nos Services

* **La structure :** Pour chaque table, on a fait une interface (ex: `IContratService`) et sa classe d'implémentation (`ContratServiceImpl`).
* **L'injection :** On utilise l'annotation Lombok `@RequiredArgsConstructor`. Ça crée le constructeur automatiquement pour nos champs `final`.
* **Le cas du Paiement :** Le service des paiements ne fait que de la lecture. Si on veut ajouter ou modifier un paiement, on passe par le service du Contrat. Ça évite de faire des bêtises avec les données.

## C. Les erreurs Spring au démarrage

Si l'application plante au lancement, c'est souvent l'un de ces 3 cas :
* **`NoSuchBeanDefinition` :** Spring ne trouve pas le composant. 
  * *Correction :* On a oublié l'annotation `@Service`, ou la classe n'est pas dans le bon dossier (package).
* **`NoUniqueBeanDefinition` :** Spring trouve deux composants possibles et ne sait pas lequel choisir. 
  * *Correction :* On ajoute `@Primary` sur celui par défaut, ou on utilise `@Qualifier` pour pointer le bon.
* **Dépendance circulaire :** Le Service A appelle le B, qui rappelle le A. Ça bloque tout.
  * *Correction :* Il faut revoir le code et créer un 3ème service indépendant pour gérer ce qu'ils ont en commun.

## D. Corrections SonarQube

Ce qu'on a nettoyé après l'analyse :
* **Fini les `@Autowired` sur les variables (S6813) :** On est passé par le constructeur.
* **Fini les exceptions génériques (S112) :** On a arrêté d'utiliser `RuntimeException` partout et on a créé notre vraie exception `ResourceNotFoundException`.
* **Ménage dans les imports (S1128) :** On a supprimé tous les imports grisés qui ne servaient plus à rien.

## E. 5 choses importantes à retenir

1. **Pas de `new` pour nos services :** C'est Spring qui s'occupe de créer les objets au démarrage de l'appli (grâce à `@Service`).
2. **On dépend de l'interface :** Le contrôleur utilise `IContratService`, pas la classe d'implémentation. Ça permet de changer le code derrière sans tout casser.
3. **Des services "sans état" (stateless) :** Un service est un singleton (une seule instance pour toute l'appli). Si on y stocke des données d'un utilisateur, le prochain utilisateur va tout écraser.
4. **On charge avant de sauvegarder (Update) :** Avant de modifier en base, on récupère d'abord l'entité existante. Si on sauvegarde direct ce qu'on reçoit, on risque d'écraser et de vider des champs importants.
5. **Comprendre les annotations :** 
   * `@Component` (ou `@Service`) = on le met sur NOS classes pour que Spring les trouve.
   * `@Bean` = on le met sur une méthode pour dire à Spring d'utiliser une classe qu'on n'a pas créée (comme une librairie).