package handson.binaire.tpe

/** Le type des nombres positifs en "binaire" */
trait Pos {
  /** This + 1 */
  type inc <: Pos

  /** This + m */
  type +[p <: Pos] <: Pos

  /** This * 1 */
  type *[p <: Pos] <: Pos
}

/** Le nombre 1 en "binaire" */
trait Un extends Pos

/** Les nombres pairs (le double de `n`) */
trait Db0[n <: Pos] extends Pos

/** Les nombres impairs strictement plus grands que 1 (le double de `n` + 1) */
trait Db1[n <: Pos] extends Pos






