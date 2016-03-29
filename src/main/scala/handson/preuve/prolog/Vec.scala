package handson.preuve.prolog

import handson.preuve._

/** La propriété: a + b = c */
sealed abstract class Plus[a <: Nat, b <: Nat, c <: Nat]

/** Recherche/Construction des preuves de : a + b = c */
object Plus {
  def apply[a <: Nat, b <: Nat, c <: Nat](implicit p : Plus[a,b,c]) = p
}

/** La propriété: a * b = c */
sealed abstract class Mult[a <: Nat, b <: Nat, c <: Nat]


/** Recherche/Construction des preuves de : a + b = c */
object Mult {
  def apply[a <: Nat, b <: Nat, c <: Nat](implicit p : Mult[a,b,c]) = p
}


/** Vecteurs de taille `n` */
sealed abstract class Vec[n <: Nat, +A] extends Product with Serializable

/** Le vecteur de taille 0 */
case object VNil extends Vec[Z, Nothing]

/** Le vecteur de taille n + 1 */
final case class VCons[n <: Nat, +A](head : A , tail : Vec[n, A]) extends Vec[S[n], A]

object Vec {
  def pure[n <: Nat,A](a : A)(implicit vb : VecBuilder[n]) : Vec[n, A] = vb(a)
}