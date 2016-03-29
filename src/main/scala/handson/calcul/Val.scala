package handson.calcul


abstract sealed class Val[n <: Nat] {
  val value : BigInt
}

object Val {
  def apply[n <: Nat](implicit v : Val[n]) : BigInt = v.value

  implicit final val val_z = new Val[Z] { final val value = BigInt(0) }

  implicit def val_s[n <: Nat](implicit v : Val[n]) = new Val[S[n]] { final val value = v.value + 1 }
}
