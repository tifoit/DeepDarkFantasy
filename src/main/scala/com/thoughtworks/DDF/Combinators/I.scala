package com.thoughtworks.DDF.Combinators

import com.thoughtworks.DDF.Arrow.Arrow

trait I[Info[_], Repr[_]] extends Arrow[Info, Repr] {
  def I[A](implicit ai: Info[A]): Repr[A => A]

  final def I_[A]: Repr[A] => Repr[A] = a => app(I[A](reprInfo(a)))(a)
}
