package handson.calcul

/** Les entiers au type level */
sealed abstract class Nat {
  type +[m <: Nat] <: Nat // Methode : Nat => Nat au Type-Level
  type *[m <: Nat] <: Nat // Methode : Nat => Nat au Type-Level
}

/** Type de 0 */
final abstract class Z           extends Nat {
  type +[m <: Nat] = m // 0 + m = m
  type *[m <: Nat] = Z // 0 * m = m
}

/** Type de n + 1 */
final abstract class S[n <: Nat] extends Nat {
  type +[m <: Nat] = S[n # + [m]]       // (n + 1) + m = (n + m) + 1
  type *[m <: Nat] = m # + [n # * [m]]  // (n + 1) * m = (n * m) + m
}

/** Vecteurs de taille n */
sealed abstract class Vec[n <: Nat, +A] extends Product with Serializable {
  def map[B](f : A => B) : Vec[n , B]

  def zip[B](other : Vec[n , B]) : Vec[n, (A,B)]
  def ap[B](f : Vec[n , A => B]) : Vec[n , B]

  def ++[B >: A, m <: Nat](other : Vec[m, B]) : Vec[n # + [m], B]
  def **[B, m <: Nat](other : Vec[m, B]) : Vec[n # * [m], (A,B)]
}

/** Vecteurs de taille 0 */
case object VNil extends Vec[Z, Nothing] {
  def map[B](f : Nothing => B) : Vec[Z , B] = VNil

  def zip[B](other : Vec[Z , B]) : Vec[Z, (Nothing,B)] = VNil
  def ap[B](f : Vec[Z , Nothing => B]) : Vec[Z , B] = VNil

  def ++[B, m <: Nat](other : Vec[m, B]) : Vec[m, B] = other
  def **[B, m <: Nat](other : Vec[m, B]) : Vec[Z, (Nothing,B)] = VNil
}

/** Vecteurs de taille n + 1 */
final case class VCons[n <: Nat, +A](head : A , tail : Vec[n, A]) extends Vec[S[n], A] {
  def map[B](f : A => B) : VCons[n , B] = VCons[n, B](f(head), tail.map(f))

  def zip[B](other : Vec[S[n] , B]) : VCons[n, (A,B)] = other match {
    case v : VCons[_, B] => VCons((head, v.head), tail.zip(v.tail.asInstanceOf[Vec[n, B]]))
  }

  def ap[B](f : Vec[S[n] , A => B]) : VCons[n , B] = f match {
    case v : VCons[_ , A => B] => VCons[n, B](v.head(head), tail.ap(v.tail.asInstanceOf[Vec[n , A => B]]))
  }

  def ++[B >: A, m <: Nat](other : Vec[m, B]) : Vec[S[n] # + [m], B] = VCons(head , tail.++(other))

  def **[B, m <: Nat](other : Vec[m, B]) : Vec[S[n] # * [m], (A,B)] = other.map((head, _)) ++ ( tail  ** other )
}



object Vec {
  /** Constuit un vecteur de taille `n` dont tous les élements sont `a` */
  def pure[n <: Nat,A](a : A)(implicit vb : VecBuilder[n]) : Vec[n, A] = vb(a)
}