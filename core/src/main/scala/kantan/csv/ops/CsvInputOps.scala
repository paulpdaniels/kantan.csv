/*
 * Copyright 2016 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.csv.ops

import kantan.csv.{CsvInput, CsvReader, _}
import kantan.csv.engine.ReaderEngine
import scala.collection.generic.CanBuildFrom

/** Provides useful syntax for types that have implicit instances of [[CsvInput]] in scope.
  *
  * The most common use case is to turn a value into a [[CsvReader]] through [[asCsvReader]]:
  * {{{
  *   val f: java.io.File = ???
  *   f.asCsvReader[List[Int]](',', true)
  * }}}
  *
  * A slightly less common use case is to load an entire CSV file in memory through [[readCsv]]:
  * {{{
  *   val f: java.io.File = ???
  *   f.readCsv[List, List[Int]](',', true)
  * }}}
  *
  * Unsafe versions of these methods are also available, even if usually advised against.
  */
final class CsvInputOps[A](val a: A) extends AnyVal {
  /** Shorthand for [[CsvInput!.reader CsvInput.reader]]. */
  def asCsvReader[B: RowDecoder](sep: Char, header: Boolean)
                                (implicit ia: CsvInput[A], e: ReaderEngine): CsvReader[ReadResult[B]] =
    ia.reader[B](a, sep, header)

  /** Shorthand for [[CsvInput.unsafeReader]]. */
  def asUnsafeCsvReader[B: RowDecoder](sep: Char, header: Boolean)
                                      (implicit ia: CsvInput[A], e: ReaderEngine): CsvReader[B] =
    ia.unsafeReader[B](a, sep, header)

  /** Shorthand for [[CsvInput.read]]. */
  def readCsv[C[_], B: RowDecoder](sep: Char, header: Boolean)
                                  (implicit ia: CsvInput[A], e: ReaderEngine,
                                   cbf: CanBuildFrom[Nothing, ReadResult[B], C[ReadResult[B]]]): C[ReadResult[B]] =
    ia.read[C, B](a, sep, header)

  /** Shorthand for [[CsvInput.unsafeRead]]. */
  def unsafeReadCsv[C[_], B: RowDecoder](sep: Char, header: Boolean)
                                        (implicit ia: CsvInput[A], e: ReaderEngine,
                                         cbf: CanBuildFrom[Nothing, B, C[B]]): C[B] =
    ia.unsafeRead[C, B](a, sep, header)
}

trait ToCsvInputOps {
  implicit def toCsvInputOps[A](a: A): CsvInputOps[A] = new CsvInputOps(a)
}

object csvInput extends ToCsvInputOps
