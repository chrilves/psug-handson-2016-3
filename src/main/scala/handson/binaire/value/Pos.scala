package handson.binaire.value

sealed abstract class Pos extends Product with Serializable {
  // this + 1
  def inc : Pos

  // this + p
  def +(p : Pos) : Pos

  // this * p
  def *(p : Pos) : Pos
  
  // Conversion en BigInt
  final def toBigInt : BigInt = ???
}

/** Le nombre 1 */
case object Un                   extends Pos {
  def inc : Pos = ???

  def +(p : Pos) : Pos = ???

  def *(p : Pos) : Pos = ???
}

/** Les nombres pairs (le double de `n`) */
final case class Db0(n : Pos)   extends Pos {
  def inc : Pos = ???

  def +(p : Pos) : Pos = ???

  def *(p : Pos) : Pos = ???
}

/** Les nombres impairs strictement plus grands que 1 (le double de `n` + 1) */
final case class Db1(n : Pos) extends Pos {
  def inc : Pos = ???

  def +(p : Pos) : Pos = ???

  def *(p : Pos) : Pos = ???
}