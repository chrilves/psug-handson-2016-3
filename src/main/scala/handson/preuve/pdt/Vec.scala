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

  /** 0 + a = 0 */
  implicit final def plus_z[a <: Nat] : Plus.Aux[Z,a,a] = new Plus[Z, a] {
    type result = a
  }

  /** (a + 1) + b = a + (b + 1) */
  implicit final def plus_s[a <: Nat, b <: Nat](implicit p : Plus[a, S[b]]) : Plus.Aux[S[a], b, p.result] = new Plus[S[a], b] {
    type result = p.result
  }
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

  /** 0 * a = 0 */
  implicit final def mult_z[a <: Nat] : Mult.Aux[Z,a,Z] = new Mult[Z, a] {
    type result = Z
  }

  /** (a + 1) * b = (a * b) + b */
  implicit final def mult_s[a  <: Nat,
                            b  <: Nat,
                            ab <: Nat
                           ](implicit ab : Mult.Aux[a , b, ab],
                                      c  : Plus[ab, b]
                            ) : Mult.Aux[S[a], b, c.result] =
    new Mult[S[a], b] {
      type result = c.result
    }
}

/** Vecteurs de taille `n` */
sealed abstract class Vec[n <: Nat, +A] extends Product with Serializable {
  def map[B](f : A => B) : Vec[n , B]

  def ++[B >: A, m <: Nat](other : Vec[m, B])(implicit p : Plus[n,m]) : Vec[p.result, B]
  def **[B, m <: Nat](other : Vec[m, B])(implicit p : Mult[n,m]) : Vec[p.result, (A,B)]
}

/** Vecteurs de taille 0 */
case object VNil extends Vec[Z, Nothing] {
  def map[B](f : Nothing => B) : Vec[Z , B] = VNil

  /** La concaténation demande une preuve que n + m = p.result */
  def ++[B, m <: Nat](other : Vec[m, B])(implicit p : Plus[Z,m]) : Vec[p.result, B] = other.asInstanceOf[Vec[p.result,B]]

  /** Le produit cartésient demande une preuve que n * m = p.result */
  def **[B, m <: Nat](other : Vec[m, B])(implicit p : Mult[Z,m]) : Vec[p.result, (Nothing,B)] = VNil.asInstanceOf[Vec[p.result,(Nothing, B)]]
}

/** Vecteurs de taille `n + 1` */
final case class VCons[n <: Nat, +A](head : A , tail : Vec[n, A]) extends Vec[S[n], A] {
  def map[B](f : A => B) : VCons[n , B] = VCons[n, B](f(head), tail.map(f))

  def ++[B >: A, m <: Nat](other : Vec[m, B])(implicit p : Plus[S[n],m]) : Vec[p.result, B] =
    VCons(head , tail.++(other)(null.asInstanceOf[Plus[n,m]])).asInstanceOf[Vec[p.result, B]]

  def **[B, m <: Nat](other : Vec[m, B])(implicit p : Mult[S[n],m]) : Vec[p.result, (A,B)] = {
    val axiom1: Mult[n, m] = null.asInstanceOf[Mult[n, m]]

    other.map((head, _)).++(tail.**(other)(axiom1))(null.asInstanceOf[Plus[m, axiom1.result]]).asInstanceOf[Vec[p.result, (A, B)]]
  }
}

object Vec {
  def pure[n <: Nat,A](a : A)(implicit vb : VecBuilder[n]) : Vec[n, A] = vb(a)
}