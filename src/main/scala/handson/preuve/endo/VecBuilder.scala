package handson.preuve.endo

import handson.preuve._

sealed abstract class VecBuilder[n <: Nat] {
  def apply[A](a : A) : Vec[n , A]
}

object VecBuilder {
  def apply[n <: Nat](implicit vb: VecBuilder[n]) = vb

  implicit final def vb_z = new VecBuilder[Z] {
    def apply[A](a: A): Vec[Z, A] = VNil
  }

  implicit final def vb_s[n <: Nat](implicit vb: VecBuilder[n]) = new VecBuilder[S[n]] {
    def apply[A](a: A): Vec[S[n], A] = VCons[n, A](a, vb(a))
  }
}