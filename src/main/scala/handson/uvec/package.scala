package handson

package object uvec {

  /** Syntax un peu moins verbeuse que VCons( .... ) :
    *
    * Exemple: 1 |:| (2 |:| (3 |:| VNil))
    *
    */
  implicit final class VecSyntax[A](val self : A) extends AnyVal {
    def |:|[B >: A](v : Vec[B]) = VCons(self , v)
  }

  def uvec_1 = VCons(1, VCons(2, VCons(3, VNil)))

  def uvec_1_1 = uvec_1.map(x => x * 10)

  def uvec_2 : Vec[Int => Int] =
    VCons((x => x * 10) ,
      VCons((x => x * 100) ,
        VCons((x => x * 1000) ,
          VNil)))

  def uvec_2_1 = uvec_2.map(f => f(5))


  def uvec_2_2 = uvec_1.ap(uvec_2)

  def uvec_2_3 = uvec_1.ap(VNil)


  def bench_1(n : Long) = {
    def x : Vec[Int]        = Vec.ind(n)(0)(_ + 1)
    def f : Vec[Int => Int] = x.map(i => x => i * x)

    val start = System.currentTimeMillis()
    x.ap(f)
    val stop  = System.currentTimeMillis()
    stop - start
  }
}
