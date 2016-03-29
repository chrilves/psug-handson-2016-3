package handson.preuve.pdt

import handson.preuve._

/** Une preuve que a * b = result */
sealed abstract class Plus[a <: Nat, b <: Nat] {
  type result <: Nat // Le resulat de a * b
}

/** Construction/Recherche des termes de preuve */
object Plus {
  def apply[a <: Nat, b <: Nat](implicit p : Plus[a,b]) : Plus.Aux[a,b,p.result] = p

  /** Le type qui explicite le resultat */
  type Aux[a <: Nat, b <: Nat, c <: Nat] = Plus[a,b] { type result = c }
}

/** Une preuve que a * b = result */
sealed abstract class Mult[a <: Nat, b <: Nat] {
  type result <: Nat // Le resultat de a * b
}

/** Construction/Recherche des termes de preuve */
object Mult {
  def apply[a <: Nat, b <: Nat](implicit p : Mult[a,b]) : Mult.Aux[a,b,p.result] = p

  /** Le type qui explicite le resultat */
  type Aux[a <: Nat, b <: Nat, c <: Nat] = Mult[a,b] { type result = c }

}

/** Vecteurs de taille `n` */
sealed abstract class Vec[n <: Nat, +A] extends Product with Serializable

/** Vecteurs de taille 0 */
case object VNil extends Vec[Z, Nothing]

/** Vecteurs de taille `n + 1` */
final case class VCons[n <: Nat, +A](head : A , tail : Vec[n, A]) extends Vec[S[n], A]

object Vec {
  def pure[n <: Nat,A](a : A)(implicit vb : VecBuilder[n]) : Vec[n, A] = vb(a)
}