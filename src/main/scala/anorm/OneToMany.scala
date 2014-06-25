package com.jaroop.anorm



/** Describes a one to many relationship between a single parent and child.
*   This is an intermediate class for parsing SQL results, then flattening.
* @param parent The parent object which contains a list of child objects.
* @param child The child object.
* @tparam A The type of the parent object.
* @tparam B The type of the child object.
*/
case class OneToMany[A, B](parent: A, child: B)

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
            .mapValues(_.map(_.child))
            .toList
            .map{ case (a, b) => rf(a, b)}
    }

}