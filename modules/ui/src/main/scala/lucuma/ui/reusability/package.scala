// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.ui

import cats.Eq
import cats.Order
import cats.data.NonEmptyList
import cats.data.NonEmptySet
import cats.kernel.instances.sortedMap._
import coulomb.Quantity
import eu.timepit.refined.api.RefType
import io.circe.Json
import japgolly.scalajs.react.Key
import japgolly.scalajs.react.ReactCats._
import japgolly.scalajs.react.Reusability
import lucuma.core.data.EnumZipper
import lucuma.core.geom.Area
import lucuma.core.math._
import lucuma.core.math.dimensional._
import lucuma.core.model._
import lucuma.core.model.sequence._
import lucuma.core.util.Enumerated
import lucuma.core.util.NewType
import lucuma.core.util.WithGid
import lucuma.core.util.WithUid
import react.common.Size

import java.time.Duration
import java.time.Instant
import scala.collection.immutable.SortedMap
import scala.collection.immutable.SortedSet

/**
 * Instances of reusability for some utility types
 */
trait UtilReusabilityInstances {
  given enumReuse[A: Enumerated]: Reusability[A] =
    Reusability.by(Enumerated[A].tag)

  given enumZipperReuse[A: Reusability]: Reusability[EnumZipper[A]] =
    Reusability.by(z => (z.lefts, z.focus, z.rights))

  given Reusability[Json] = Reusability.by_==

  given sortedSetReuse[A: Order]: Reusability[SortedSet[A]]    = Reusability.byEq
  given sortedMapReuse[K, V: Eq]: Reusability[SortedMap[K, V]] = Reusability.byEq

  given nonEmptyListReuse[A: Reusability]: Reusability[NonEmptyList[A]] =
    Reusability.by(nel => (nel.head, nel.tail))
  given nonEmptySetReuse[A]: Reusability[NonEmptySet[A]]                =
    Reusability.by(_.toSortedSet.unsorted)

  given Reusability[Key] = Reusability.by_==

  given reusabilityNewType[W, T <: NewType[W]#Type](using
    reusability: Reusability[W]
  ): Reusability[T] =
    reusability.asInstanceOf[Reusability[T]]
}

/**
 * Instances of reusability for some common math types
 */
trait MathReusabilityInstances {
  // Reusability for coulomb quantities.
  given quantityReuse[N: Reusability, U]: Reusability[Quantity[N, U]] = Reusability.by(_.value)
  given Reusability[Angle]                                            = Reusability.by(_.toMicroarcseconds)
  given Reusability[RightAscension]                                   = Reusability.byEq
  given Reusability[Declination]                                      = Reusability.byEq
  given Reusability[Coordinates]                                      = Reusability.byEq
  given Reusability[Epoch]                                            = Reusability.byEq
  given Reusability[ProperMotion]                                     = Reusability.byEq
  given Reusability[RadialVelocity]                                   = Reusability.byEq
  given Reusability[Parallax]                                         = Reusability.byEq
  given Reusability[BigDecimal]                                       = Reusability.byEq
  given sizeReuse(using Reusability[Double]): Reusability[Size]       =
    Reusability.by(x => (x.height, x.width))
  given Reusability[Units]                                            = Reusability.byEq
  given measureReuse[N: Eq]: Reusability[Measure[N]]                  = Reusability.byEq
  given Reusability[Range.Exclusive]                                  = Reusability.by(x => (x.start, x.end, x.step))
  given Reusability[Range.Inclusive]                                  = Reusability.by(x => (x.start, x.end, x.step))
  given Reusability[Area]                                             = Reusability.byEq
  given Reusability[WavelengthRange]                                  = Reusability.byEq
}

/**
 * reusability of time types
 */
trait TimeReusabilityInstances {
  implicit val durationReuse: Reusability[Duration] = Reusability.by(_.getSeconds)
  implicit val instantReuse: Reusability[Instant]   =
    Reusability.by(i => (i.getEpochSecond, i.getNano))
}

/**
 * Generic reusability of refined types
 */
trait RefinedReusabiltyInstances {
  given refTypeCogen[F[_, _], T: Reusability, P](using rt: RefType[F]): Reusability[F[T, P]] =
    Reusability.by(rt.unwrap)
}

/**
 * Reusability instances for model classes
 */
trait ModelReusabiltyInstances
    extends RefinedReusabiltyInstances
    with UtilReusabilityInstances
    with MathReusabilityInstances
    with TimeReusabilityInstances {
  given gidReuse[Id <: WithGid#Id]: Reusability[Id]                    = Reusability.by(_.value)
  given uidReuse[Id <: WithUid#Id]: Reusability[Id]                    = Reusability.by(_.toUuid)
  given Reusability[OrcidId]                                           = Reusability.by(_.value.toString)
  given Reusability[OrcidProfile]                                      = Reusability.byEq
  given Reusability[Partner]                                           = Reusability.byEq
  given Reusability[StandardRole]                                      = Reusability.byEq
  given Reusability[StandardUser]                                      = Reusability.byEq
  given Reusability[CatalogInfo]                                       = Reusability.byEq
  given siderealTrackingReuse: Reusability[SiderealTracking]           = Reusability.byEq
  given Reusability[UnnormalizedSED]                                   = Reusability.byEq
  given Reusability[SourceProfile]                                     = Reusability.byEq
  given Reusability[User]                                              = Reusability.byEq
  given Reusability[Offset]                                            = Reusability.byEq
  given Reusability[Wavelength]                                        = Reusability.byEq
  given spectralDefinitionReuse[T]: Reusability[SpectralDefinition[T]] = Reusability.byEq
  given Reusability[Semester]                                          = Reusability.byEq
  given Reusability[EphemerisKey]                                      = Reusability.byEq
  given Reusability[Target.Sidereal]                                   = Reusability.byEq
  given Reusability[Target.Nonsidereal]                                = Reusability.byEq
  given Reusability[Target]                                            = Reusability.byEq
  given Reusability[ElevationRange.AirMass]                            = Reusability.byEq
  given Reusability[ElevationRange.HourAngle]                          = Reusability.byEq
  given Reusability[ElevationRange]                                    = Reusability.byEq
  given Reusability[ConstraintSet]                                     = Reusability.byEq
  given Reusability[ProposalClass]                                     = Reusability.byEq
  given Reusability[Proposal]                                          = Reusability.byEq
  // Sequences
  given Reusability[GmosNodAndShuffle]                                 = Reusability.byEq
  given Reusability[GmosCcdMode]                                       = Reusability.byEq
  given Reusability[GmosGratingConfig]                                 = Reusability.byEq
  given gmosFpuMaskReuse[T: Eq]: Reusability[GmosFpuMask[T]]           = Reusability.byEq
  given Reusability[StaticConfig]                                      = Reusability.byEq
  given Reusability[StepConfig]                                        = Reusability.byEq
  given Reusability[StepTime]                                          = Reusability.byEq
  given Reusability[Step]                                              = Reusability.byEq
  given Reusability[Atom]                                              = Reusability.byEq
  given Reusability[ManualConfig]                                      = Reusability.byEq
  given Reusability[ExecutionSequence]                                 = Reusability.byEq
  given Reusability[FutureExecutionConfig]                             = Reusability.byEq
}

package object reusability
    extends UtilReusabilityInstances
    with MathReusabilityInstances
    with ModelReusabiltyInstances
    with TimeReusabilityInstances
