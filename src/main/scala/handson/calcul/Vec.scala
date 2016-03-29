package handson.calcul

/** Les entiers au type level */
sealed abstract class Nat {
  type +[m <: Nat] <: Nat // Methode : Nat => Nat au Type-Level
  type *[m <: Nat] <: Nat // Methode : Nat => Nat au Type-Level
}

/** Type de 0 */
abstract class Z           extends Nat

/** Type de n + 1 */
abstract class S[n <: Nat] extends Nat

/** Vecteurs de taille n */
sealed abstract class Vec[n <: Nat, +A] extends Product with Serializable

/** Vecteurs de taille 0 */
case object VNil extends Vec[Z, Nothing]

/** Vecteurs de taille n + 1 */
final case class VCons[n <: Nat, +A](head : A , tail : Vec[n, A]) extends Vec[S[n], A]

object Vec {
  /** Constuit un vecteur de taille `n` dont tous les élements sont `a` */
  def pure[n <: Nat,A](a : A)(implicit vb : VecBuilder[n]) : Vec[n, A] = vb(a)
}