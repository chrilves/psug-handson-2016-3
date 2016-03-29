package handson.preuve.prolog

import handson.preuve._

/** La propriété: a + b = c */
sealed abstract class Plus[a <: Nat, b <: Nat, c <: Nat]

/** Recherche/Construction des preuves de : a + b = c */
object Plus {
  def apply[a <: Nat, b <: Nat, c <: Nat](implicit p : Plus[a,b,c]) = p

  /** 0 + a = a */
  implicit final def plus_z[a <: Nat] = new Plus[Z, a, a] { }

  /** (a + 1) + b = c  SI a + (b + 1) = c */
  implicit final def plus_s[a <: Nat, b <: Nat, c <: Nat](implicit p : Plus[a, S[b] , c]) = new Plus[S[a], b, c] { }
}

/** La propriété: a * b = c */
sealed abstract class Mult[a <: Nat, b <: Nat, c <: Nat]


/** Recherche/Construction des preuves de : a + b = c */
object Mult {
  def apply[a <: Nat, b <: Nat, c <: Nat](implicit p : Mult[a,b,c]) = p

  /** 0 * a = 0 */
  implicit final def mult_z[a <: Nat] = new Mult[Z, a, Z] { }

  /** (a + 1) * b = c  SI IL EXISTE UN TYPE ab TEL QUE   a + b = ab ET ab + b = c */
  implicit final def mult_s[a  <: Nat,
                            b  <: Nat,
                            c  <: Nat,
                            ab <: Nat
                           ](implicit ab : Mult[a , b , ab],
                                      c  : Plus[ab, b,  c ]
                            ) = new Mult[S[a], b, c] { }
}


/** Vecteurs de taille `n` */
sealed abstract class Vec[n <: Nat, +A] extends Product with Serializable {
  def map[B](f : A => B) : Vec[n , B]

  /** La concaténation demande une preuve que n + m = p */
  def ++[B >: A, m <: Nat, p <: Nat](other : Vec[m, B])(implicit preuve_n_plus_m_egal_p : Plus[n,m,p]) : Vec[p, B]

  /** Le produit cartésient demande une preuve que n * m = p */
  def **[B, m <: Nat, p <: Nat](other : Vec[m, B])(implicit preuve_n_mult_m_egal_p : Mult[n,m,p]) : Vec[p, (A,B)]
}

/** Le vecteur de taille 0 */
case object VNil extends Vec[Z, Nothing] {
  def map[B](f : Nothing => B) : Vec[Z , B] = VNil

  def ++[B, m <: Nat, p <: Nat](other : Vec[m, B])(implicit preuve_n_plus_m_egal_p : Plus[Z,m,p]) : Vec[p, B] = other.asInstanceOf[Vec[p,B]]
  def **[B, m <: Nat, p <: Nat](other : Vec[m, B])(implicit preuve_n_mult_m_egal_p : Mult[Z,m,p]) : Vec[p, (Nothing,B)] = VNil.asInstanceOf[Vec[p,(Nothing, B)]]
}

/** Le vecteur de taille n + 1 */
final case class VCons[n <: Nat, +A](head : A , tail : Vec[n, A]) extends Vec[S[n], A] {
  def map[B](f : A => B) : VCons[n , B] = VCons[n, B](f(head), tail.map(f))

  def ++[B >: A, m <: Nat, p <: Nat](other : Vec[m, B])(implicit preuve_n_plus_m_egal_p : Plus[S[n],m,p]) : Vec[p, B] =
    VCons[Nat, B](head , tail.++(other)(null.asInstanceOf[Plus[n,m,Nat]])).asInstanceOf[Vec[p, B]]

  def **[B, m <: Nat, p <: Nat](other : Vec[m, B])(implicit preuve_n_plus_m_egal_p : Mult[S[n],m,p]) : Vec[p, (A,B)] =
    other.map((head, _)).++(tail.**(other)(null.asInstanceOf[Mult[n,m,Nat]]))(null.asInstanceOf[Plus[m,Nat,Nat]]).asInstanceOf[Vec[p,(A,B)]]
}

object Vec {
  def pure[n <: Nat,A](a : A)(implicit vb : VecBuilder[n]) : Vec[n, A] = vb(a)
}