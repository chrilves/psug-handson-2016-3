package handson.nat

import handson.preuve._
import handson.preuve.prolog._

package object prolog {
  implicit final class VecSyntax[A](val self : A) extends AnyVal {
    def |:|[n <: Nat, B >: A](v : Vec[n , B]) = VCons(self , v)
  }


  val x : Vec[_3, Int] = 1 |:| (2 |:|  (3 |:| VNil))
  val y : Vec[_2, Int] = 4 |:| (5 |:| VNil)

  /*
  val x_plus_y : Vec[_5, Int] = x ++ y
  val x_prod_y : Vec[_6, (Int, Int)] = x ** y */
}
