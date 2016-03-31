package handson.binaire.value

sealed abstract class Pos extends Product with Serializable {
  
  // Patern matching sur Pos (Pos est une co-algèbre ;) )
  def Match[R](un : R, db0 : Pos => R, db1 : Pos => R) : R

  // Pos est une algebre initiale ;)
  def fold[R](un : R, db0 : R => R, db1 : R => R) : R

  // this + 1
  def inc : Pos

  // this + p
  def +(p : Pos) : Pos

  // this * p
  def *(p : Pos) : Pos
  
  // Conversion en BigInt
  final def toBigInt : BigInt = {
    
    val un : BigInt = BigInt(1)
    
    def db0(p : Pos) : BigInt = p.toBigInt * 2
    def db1(p : Pos) : BigInt = p.toBigInt * 2 + 1
    
    Match[BigInt](un, db0, db1)
  }

}

/** Le nombre 1 */
case object Un                   extends Pos {

  def Match[R](un : R, db0 : Pos => R, db1 : Pos => R) : R = un

  def fold[R](un : R, db0 : R => R, db1 : R => R) : R = un

  // 1 + 1 = 2 * 1
  def inc : Pos = Db0(Un)

  // 1 + p = p + 1
  def +(p : Pos) : Pos = p.inc

  // 1 * p = p
  def *(p : Pos) : Pos = p
}

/** Les nombres pairs (le double de `n`) */
final case class Db0(n : Pos)   extends Pos {

  def Match[R](un : R, db0 : Pos => R, db1 : Pos => R)  : R = db0(n)

  def fold[R](un : R, db0 : R => R, db1 : R => R) : R = db0(n.fold(un, db0, db1))

  // (2 * n + 0) + 1 = 2 * n + 1
  def inc : Pos = Db1(n)

  /* (2 * n + 0) + (     1   ) = 2 * n + 1
   * (2 * n + 0) + (2 * m + 0) = 2 * (n + m) + 0
   * (2 * n + 0) + (2 * m + 1) = 2 * (n + m) + 1
   */
  def +(p : Pos) : Pos =
    p.Match[Pos]( Db1(n),                   // p = 1
                 (m : Pos) => Db0(n + m),   // p = 2 * m + 0
                 (m : Pos) => Db1(n + m)    // p = 2 * m + 1
                )

  /* (2 * n + 0) * (     1   ) = 2 * n + 1
   * (2 * n + 0) * (2 * m + 0) = 4 * (n * m) = 2 * (2 * (n * m) + 0) + 0
   * (2 * n + 0) * (2 * m + 1) = 4 * (n * m) + (2 * n + 0)
   */
  def *(p : Pos) : Pos =
    p.Match[Pos]( this,                               // p = 1
                 (m : Pos) => Db0(Db0(n * m)),        // p = 2 * m + 0
                 (m : Pos) => Db0(Db0(n * m)) + this  // p = 2 * m + 1
                )

}

/** Les nombres impairs strictement plus grands que 1 (le double de `n` + 1) */
final case class Db1(n : Pos) extends Pos {

  def Match[R](un : R, db0 : Pos => R, db1 : Pos => R) = db1(n)

  def fold[R](un : R, db0 : R => R, db1 : R => R) : R = db1(n.fold(un, db0, db1))

  // (2 * n + 1) + 1 = 2 * (n + 1) + 0
  def inc : Pos = Db0(n.inc)

  /* (2 * n + 1) + (     1   ) = 2 * (n + 1) + 0
   * (2 * n + 1) + (2 * m + 0) = 2 * (n + m) + 1
   * (2 * n + 1) + (2 * m + 1) = 2 * (n + m + 1) + 0
   */
  def +(p : Pos) : Pos =
    p.Match[Pos]( Db0(n.inc),                   // p = 1
                 (m : Pos) => Db1(n + m),       // p = 2 * m + 0
                 (m : Pos) => Db0((n + m).inc)  // p = 2 * m + 1
                )

  /* (2 * n + 1) * (     1   ) = 2 * n + 1
   * (2 * n + 1) * (2 * m + 0) = 4 * (n * m) + (2 * m + 0)
   * (2 * n + 1) * (2 * m + 1) = 4 * (n * m) + (2 * (n + m) + 1)
   */
  def *(p : Pos) : Pos =
    p.Match[Pos]( this,                                     // p = 1
                 (m : Pos) => Db0(Db0(n * m)) + p,          // p = 2 * m + 0
                 (m : Pos) => Db0(Db0(n * m)) + Db1(n + m)  // p = 2 * m + 1
                )

}