package com.jaroop.anorm

/** A Function-like class to describe how to copy a list of child objects into
* 	their respective parents.
* @tparam A The type of the parent object in a one-to-many relation.
* @tparam B The type of the child object in a one-to-many relation.
* @param f A function that takes a parent and a list of it's children and returns the parent with the children nested inside.
*/
case class RowFlattener[A, B](f: (A, List[B]) => A) {

	/** Syntactic sugar for applying the `RowFlattener` function, without collision with implicit functions.
	* @param parent The parent object in a one-to-many relation.
	* @param children The children that should be copied into the parent object.
	* @return A parent containing the children in a nested list.
	*/
    def apply(parent: A, children: List[B]): A = f(parent, children)
    
}
