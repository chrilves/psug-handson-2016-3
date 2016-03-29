package handson.preuve

import handson.preuve.endo._

package object endo {

  implicit final class VecSyntax[A](val self : A) extends AnyVal {
    def |:|[n <: Nat, B >: A](v : Vec[n , B]) = VCons(self , v)
  }

  /*
  val x : Vec[_3, Int] = 1 |:| (2 |:|  (3 |:| VNil))
  val y : Vec[_2, Int] = 4 |:| (5 |:| VNil)

  val z : Vec[_3, Int => Int] =
    ((_:Int) *   10) |:| (
    ((_:Int) *  100) |:| (
    ((_:Int) * 1000) |:| (
    VNil )))

  val x_zip_z : Vec[_3, (Int, Int => Int)] = x zip z
  val x_ap_z  : Vec[_3, Int] = x ap z

  */
}
