import language.experimental.macros
import scala.reflect.macros.blackbox.Context

package object desugar {
  def explicit(a: Any): String = macro explicitImpl

  def explicitImpl(c: Context)(a: c.Expr[Any]) = {
    import c.universe._
    c.Expr(Literal(Constant(showCode(a.tree))))
  }
}
