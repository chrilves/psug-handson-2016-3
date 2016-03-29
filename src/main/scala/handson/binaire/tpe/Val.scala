package handson.binaire.tpe

abstract sealed class Val[n <: Pos] {
  val value : BigInt
}

object Val {
  def apply[n <: Pos](implicit v : Val[n]) : BigInt = v.value

  implicit final val val_Un = new Val[Un] { final val value = BigInt(1) }

  implicit def val_Db0[n <: Pos](implicit v : Val[n]) = new Val[Db0[n]] { final val value = v.value * 2     }
  implicit def val_Db1[n <: Pos](implicit v : Val[n]) = new Val[Db1[n]] { final val value = v.value * 2 + 1 }
}