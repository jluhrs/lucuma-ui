// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.forms

import cats.effect.Async
import cats.effect.std.Dispatcher
import cats.implicits._
import crystal.ViewF
import crystal.ViewOptF
import crystal.react.implicits._
import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.extra.StateSnapshot
import org.typelevel.log4cats.Logger

trait ExternalValue[EV[_]] {
  def get[A](ev: EV[A]): Option[A]
  def set[A](ev: EV[A]): A => Callback
}

object ExternalValue {
  implicit def externalValueViewF[F[_]: Async: Dispatcher: Logger]: ExternalValue[ViewF[F, *]] =
    new ExternalValue[ViewF[F, *]] {
      override def get[A](ev: ViewF[F, A]): Option[A] = ev.get.some

      override def set[A](ev: ViewF[F, A]): A => Callback =
        ev.set.andThen(_.runAsyncCB)
    }

  implicit def externalValueViewOptF[F[_]: Async: Dispatcher: Logger]
    : ExternalValue[ViewOptF[F, *]] =
    new ExternalValue[ViewOptF[F, *]] {
      override def get[A](ev: ViewOptF[F, A]): Option[A] = ev.get

      override def set[A](ev: ViewOptF[F, A]): A => Callback =
        ev.set.andThen(_.runAsyncCB)
    }

  implicit val externalValueStateSnapshot: ExternalValue[StateSnapshot] =
    new ExternalValue[StateSnapshot] {
      override def get[A](ev: StateSnapshot[A]): Option[A] = ev.value.some

      override def set[A](ev: StateSnapshot[A]): A => Callback = ev.setState
    }
}
