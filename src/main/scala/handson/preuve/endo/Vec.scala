package handson.preuve.endo

import handson.preuve._

/** Vecteurs de taille `n` */
sealed abstract class Vec[n <: Nat, +A] extends Product with Serializable {
  /** Applique la fonction `f` à tous les élements du vecteur.
    * Retourne le vecteur des résultats.
    *
    * (x, y, z).map(f)
    * =
    * (f(x), f(y), f(z)).map(f)
    *
    */

  // def map[B](f : A => B) : Vec[ _ , B]

  /** Transforme une paire de vecteurs en un vecteur de paires.
    *
    * (x,y,z) zip (a,b,c) = ( (x,a), (y,b), (z,c) )
    *
    */

  // def zip[B](other : Vec[n , B]) : Vec[ _ , (A,B)]

  /** Applique le vecteur de fonctions f, composante par composante.
    * Retourne le vecteur des resultats
    *
    * (x,y,z) ap (f,g,h) = (f(x), g(y), h(z))
    *
    */

  // def ap[B](f : Vec[_ , A => B]) : Vec[ _  , B]
}

/** Vecteur de taille 0 */
case object VNil extends Vec[Z, Nothing]

/** Vecteur de taille n + 1 */
final case class VCons[n <: Nat, +A](head : A , tail : Vec[n, A]) extends Vec[S[n], A]

object Vec {
  /** Construit un vecteur de taille `n` dont chaque élement est `a` */
  def pure[n <: Nat,A](a : A)(implicit vb : VecBuilder[n]) : Vec[n, A] = vb(a)
}