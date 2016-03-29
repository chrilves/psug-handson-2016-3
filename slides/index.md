<!--
- title : Type-Level Programming en Scala : Trucs et Astuces
- description : Trucs et Astuces pour programmer au type-level en Scala
- author : Christophe Calvès
- theme : league
- transition : convex
- slideNumber : true -->

# Type-level programming in Scala
## Trucs et Astuces

[Christophe Calvès](https://www.linkedin.com/pub/christophe-calv%C3%A8s/b0/325/ab6)/
[@chrilves](http://twitter.com/chrilves)


https://github.com/christophe-calves/psug-handson-2016-3.git

----

## Planning du Hands On

* "Petit" exemple d'introduction: les vecteurs.
 - A quoi ça sert?
 - L'approche orientée **Preuves**.
 - L'approche orientée **Calculs**.
* S'amuser au type-level.

---

# Le Problème

----

## Vecteurs

Des listes de taille fixe.

```scala
type Vec0[A] = Unit
type Vec1[A] = (A)
type Vec2[A] = (A,A)
type Vec3[A] = (A,A,A)
type Vec4[A] = (A,A,A,A)
...
```

----

## Opérations (1/2)

<!--
Foncteur
```scala
val x : Vec[4, Int] = (1,2,3,4)

x.map(x => 10 * x)
res0: Vec[4, String] = ("1", "2", "3", "4")
```
-->

Concaténation
```scala
scala> val x : Vec[4, Int] = (1,2,3,4)
scala> val y : Vec[2, Int] = (5,6)

scala> x ++ y
res0: Vec[6, Int] = (1,2,3,4,5,6)
```

Produit Cartésien
```scala
scala> val x : Vec[4, Int] = (1,2,3,4)
scala> val y : Vec[2, Int] = (5,6)

scala> x ** y
res0: Vec[8, Int] = ((1,5),(1,6),(2,5),(2,6),(3,5),(3,6),(4,5),(4,6))
```

----

## Opérations (2/2)

Zip
```scala
scala> val x : Vec[3, Int ] = ( 1 , 2 , 3 )
scala> val y : Vec[3, Char] = ('a','b','c')

scala> x zip y
res0: Vec[3, (Int,Char)] = ((1,'a'),(2,'b'),(3,'c'))
```

Applicative
```scala
scala> val x : Vec[3, Int       ] = ( 1 , 2 , 3 )
scala> val y : Vec[3, Int => Int] = ( (x => 10   * x),
                                      (x => 100  * x),
                                      (x => 1000 * x)
                                    )

scala> x ap y
res0: Vec[3, Int] = (10, 200, 300)
```

---

## Première Version

Données
```scala
sealed abstract class Vec[+A]
case object VNil                                extends Vec[Nothing]
case class  VCons[+A](head : A , tail : Vec[A]) extends Vec[A]
```

Opérations
```scala
abstract class Vec[+A] {
  def map[B](f : A => B) : Vec[B]

  def zip[B](other : Vec[B]) : Vec[(A,B)]
  def ap[B](f : Vec[A => B]) : Vec[B]

  def ++[B >: A](other : Vec[B]) : Vec[B]
  def **[B](other : Vec[B]) : Vec[(A,B)]
}
```

----

## Presque ...

```scala
scala> val x = 1 |:| (2 |:| (3 |:| VNil))
x: VCons[Int] = VCons(1,VCons(2,VCons(3,VNil)))

scala> val y = 'a' |:| ('b' |:| VNil)
y: VCons[Char] = VCons(a,VCons(b,VNil))

scala> x ++ y
res3: Vec[AnyVal] = VCons(1,VCons(2,VCons(3,
                       VCons(a,VCons(b,
                         VNil)))))

scala> x ** y
res4: Vec[(Int,Char)] = VCons((1,a),VCons((1,b),
                           VCons((2,a),VCons((2,b),
                             VCons((3,a),VCons((3,b),
                               VNil))))))
```

```scala
scala> val y = 'a' |:| ('b' |:| ('c' |:| VNil))
y: VCons[Char] = VCons(a,VCons(b,VCons(c,VNil)))

scala> x zip y
res9: Vec[(Int, Char)] = VCons((1,a),VCons((2,b),VCons((3,c),VNil)))

```

----

## Problème de taille!

```scala
scala> val x = 1 |:| (2 |:| (3 |:| VNil))
x: VCons[Int] = VCons(1,VCons(2,VCons(3,VNil)))

scala> val y = 'a' |:| ('b' |:| VNil)
y: VCons[Char] = VCons(a,VCons(b,VNil))

scala> x zip y
scala.MatchError: VNil (of class handson.uvec.VNil$)
  at handson.uvec.VCons.zip(Vec.scala:26)

scala> val z: Vec[Int=>Int] = ((_:Int) + 1) |:| (((_:Int) * 10) |:| VNil)
y: Vec[Int => Int] = VCons(<function1>,VCons(<function1>,VNil))

scala> x ap z
scala.MatchError: VNil (of class handson.uvec.VNil$)
  at handson.uvec.VCons.ap(Vec.scala:30)
```


---

# Des Entiers au Type-Level

----

## Idée

Indiquer la **taille** du vecteur dans son **type**.

```scala
class Vec[n , +A]
```

Hypothèses
```scala
val x : Vec[n , A]
val y : Vec[m , A]
```

Définition
```scala
VNil         : Vec[ ? , Nothing]
VCons(a , x) : Vec[ ? , A]
```

Opération
```scala
x ++  y : Vec[ ? , A]
x **  y : Vec[ ? , A]
```

----

## Représentation Canonique: Penao

Au pays des valeurs
```scala
sealed abstract class Nat
case object Z          extends Nat // 0
case class  S(n : Nat) extends Nat // n + 1
```

Au pays des types
```scala
sealed abstract class Nat
final abstract class Z           extends Nat // 0
final abstract class S[n <: Nat] extends Nat // n + 1
```

Vecteurs 
```scala
sealed abstract class Vec[n <: Nat, +A]
case object VNil                                            extends Vec[Z   , Nothing]
case class  VCons[n <: Nat, +A](head : A, tail : Vec[n, A]) extends Vec[S[n], A]
```

----

## Map, Zip et Ap

*Map*, *Zip* et *Ap* préservent la taille.

```scala
class Vec[n <: Nat, +A]
  def map[B](f : A => B) : Vec[n , B]

  def zip[B](other : Vec[n , B]) : Vec[n, (A,B)]

  def ap[B](f : Vec[n , A => B]) : Vec[n , B]
}
```

----

## Concaténation et Produit Cartésien

Addition et Multiplication au type-level.

```scala
abstract class Vec[n <: Nat, +A] {
  def ++[m <: Nat, B >: A](other : Vec[m, B]) : Vec[ n + m ,   B  ]
  def **[m <: Nat, B     ](other : Vec[m, B]) : Vec[ n * m , (A,B)]
}
```

---

# Preuves

* Idée:
 - demander un/des *argument(s)* ...
 - qui apportent la *preuve* ...
 - que *hypothèse* dont on souhaite s'assurer est *remplie*.

* Principe:
 - l'*existence* d'une valeur du *type* recherché apporte la preuve
   de la *propriété* désirée.

----

## Exemple

La propriété: les types `A` et `B` sont égaux.
```scala
sealed abstract class Eq[A,B]
```

Recherche de preuve:
```scala
object Eq {
  def apply[A,B](implicit p : Eq[A,B]) = p
  
  implicit def eq[A] = new Eq[A,A] { }
}
```

Safe cast:
```scala
def cast[A,B](a : A)(implicit p : Eq[A,B]) : B = a.asInstanceOf[B]
```

Par *construction*, une valeur de type `Eq[A,B]` prouve que `A = B`.

----

## Retour aux Vecteurs

Une preuve que `n + m = r` :
```scala
sealed abstract class Plus[n <: Nat, m <: Nat, r <: Nat]

object Plus {
  def apply[n <: Nat, m <: Nat, r <: Nat](implicit p : Plus[n,m,r]) = p
}
```

Une preuve que `n * m = r` :
```scala
sealed abstract class Mult[n <: Nat, m <: Nat, r <: Nat]

object Mult { 
  def apply[n <: Nat, m <: Nat, r <: Nat](implicit p : Mult[n,m,r]) = p
}
```

Concaténation et produit:
```scala
abstract class Vec[n <: Nat, +A] {
 def ++[m <: Nat, r <: Nat, B>:A](o : Vec[m, B])(implicit p : Plus[n,m,r]) : Vec[r,  B  ]
 def **[m <: Nat, r <: Nat, B   ](o : Vec[m, B])(implicit p : Mult[n,m,r]) : Vec[r,(A,B)]
}
```

----

## Variante

Mettre le résultat (`r`) en tant que membre de type:
```scala
sealed abstract class Plus[n <: Nat, m <: Nat] { type result <: Nat }

object Plus {
  type Aux[n <: Nat, m <: Nat, r <: Nat] = Plus[n,m] { type result = r }

  def apply[n <: Nat, m <: Nat](implicit p : Plus[n,m]) : Plus.Aux[n,m,p.result] = p
}
```

* Le type `r` n'est plus seulement vérifié, mais calculé.
* Il peut souvent être omis.
 
```scala
scala> val x = Plus[_3,_8] 
x: Plus[S[S[S[Z]]], S[S[S[S[S[S[S[S[Z]]]]]]]]] { 
     type result = S[S[S[S[S[S[S[S[S[S[S[Z]]]]]]]]]]]
   } = Plus$$anon$2@427f2d83

scala> Val[x.result] 
res3: BigInt = 11
```

---

# Calcul

Idée: Calculer l'addition et la multiplication **entièrement** au type-level.

```scala
trait Nat {
  type +[m <: Nat] <: Nat
  type *[m <: Nat] <: Nat
}

trait Z                extends Nat // 0
trait S[n <: Nat]      extends Nat // n + 1
```

----

### Limitations de la représentation

* Uniquement pour de "petits" nombres:

```scala
scala> Val[_200]
res2: Long = 200

scala> Val[_300]
java.lang.StackOverflowError
        at scala.reflect.internal.util.WeakHashSet.linkedListLoop$4(WeakHashSet.scala:189)
        at scala.reflect.internal.util.WeakHashSet.findEntryOrUpdate(WeakHashSet.scala:194)
```

* Lent!

---

# Coder Efficacement au Type-Level

----

## Entiers Positifs en binaire


* Représentation
$$ℕ^+ = 1 \mid 2 \times ℕ^+ \mid 2 \times ℕ^+ + 1$$

* Exemple
$$1010b = 2(2(2 \times 1 + 0) + 1) + 0$$

```scala
sealed abstract class Pos
case       object Un             extends Pos
final case class  Db0(n : Pos)   extends Pos
final case class  Db1(n : Pos)   extends Pos

val _10 = Db0(Db1(Db0(Un)))
```

----

## Opérations

```scala
sealed abstract class Pos {
  def inc : Pos
  def +(p : Pos) : Pos
  def *(p : Pos) : Pos

  def toBigInt : BigInt
}

case       object Un             extends Pos
final case class  Db0(n : Pos)   extends Pos
final case class  Db1(n : Pos)   extends Pos
```

----

## Hard Mode

On s'interdit les constructions qui n'existent pas au Type-Level:
- Le pattern-matching natif / .isInstanceOf[...]
- Les casts / .asInstanceOf[...]
- Certaines fraudes logiques: null, exceptions, ... 

----

## Imprédicativité mon amour

En se limitant aux constructions bien fondées,

```scala
sealed abstract class Pos
case       object Un             extends Pos
final case class  Db0(n : Pos)   extends Pos
final case class  Db1(n : Pos)   extends Pos
```

est "équivalent" à

```scala
trait Pos {
  def Match[R](un : R, db0 : Pos => R, db1 : Pos => R) : R

  def fold[R](un : R, db0 : R => R, db1 : R => R) : R
}
```

---

# Au Type-Level

----

## Correspondance Value <-> Type level

Valeurs

```scala
class A { val y : B }
x : A
x.y
def f(x : A) : B = ...
```

Types

```scala
class A { type y <: B }
x <: A
x#y
type f[x <: A] = ...
trait f[x <: A] extends B { type result = ... }
```

----

### Quelques limitations du typeur

Dès qu'il le peut, il réduit!

```scala
trait Π {
  type λ[x]
}

trait R[f <: Π] extends Π {
  type λ[x] = f#λ[x]
}

trait Ω extends R[Ω]
```

```scala
scala> type f[x] = Ω#λ[x]
java.lang.StackOverflowError
        at scala.reflect.internal.Mirrors$Roots$RootClass.typeOfThis(Mirrors.scala:306)
        at scala.reflect.internal.Types$ThisType.underlying(Types.scala:1172)
        at scala.reflect.internal.Types$SimpleTypeProxy$class.boundSyms(Types.scala:153)
        at scala.reflect.internal.Types$SingletonType.boundSyms(Types.scala:1076)
```

----

### Propagation non optimale des contraintes de types.

* Un légère tandance à [oublier des contraintes de type](https://issues.scala-lang.org/browse/SI-4043?jql=status%20%3D%20Open%20AND%20text%20~%20%22bound%22)
* La substitution n'est pas toujours correcte

```scala
type id[x <: Nat] = x
type f[n <: Nat] = n#chain[Nat, id]
type g[n <: Nat] = n#chain[Nat, id]#inc
```

```scala
scala> Val[f[Z]]
res1: typefun.valuelevel.Val.zero.value = 0

scala> Val[f[Z]#inc]
res2: Long = 1

scala> Val[g[Z]]
<console>:19: error: could not find implicit value for parameter v: typefun.valuelevel.Val[g[typefun.Z]]
```

----

### Astuce N°1

* Éviter à tout prix l'η-expansion
* normalisation à la définition et l'utilisation **directe**
* mais non propagée aux membres de type

```scala
trait LoopSig {
  type loop
}

trait LoopRec[l <: LoopSig] extends LoopSig {
  final type loop = l#loop
}

trait  Loop extends LoopRec[Loop]
```

```scala
scala> List.empty[Loop#loop]
java.lang.StackOverflowError
        at scala.reflect.internal.tpe.TypeMaps$class.isPossiblePrefix(TypeMaps.scala:446)
        at scala.reflect.internal.SymbolTable.isPossiblePrefix(SymbolTable.scala:16)
```

----

### Astuce N°2 : Raffinement de types

```scala
trait AppJ[A, B, f <: Π[A,Thunk[B]] , a <: A] extends Thunk[B] {
  final type eval = f # λ[a] # eval
}
```

```scala
<console>:17: error: overriding type eval in trait Thunk with bounds <: B;
 type eval has incompatible type
         final type eval = f # λ[a] # eval
                    ^
Nothing <: AppJ.this.eval?
true
AppJ.this.eval <: B?
false
```
Abusez des raffinements de types

```scala
trait AppJ[A, B, f <: Π[A,Thunk[B]] { type λ[α <: A] <: Thunk[B] } , a <: A] extends Thunk[B] {
  final type eval = f # λ[a] # eval
}
```

---

### Entiers positifs en binaire

```scala
trait Pos {
  type This <: Pos

  type Match[R, un <: R, db0[_ <: Pos] <: R, db1[_ <: Pos] <: R] <: R

  type inc <: Pos
  type +[p <: Pos] <: Pos
  type *[p <: Pos] <: Pos
}
```

```
trait Un extends Pos {
  type This = Un
  final type Match[R, un <: R, db0[_ <: Pos] <: R, db1[_ <: Pos] <: R] = un
}

trait Db0[n <: Pos] extends Pos {
  final type This = Db0[n]
  final type Match[R, un <: R, db0[_ <: Pos] <: R, db1[_ <: Pos] <: R] = db0[n]
}

trait Db1[n <: Pos] extends Pos {
  final type This = Db1[n]
  final type Match[R, un <: R, db0[_ <: Pos] <: R, db1[_ <: Pos] <: R] = db1[n]
}
```

----

### Successeur

* Signature

```scala
tait Pos {
  type inc <: Pos
}
```

* Définition

```scala
trait Un extends Pos {
  final type inc = Db0[Un]    // (1) + 1 = 2 * 1
}

trait Db0[n <: Pos] extends Pos {
  final type inc = Db1[n]     // (2 * n) + 1 = 2 * 1 + 1
}

trait Db1[n <: Pos] extends Pos {
  final type inc = Db0[n#inc] // (2 * n + 1) + 1 = 2 * (n + 1)
}
```

----

## Astuce N°3

* Distinguer les **valeurs** des **calculs**
* *But*: forcer une stratégie d'appel par valeur
* *Règle*: ne jamais déguiser un calcul en valeur

----

### Addition

* Signature

```scala
tait Pos {
  type +[m <: Pos] <: Pos
}
```

* Définition

```scala
trait Un extends Pos {
	final type +[m <: Pos] = m # inc
}

trait Db0[n <: Pos] extends Pos {
  final type +[m <: Pos] = m # Match[Pos, Db1[n] , fun1 , fun2 ]

  final type fun1[m2 <: Pos] = Db0[m2 # +[n]] // (2 * n) + (2 * m2     ) = 2 * (m2 + n)
  final type fun2[m2 <: Pos] = Db1[m2 # +[n]] // (2 * n) + (2 * m2 + 1 ) = 2 * (m2 + n) + 1
}
```

----

### Problème

* Calcul déguisé en valeur

```scala
final type fun1[m2 <: Pos] = Db0[m2 # +[n]]
```

* Duplication des calculs

```scala
type double[x <: Nat] = x # + [x]

double[Db0[m2 # +[n]]] = Db0[m2 # +[n]] # + [ Db0[m2 # +[n]] ] // duplication des calculs.
```

---

### Astuce N°4 : Chaînge

* *Idée*: Ne passer en argument que
 - des valeurs
 - des variables
* *Objectif*:
 - Chaîner le flot des calculs pour
 - Former un flot de transformation de valeurs (et non de calculs).
* *Technique*: Bloquer la réduction avec des types abstraits.

----

### Principe

```scala
tait Pos {
	type chain[R, F[_ <: Pos] <: R] <: R
}
```

* Définition

```scala
trait Un extends Pos {
	final type chain[R, F[_ <: Pos] <: R] = F[Un]
}

trait Db0[n <: Pos] extends Pos {
	final type chain[R, F[_ <: Pos] <: R] = F[Db0[n]]
}

trait Db1[n <: Pos] extends Pos {
	final type chain[R, F[_ <: Pos] <: R] = F[Db1[n]]
}
```

----

### En Pratique

```scala
trait Db0[n <: Pos] extends Pos {
  final type +[m <: Pos] = m # Match[Pos, Db1[n] , fun1 , fun2 ]

  final type fun1[m2 <: Pos] = m2 # +[n] # chain[Pos, Db0] // Db0[m2 + n]
  final type fun2[m2 <: Pos] = m2 # +[n] # chain[Pos, Db1] // Db1[m2 + n]

}

trait Db1[n <: Pos] extends Pos {
  final type +[m <: Pos] = m # Match[ Pos, exp1 , fun1, fun2 ]

  final type exp1 = Db0[n # inc]
  final type fun1[m2 <: Pos] = m2 # +[n] # chain[Pos, Db1]       // Db1[m2 + n]
  final type fun2[m2 <: Pos] = m2 # +[n] # inc # chain[Pos, Db0] // Db0[(m2 + n) # inc]
}
```

---

### Multiplication

* Signature

```scala
tait Pos {
  type *[m <: Pos] <: Pos
}
```

* Définition

```scala
trait Un extends Pos {
	final type *[m <: Pos] = m // m * 1 = m
}

trait Db0[n <: Pos] extends Pos {
  final type *[m <: Pos] = m # Match[Pos , This , fun3 , fun4 ]

  final type fun3[m2 <: Pos] =
	  m2 # *[n] # chain[Pos, Db0] # chain[Pos, Db0]           // 2n*2n = 4(n * m)

  final type fun4[m2 <: Pos] =
	  m2 # *[n] # chain[Pos, Db0] # + [n] # chain[Pos, Db0] // 2n*(2m+1) = 2(2*m*n + n)
}
```

----

### Petite Mémoire

Malheureusement:
```scala
scala> Val[_2#*[_2]]
<console>:32: error: could not find implicit value for parameter ...
```

*Problème*: dans une longue chaîne comme

```scala
m2 # *[n] # chain[Pos, Db0] # + [n] # chain[Pos, Db0]
```

Le typeur déduit:
```scala
m2 # *[n] # chain[Pos, Db0] <: Pos
```

----

### Astuce N°5 : β-expansion

Utiliser autant que possible la transformation de β-expansion:

```scala
exp1 # exp2 => ({ type f[x <: T] = x # exp2})#f[exp1]
```


----

### Mise en pratique

```scala
trait Db0[n <: Pos] extends Pos {
  final type *[m <: Pos] = m # Match[Pos , This , fun3 , fun4 ]

  final type fun3[m2 <: Pos] =
	   ({type f[x <: Pos] = Db0[Db0[x]]})
		   #f[m2 # *[n]]

  final type fun4[m2 <: Pos] =
	  ({type f[x <: Pos] = x # + [n] # chain[Pos, Db0]})
		  #f[m2 # *[n] # chain[Pos, Db0]]
}
```

----

### Résultats

```scala
type _1 = Un
type _2 = Db0[Un]
type _3 = Db1[Un]
type _4 = Db0[Db0[Un]]
type _5 = Db1[Db0[Un]]
type _6 = Db0[Db1[Un]]
...
```

```scala
Val[ _99 # *[ _98] # *[ _57] # +[ _12 # *[_42] # *[_88] # *[_39] ] ]
res1: Long = 2282742

scala> (99  * 98 * 57) + (12 * 42 * 88 * 39)
res6: Int = 2282742
```

---

### Conclusion

Programmer au Type-Level en Scala c'est
* Possible
* Efficace
* Fun!!

https://github.com/christophe-calves/typefun
