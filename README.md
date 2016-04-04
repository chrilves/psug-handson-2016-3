#Code source du [PSUG #61](www.meetup.com/fr-FR/Paris-Scala-User-Group-PSUG/events/229594321)
## Type level computations in Scala - *Quelques techniques et astuces*

Dépôt des sources du 61ième meetup du [Paris Scala User Group](http://www.meetup.com/fr-FR/Paris-Scala-User-Group-PSUG).  La présentation est dans le fichier [slides/index.md](https://github.com/christophe-calves/psug-handson-2016-3/blob/master/slides/index.md), lisible avec [reveal-md](https://github.com/webpro/reveal-md) : `reveal-md slides/index.md` .  La branche `master` contient l’énoncé du Hands'On tandis que la branche `solution` contient le code complet.

Les sources sont organisées de la manière suivante:

- `handson.uvec` : implémentation "non typée" des vecteurs. Le type d'un vecteur ne contient pas sa taille. Cette version sert de base et de motivation pour le reste de la session.
-  `handson.preuve` : implémentation typée des vecteur par une approche orientée preuves.
 * `handson.preuve.endo` : restriction aux opération qui préservent la taille (*zip*, *ap*, *map*).
 * `handson.preuve.prolog` et `handson.preuve.pdt`: opérations de concaténation et de produit cartésien.
- `handson.calcul`: implémentation typée des vecteur par une approche orientée calcul.
- `handson.binaire`: implémentation efficace des entiers naturel positifs au type-level par une représentation binaire.
