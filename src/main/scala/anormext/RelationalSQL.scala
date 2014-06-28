package com.jaroop.anormext

import anorm._
import java.sql.Connection

/** Holds a `SimpleSql[T]` statement created from `SQL(...)` in order to apply the underlying `RowParser`,
*   then use `RowFlattener`s to flatten the relational structure into the parent object.
*/
case class RelationalSQL[T](sql: SimpleSql[T]) {

    def as[A, B, T](rp: RelationalResultParser[A, B, T])(implicit rf: RowFlattener[A, B], c: Connection): T = 
        rp.f(OneToMany.flatten(this.sql.as(rp.parser.parser *)))

    def as[A, B1, B2, T](rp: RelationalResultParser2[A, B1, B2, T])(implicit rf: RowFlattener2[A, B1, B2], c: Connection): T = 
        rp.f(OneToMany.flatten(this.sql.as(rp.parser.parser *)))

    def as[A, B1, B2, B3, T](rp: RelationalResultParser3[A, B1, B2, B3, T])(implicit rf: RowFlattener3[A, B1, B2, B3], c: Connection): T = 
        rp.f(OneToMany.flatten(this.sql.as(rp.parser.parser *)))

    def as[A, B1, B2, B3, B4, T](rp: RelationalResultParser4[A, B1, B2, B3, B4, T])(implicit rf: RowFlattener4[A, B1, B2, B3, B4], c: Connection): T = 
        rp.f(OneToMany.flatten(this.sql.as(rp.parser.parser *)))

}

/** Holds a `RelationalParser` and a function that describes what to do with the result `List`. */
case class RelationalResultParser[A, B, T](parser: RelationalParser[A, B], f: List[A] => T)

case class RelationalResultParser2[A, B1, B2, T](parser: RelationalParser2[A, B1, B2], f: List[A] => T)

case class RelationalResultParser3[A, B1, B2, B3, T](parser: RelationalParser3[A, B1, B2, B3], f: List[A] => T)

case class RelationalResultParser4[A, B1, B2, B3, B4, T](parser: RelationalParser4[A, B1, B2, B3, B4], f: List[A] => T)
