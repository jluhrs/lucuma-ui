// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.utils

import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.*
import japgolly.scalajs.react.util.DefaultEffects.{Sync => DefaultS}
import eu.timepit.refined.api.Refined

def linkOverride(f: => DefaultS[Unit]): ReactMouseEvent => Callback =
  e => {
    val forward = linkOverride[Unit](f)
    forward(e, ())
  }

def linkOverride[A](f: => DefaultS[Unit]): (ReactMouseEvent, A) => Callback =
  (e: ReactMouseEvent, _: A) =>
    (e.preventDefaultCB *> f)
      .unless_(e.ctrlKey || e.metaKey)

inline given [T, P](using f: T => VdomNode): Conversion[T Refined P, VdomNode] = v => f(v.value)
