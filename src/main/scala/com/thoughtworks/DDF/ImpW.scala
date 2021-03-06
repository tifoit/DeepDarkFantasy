package com.thoughtworks.DDF

import com.thoughtworks.DDF.Arrow.BEvalArrow

trait ImpW[Info[_], Repr[_], T] {
  ext =>
  type Weight

  implicit val l: Loss[Weight]

  def w: Weight

  def wi: Info[Weight]

  def exp: Repr[Weight => T]

  def eval: BEval[Weight => T]

  def update[TL](rate: Double, tl: TL)(implicit ti: Loss.Aux[T, TL]): ImpW[Info, Repr, T] = {
    val newW = l.update(w)(rate)(new BEvalArrow {}.aeval(eval).forward(ext.l.convert(w)).backward(tl))
    new ImpW[Info, Repr, T] {
      override type Weight = ext.Weight

      override implicit val l: Loss[ext.Weight] = ext.l

      override val w: ext.Weight = newW

      override val exp: Repr[ext.Weight => T] = ext.exp

      override val eval: BEval[ext.Weight => T] = ext.eval

      override val wi: Info[ext.Weight] =  ext.wi
    }
  }
}

object ImpW {
  import com.thoughtworks.DDF.CombUnit.CombUnit
  def apply[Info[_], Repr[_], T](expT: Repr[T], evalT: BEval[T])(
    implicit cuex: CombUnit[Info, Repr], cuev: CombUnit[Loss, BEval]): ImpW[Info, Repr, T] =
    new ImpW[Info, Repr, T] {
      override type Weight = scala.Unit

      override implicit val l: Loss[scala.Unit] = cuev.unitInfo

      override def wi: Info[scala.Unit] = cuex.unitInfo

      override val w: scala.Unit = ()

      override val exp: Repr[scala.Unit => T] = cuex.app(cuex.K(cuex.reprInfo(expT), cuex.unitInfo))(expT)

      override val eval: BEval[scala.Unit => T] = cuev.app(cuev.K(cuev.reprInfo(evalT), cuev.unitInfo))(evalT)
    }
}