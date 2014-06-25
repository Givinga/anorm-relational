package com.jaroop.anorm

/** Describes a one to many relationship between a single parent and child.
*   This is an intermediate class for parsing SQL results, then flattening.
* @param parent The parent object which contains a list of child objects.
* @param child The child object.
* @tparam A The type of the parent object.
* @tparam B The type of the child object.
*/
case class OneToMany[A, B](parent: A, child: Option[B])

case class OneToMany2[A, B1, B2](parent: A, c1: Option[B1], c2: Option[B2])

case class OneToMany3[A, B1, B2, B3](parent: A, c1: Option[B1], c2: Option[B2], c3: Option[B3])

case class OneToMany4[A, B1, B2, B3, B4](parent: A, c1: Option[B1], c2: Option[B2], c3: Option[B3], c4: Option[B4])

object OneToMany {

    /** Flattens a one-to-many relation by using an implicit `RowFlattener` to copy the
    *   children into the parent, leaving on the parent.
    * @tparam A The type of the parent objects in the one-to-many relation.
    * @tparam B The type of the child objects in the one-to-many relation.
    * @param relations The list of relations to flatten.
    * @param rf A `RowFlattener` that describes how to copy a list of children into a parent.
    * @return A list of parent objects only, containing the children.
    */
    def flatten[A, B](relations: List[OneToMany[A, B]])(implicit rf: RowFlattener[A, B]): List[A] = {
        relations.groupBy(_.parent)
            .map{ case (a, rel) => rf(a, rel.flatMap(_.child))}
            .toList
    }

    def flatten[A, B1, B2](relations: List[OneToMany2[A, B1, B2]])(implicit rf: RowFlattener2[A, B1, B2]): List[A] = {
        relations.groupBy(_.parent)
            .mapValues(_.map(t => (t.c1, t.c2)).unzip)
            .toList
            .map{ case (a, (b, c)) => rf(a, b.flatten.distinct, c.flatten.distinct)}
    }

    def flatten[A, B1, B2, B3](relations: List[OneToMany3[A, B1, B2, B3]])(implicit rf: RowFlattener3[A, B1, B2, B3]): List[A] = {
        relations.groupBy(_.parent)
            .mapValues(_.map(t => (t.c1, t.c2, t.c3)).unzip3)
            .toList
            .map{ case (p, (c1, c2, c3)) => rf(p, c1.flatten.distinct, c2.flatten.distinct, c3.flatten.distinct)}
    }

    def flatten[A, B1, B2, B3, B4](relations: List[OneToMany4[A, B1, B2, B3, B4]])(implicit rf: RowFlattener4[A, B1, B2, B3, B4]): List[A] = {
        relations.groupBy(_.parent)
            .toList
            .map{ case (p, t) => 
                rf(
                    p, 
                    t.map(_.c1).flatten.distinct, 
                    t.map(_.c2).flatten.distinct, 
                    t.map(_.c3).flatten.distinct, 
                    t.map(_.c4).flatten.distinct
                )
            }
    }

    /** Transform a non-optional relation to `Some` for compatibility with `flatten` */
    def apply[A, B](parent: A, child: B): OneToMany[A, B] = OneToMany[A, B](parent, Some(child))

    def apply[A, B1, B2](parent: A, c1: B1, c2: B2): OneToMany2[A, B1, B2] = OneToMany2[A, B1, B2](parent, Some(c1), Some(c2))

    def apply[A, B1, B2, B3](parent: A, c1: B1, c2: B2, c3: B3): OneToMany3[A, B1, B2, B3] = OneToMany3[A, B1, B2, B3](parent, Some(c1), Some(c2), Some(c3))

    def apply[A, B1, B2, B3, B4](parent: A, c1: B1, c2: B2, c3: B3, c4: B4): OneToMany4[A, B1, B2, B3, B4] = OneToMany4[A, B1, B2, B3, B4](parent, Some(c1), Some(c2), Some(c3), Some(c4))

}
