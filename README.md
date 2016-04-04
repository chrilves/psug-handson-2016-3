#Code source du [PSUG #61](www.meetup.com/fr-FR/Paris-Scala-User-Group-PSUG/events/229594321)
## Type level computations in Scala - *Quelques techniques et astuces*

### Organisation du dépôt

Ce sont les sources du 61ième meetup du [Paris Scala User Group](http://www.meetup.com/fr-FR/Paris-Scala-User-Group-PSUG).  La présentation est dans le fichier [slides/index.md](https://github.com/christophe-calves/psug-handson-2016-3/blob/master/slides/index.md), lisible avec [reveal-md](https://github.com/webpro/reveal-md) : `reveal-md slides/index.md` .  La branche `master` contient l’énoncé du Hands'On tandis que la branche `solution` contient le code complet.

Les sources sont organisées de la manière suivante:

- `handson.uvec` : implémentation "non typée" des vecteurs. Le type d'un vecteur ne contient pas sa taille. Cette version sert de base et de motivation pour le reste de la session.
-  `handson.preuve` : implémentation typée des vecteur par une approche orientée preuves.
 * `handson.preuve.endo` : restriction aux opération qui préservent la taille (*zip*, *ap*, *map*).
 * `handson.preuve.prolog` et `handson.preuve.pdt`: opérations de concaténation et de produit cartésien.
- `handson.calcul`: implémentation typée des vecteur par une approche orientée calcul.
- `handson.binaire`: implémentation efficace des entiers naturel positifs au type-level par une représentation binaire.

### Lectures utiles

Suite aux discussions très intéressantes de la session, voici un petit récapitulatif des lectures que je conseille pour aller plus loin:

- **Le [Coq'Art](https://www.labri.fr/perso/casteran/CoqArt/)**: Bien que dédié à l'assistant de preuve [Coq](https://coq.inria.fr/), il couvre les bases de la théorie des types de manière très pédagogique. La lecture est plaisante. Les sujets sont bien amenés et bien illustrés, avec à chaque fois le contexte et la motivation. Avantage non négligeable, les auteurs le rendent accessible en fançais [ici](https://www.labri.fr/perso/casteran/CoqArt/coqartF.pdf). Je conseille tout particulièrement la lecture des chapitres 1 et 4 pour l'introduction de la notion de preuve, mais surtout des chapitres 7, 10, 14 et 15 qui apportent de nombreuses cléfs pour comprendre les `case class`. De manière générale, il est une excellente lecture pour comprendre comment utiliser Scala de manière avancée.
- http://steshaw.org/plt/ : [Steven Shaw](http://steshaw.org/) y dresse une liste de lectures très intéressantes sur la théorie des langages de programmation, classées par thème.

