// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui.hooks

import crystal.react.*
import crystal.react.hooks.*
import japgolly.scalajs.react.*
import japgolly.scalajs.react.hooks.CustomHook
import lucuma.ui.enums.Theme

private object UseTheme:

  private val hook =
    CustomHook[Theme]
      .useStateViewBy(initial => initial)
      .useEffectOnMountBy((initial, _) => Theme.init(initial))
      .buildReturning((_, theme) => theme.withOnMod(_.mount))

  object HooksApiExt:
    sealed class Primary[Ctx, Step <: HooksApi.AbstractStep](api: HooksApi.Primary[Ctx, Step]):
      final def useTheme(initial: Theme = Theme.System)(using step: Step): step.Next[View[Theme]] =
        useThemeBy(_ => initial)

      final def useThemeBy(initial: Ctx => Theme)(using step: Step): step.Next[View[Theme]] =
        api.customBy(ctx => hook(initial(ctx)))

    final class Secondary[Ctx, CtxFn[_], Step <: HooksApi.SubsequentStep[Ctx, CtxFn]](
      api: HooksApi.Secondary[Ctx, CtxFn, Step]
    ) extends Primary[Ctx, Step](api):
      def useThemeBy(initial: CtxFn[Theme])(using step: Step): step.Next[View[Theme]] =
        super.useThemeBy(step.squash(initial)(_))

  trait HooksApiExt:
    import HooksApiExt.*

    implicit def hooksExtTheme1[Ctx, Step <: HooksApi.AbstractStep](
      api: HooksApi.Primary[Ctx, Step]
    ): Primary[Ctx, Step] =
      new Primary(api)

    implicit def hooksExtTheme2[
      Ctx,
      CtxFn[_],
      Step <: HooksApi.SubsequentStep[Ctx, CtxFn]
    ](
      api: HooksApi.Secondary[Ctx, CtxFn, Step]
    ): Secondary[Ctx, CtxFn, Step] =
      new Secondary(api)

  object syntax extends HooksApiExt
