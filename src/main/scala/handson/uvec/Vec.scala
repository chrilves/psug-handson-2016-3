package handson.uvec

/** Type des vecteurs */
sealed abstract class Vec[+A] extends Product with Serializable {
  /** Applique la fonction `f` à tous les élements du vecteur.
    * Retourne le vecteur des résultats.
    *
    * (x, y, z).map(f)
    * =
    * (f(x), f(y), f(z)).map(f)
    *
    */
  def map[B](f : A => B) : Vec[B]

  /** Transforme une paire de vecteurs en un vecteur de paires.
    *
    * (x,y,z) zip (a,b,c) = ( (x,a), (y,b), (z,c) )
    *
    */
  def zip[B](other : Vec[B]) : Vec[(A,B)]

  /** Applique le vecteur de fonctions f, composante par composante.
    * Retourne le vecteur des resultats
    *
    * (x,y,z) ap (f,g,h) = (f(x), g(y), h(z))
    *
    */
  def ap[B](f : Vec[A => B]) : Vec[B]

  /** Concatène deux vecteurs.
    *
    * (x,y,z) ++ (a,b) = (x,y,z,a,b)
    */
  def ++[B >: A](other : Vec[B]) : Vec[B]

  /** Le produit cartésien de deux vecteurs.
    *
    * (x,y,z) ** (a,b) = ( (x,a), (x,b) , (y,a), (y,b) , (z,a), (z,b) )
    *
    */
  def **[B](other : Vec[B]) : Vec[(A,B)]
}

/** Vecteur de taille 0 */
case object VNil extends Vec[Nothing] {
  def map[B](f : Nothing => B) : Vec[B] = VNil

  def zip[B](other : Vec[B]) : Vec[(Nothing,B)] = VNil
  def ap[B](f : Vec[Nothing => B]) : Vec[B] = VNil

  def ++[B](other : Vec[B]) : Vec[B] = other
  def **[B](other : Vec[B]) : Vec[(Nothing,B)] = VNil
}

/** Vecteur de taille "taille de tail" + 1 */
final case class VCons[+A](head : A, tail : Vec[A]) extends Vec[A] {
  def map[B](f : A => B) : Vec[B] = VCons(f(head), tail.map(f))

  def zip[B](other : Vec[B]) : Vec[(A,B)] = other match {
    case VCons(ohead, otail) => VCons((head, ohead), tail.zip(otail))
  }

  def ap[B](f : Vec[A => B]) : Vec[B] = f match {
    case VCons(fhead, ftail) => VCons(fhead(head), tail.ap(ftail))
  }

  def ++[B >: A](other : Vec[B]) : Vec[B] = VCons(head , tail ++ other)
  def **[B](other : Vec[B]) : Vec[(A,B)] = other.map((head, _)) ++ tail ** other
}

object Vec {
  /** Retourne le vecteur de taille `n` dont tous les élements sont `a` */
  def pure[A](n : Long)(a : A) : Vec[A] = if (n <= 0) VNil else VCons(a , pure(n - 1)(a))

  /** Retourne le vecteur de taile `n`: (a, f(a), f(f(a)), ...) */
  def ind[A](n : Long)(a : A)(f : A => A) : Vec[A] = {

    def aux(k : Long, a2 : A) : Vec[A] =
      if (k <= 0) VNil
      else VCons(a2 , aux(k - 1, f(a2)))

    aux(n, a)
  }
}
