package com.jaroop.anormext

import anorm._

/** A wrapper for a `RowParser[OneTomany[A, B]]` to control flattening of the result set.
* @param parser The underlying Anorm parser used.
*/
case class RelationalParser[A, B](parser: RowParser[OneToMany[A, B]]) {

    def * = RelationalResultParser[A, B, List[A]](this, identity[List[A]])

    def + = RelationalResultParser[A, B, List[A]](this, {list => assert(list.nonEmpty); list} )

    def singleOpt = RelationalResultParser[A, B, Option[A]](this, list => list.headOption)

    def single = RelationalResultParser[A, B, A](this, list => list.head)

}

case class RelationalParser2[A, B1, B2](parser: RowParser[OneToMany2[A, B1, B2]]) {

    def * = RelationalResultParser2[A, B1, B2, List[A]](this, identity[List[A]])

    def + = RelationalResultParser2[A, B1, B2, List[A]](this, {list => assert(list.nonEmpty); list} )

    def singleOpt = RelationalResultParser2[A, B1, B2, Option[A]](this, list => list.headOption)

    def single = RelationalResultParser2[A, B1, B2, A](this, list => list.head)

}

case class RelationalParser3[A, B1, B2, B3](parser: RowParser[OneToMany3[A, B1, B2, B3]]) {

    def * = RelationalResultParser3[A, B1, B2, B3, List[A]](this, identity[List[A]])

    def + = RelationalResultParser3[A, B1, B2, B3, List[A]](this, {list => assert(list.nonEmpty); list} )

    def singleOpt = RelationalResultParser3[A, B1, B2, B3, Option[A]](this, list => list.headOption)

    def single = RelationalResultParser3[A, B1, B2, B3, A](this, list => list.head)

}

case class RelationalParser4[A, B1, B2, B3, B4](parser: RowParser[OneToMany4[A, B1, B2, B3, B4]]) {

    def * = RelationalResultParser4[A, B1, B2, B3, B4, List[A]](this, identity[List[A]])

    def + = RelationalResultParser4[A, B1, B2, B3, B4, List[A]](this, {list => assert(list.nonEmpty); list} )

    def singleOpt = RelationalResultParser4[A, B1, B2, B3, B4, Option[A]](this, list => list.headOption)

    def single = RelationalResultParser4[A, B1, B2, B3, B4, A](this, list => list.head)
}


/** Holds several `apply` methods to make defining parsers a bit cleaner. */
object RelationalParser {

    def apply[A, B](parent: RowParser[A], child: RowParser[B]): RelationalParser[A, B] = {
    	RelationalParser[A, B] (
	    	parent~child map {
	            case p~c => OneToMany(p, c)
	        }
    	)
    }

    def apply[A, B1, B2](parent: RowParser[A], c1: RowParser[B1], c2: RowParser[B2]): RelationalParser2[A, B1, B2] = {
        RelationalParser2[A, B1, B2] (
            parent~c1~c2 map {
                case p~c1~c2 => OneToMany(p, c1, c2)
            }
        )
    }

    def apply[A, B1, B2, B3](parent: RowParser[A], c1: RowParser[B1], c2: RowParser[B2], c3: RowParser[B3]): RelationalParser3[A, B1, B2, B3] = {
        RelationalParser3[A, B1, B2, B3] (
            parent~c1~c2~c3 map { 
                case p~c1~c2~c3 => OneToMany(p, c1, c2, c3)
            }
        )
    }

    def apply[A, B1, B2, B3, B4](parent: RowParser[A], c1: RowParser[B1], c2: RowParser[B2], c3: RowParser[B3], c4: RowParser[B4]): RelationalParser4[A, B1, B2, B3, B4] = {
        RelationalParser4[A, B1, B2, B3, B4] (
            parent~c1~c2~c3~c4 map { 
                case p~c1~c2~c3~c4 => OneToMany(p, c1, c2, c3, c4)
            }
        )
    }

}