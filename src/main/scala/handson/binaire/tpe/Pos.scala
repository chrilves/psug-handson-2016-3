package handson.binaire.tpe

/** Le type des nombres positifs en "binaire" */
trait Pos {
  /** Le nombre */
  type This <: Pos

  /** Pattern Matching sur la structure du nombre (Pos est une co-algèbre ;) )*/
  type Match[R, un <: R, db0[_ <: Pos] <: R, db1[_ <: Pos] <: R] <: R
  
  /** Pos est une algèbre */
  type fold[R, un <: R, db0[_ <: R] <: R, db1[_ <: R] <: R] <: R

  /** Petite astuce */
  type chain[R, F[_ <: Pos] <: R] <: R

  /** This + 1 */
  type inc <: Pos

  /** This + m */
  type +[p <: Pos] <: Pos

  /** This * 1 */
  type *[p <: Pos] <: Pos
}

/** Le nombre 1 en "binaire" */
trait Un extends Pos {
  type This = Un

  final type Match[R, un <: R, db0[_ <: Pos] <: R, db1[_ <: Pos] <: R] = un

  final type chain[R, F[_ <: Pos] <: R] = F[Un]

  type fold[R, un <: R, db0[_ <: R] <: R, db1[_ <: R] <: R] = un


  // 1 + 1 = 2 * 1
  final type inc = Db0[Un]

  // 1 + p = p + 1
  final type +[p <: Pos] = p # inc

  // 1 * p = p
  final type *[p <: Pos] = p

}

/** Les nombres pairs (le double de `n`) */
trait Db0[n <: Pos] extends Pos {

  final type This = Db0[n]

  final type Match[R, un <: R, db0[_ <: Pos] <: R, db1[_ <: Pos] <: R] = db0[n]

  final type chain[R, F[_ <: Pos] <: R] = F[Db0[n]]

  type fold[R, un <: R, db0[_ <: R] <: R, db1[_ <: R] <: R] = db0[n # fold[R, un, db0, db1]]

  // (2 * n + 0) + 1 = 2 * n + 1
  final type inc = Db1[n]

  // +

  /* (2 * n + 0) + (     1   ) = 2 * n + 1
   * (2 * n + 0) + (2 * m + 0) = 2 * (n + m) + 0
   * (2 * n + 0) + (2 * m + 1) = 2 * (n + m) + 1
   */
  final type +[p <: Pos] = p # Match[Pos, Db1[n] , fun1 , fun2 ]

  final type fun1[m <: Pos] = m # +[n] # chain[Pos, Db0]
  final type fun2[m <: Pos] = m # +[n] # chain[Pos, Db1]

  // *

  /* (2 * n + 0) * (     1   ) = 2 * n + 1
   * (2 * n + 0) * (2 * m + 0) = 4 * (n * m) = 2 * (2 * (n * m) + 0) + 0
   * (2 * n + 0) * (2 * m + 1) = 4 * (n * m) + (2 * n + 0)
   */
  final type *[p <: Pos] = p # Match[Pos , This , fun3 , fun4 ]

  final type fun3[m <: Pos] = ({type f[x <: Pos] = Db0[Db0[x]]})#f[m # *[n]]
  final type fun4[m <: Pos] = ({type f[x <: Pos] = x # + [n] # chain[Pos, Db0]})#f[m # *[n] # chain[Pos, Db0]]


}

/** Les nombres impairs strictement plus grands que 1 (le double de `n` + 1) */
trait Db1[n <: Pos] extends Pos {

  final type This = Db1[n]

  final type Match[R, un <: R, db0[_ <: Pos] <: R, db1[_ <: Pos] <: R] = db1[n]

  final type chain[R, F[_ <: Pos] <: R] = F[Db1[n]]

  type fold[R, un <: R, db0[_ <: R] <: R, db1[_ <: R] <: R] = db1[n # fold[R, un, db0, db1]]

  // (2 * n + 1) + 1 = 2 * (n + 1) + 0
  final type inc = Db0[n#inc]

  // +

  /* (2 * n + 1) + (     1   ) = 2 * (n + 1) + 0
   * (2 * n + 1) + (2 * m + 0) = 2 * (n + m) + 1
   * (2 * n + 1) + (2 * m + 1) = 2 * (n + m + 1) + 0
   */
  final type +[p <: Pos] = p # Match[ Pos, exp1 , fun1, fun2 ]

  final type exp1 = Db0[n # inc]

  final type fun1[m <: Pos] = m # +[n] # chain[Pos, Db1]
  final type fun2[m <: Pos] = m # +[n] # inc # chain[Pos, Db0]

  // *
  /* (2 * n + 1) * (     1   ) = 2 * n + 1
   * (2 * n + 1) * (2 * m + 0) = 4 * (n * m) + (2 * m + 0)
   * (2 * n + 1) * (2 * m + 1) = 4 * (n * m) + (2 * (n + m) + 1)
   */
  final type *[p <: Pos] = p # Match[Pos , This , fun3 , fun4 ]

  final type fun3[m <: Pos] = ({ type f[x <: Pos] = x # + [ m ] # chain[Pos, Db0]})#f[m # *[n] # chain[Pos, Db0]]
  final type fun4[m <: Pos] = ({ type f[x <: Pos] = x # + [ m # +[n] # chain[Pos, Db1] ] })#f[m # *[n] # chain[Pos, ({ type G[x <: Pos] = Db0[Db0[x]]})#G]]
}






