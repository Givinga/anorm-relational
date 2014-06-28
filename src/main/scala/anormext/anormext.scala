package com.jaroop.anormext

import anorm._

package object anormext {

	/** Implicitly convert `SimpleSql[T]` to `RelationalSQL[T]` for almost seemless integration
	* @param sql 
	* @return `RelationalSQL` wrapper of `SimpleSql`
	*/
	implicit def simple2Relational[T](sql: SimpleSql[T]): RelationalSQL[T] = RelationalSQL(sql)
}