*Anorm Relational*
------------------------------------------------

Anorm relational is a simple wrapper around the Anorm library available within [Play Framework](http://www.playframework.com), designed to extend the parsing API to handle one-to-many relationships with ease. It is currently available for Play 2.3.x and Anorm 2.5.x.

Motivations
-----------

Anorm is a great light-weight library that allows you to write your own SQL and parse the results, without the need for an ORM.  It's able to parse complicated data structures easily using the parser combinator API, but even that has its drawbacks. Consider this simple example:

```scala
case class Blog(id: Long, title: String, posts: List[Post])
case class Post(id: Long, title: String, body: String)
```

Let's say that I'm hosting some sort of multi-blog site, and when you visit a particular blog, you see a landing page with a summary of posts. When serving this page I want to pass the `Blog` to the view for rendering. I have two options for constructing this object. I could call something like `Blog.read(id)`, then `Post.list(blogId)` to retrieve the posts that belong to that `Blog` and copy them within. Or, `Blog.read(id)` could achieve this all within one query:

```SQL
SELECT * FROM blogs b
LEFT OUTER JOIN posts p ON(p.blog_id = b.id)
WHERE b.id = {id}
```

I prefer the second option, because it saves another round-trip to the database. Anorm however, has no natural way of processing the result rows, because a `RowParser` parses rows to objects, but not multiple rows to a single object. Therefore, parsing a result set containing a one-to-many relation must be done like so:

```scala
/** Parses a `Blog` without nested posts. */
val simple: RowParser[Blog] = {
    get[Long]("blogs.id") ~ get[String]("blogs.title") map {
        case id~title => Blog(id, title, Nil)
    }
}

/** Parses a `Blog` paired with an optional `Post` that can be later be collapsed into one object. */
val parser: RowParser[(Blog, Option[Post])] = {
    simple ~ Post.parser.? map {
        case blog~post => (blog, post)
    }
}

/** Selects a blog with related posts within one query, parses the rows as (blog, post) tuples,
*   then groups the tuples by blog to eventually nest the posts within their parent blog.
*/
def read(id: Long): Option[Blog] = {
    DB.withConnection { implicit c =>
        SQL("""
            SELECT * FROM blogs b
            LEFT OUTER JOIN posts p ON(p.blog_id = b.id)
            WHERE b.id = {id}
        """).on("id" -> id)
            .as(parser *)
            .groupBy(_._1)
            .mapValues(_.map(_._2).flatten)
            .toList
            .map{ case (blog, posts) => blog.copy(posts = posts) }
            .headOption
    }
}
```

As you can see, there is a good chunk of logic involved to make this work. It's terrible to reason about, and also not very portable. The `parser` can be re-used, but there logic thereafter must be duplicated, which is undesirable. If for some reason you wanted to implement a similar `list` function that list all blogs with their nested posts, you would have to do this:

```scala
def list: List[Blog] = {
    DB.withConnection { implicit c =>
        SQL("""
            SELECT * FROM blogs b
            LEFT OUTER JOIN posts p ON(p.blog_id = b.id)
        """).as(parser *)
            .groupBy(_._1)
            .mapValues(_.map(_._2).flatten)
            .toList
            .map{ case (blog, posts) => blog.copy(posts = posts) }
    }
}
```

This isn't very much different than `read`, but it duplicates the post-parsing logic, which I find unacceptable. And what happens when we complicate the example? What if it looks more like this?

``` scala
case class Blog(id: Long, title: String, posts: List[Post], authors: List[User])
case class Post(id: Long, title: String, body: String, tags: List[Tag], comments: List[Comment])
case class Comment(id: Long, subject: String, body: String)
case class User(id: Long, email: String)
```

The same `groupBy` technique can be used to solve the problem of fetching a `Blog` in a single query, however you must start over completely to figure out the transformations--and you can trust me in that it will look horribly ugly. This is where the `RelationalParser` and `RowFlattener` will come in.

Installation
------------

Add the following to your build dependencies in your `build.sbt` or `Build.scala` file:

```
"com.jaroop" %% "anorm-relational" % "0.3.0"
```

For example in your `build.sbt`:

```
libraryDependencies ++= Seq(
    "com.jaroop" %% "anorm-relational" % "0.3.0"
)
```

Use this table to determine which version to use:

| Play/Anorm Version  | Anorm Relational Version | Scala 2.10 | Scala 2.11 | Scala 2.12 |
| ------------------- | ------------------------ | ---------- | ---------- | ---------- |
| Play 2.3.x          | 0.1.0                    | &#10003;   | &#10003;   |            |
| Anorm 2.5.0         | 0.2.0                    | &#10003;   | &#10003;   |            |
| Anorm 2.5.3         | 0.3.0                    |            | &#10003;   | &#10003;   |

Usage
-----

Anorm relational uses the "enrich my library" pattern to add an `asRelational` method to Anorm's `SQL` and `SqlQuery`.  In any file you wish to use the implicit converters `import com.jaroop.anorm.relational._` 

Using the simpler Blog/Post example, we will need to define a `RelationalParser` that is composed of `RowParser`s for the `Blog` and `Post` types. A `RelationalParser` is not an actual type. Instead, it's a collection of helper `apply` methods that construct special `RowParser`s which make it easier to unravel multiple relations. The first argument of `RelationalParser.apply` is the `RowParser` for the parent object, and the following arguments are the `RowParser`s for the child objects.

```scala
val relationalParser = RelationalParser(Blog.simple, Post.parser)
```

Then, we must define an implicit `RowFlattener`, which is essentially a function that describes how to copy lists of child objects into their parent.

```scala
implicit val rf = RowFlattener[Blog, Post] { (blog, posts) => blog.copy(posts = posts) }
```

Now we can re-write `Blog.read` like so:

```scala
def read(id: Long): Option[Blog] = {
    DB.withConnection { implicit c =>
        SQL("""
            SELECT * FROM blogs b
            LEFT OUTER JOIN posts p ON(p.blog_id = b.id)
            WHERE b.id = {id}
        """).on("id" -> id)
            .asRelational(relationalParser.singleOpt)
    }
}
```

This looks much cleaner now that `groupBy` has been swept under the rug. It also allows us to modify `Blog.list` in a similar way, using `asRelational(relationalParser *)` instead of 5 lines of difficult logic. All of the code duplication has also vanished.

Handling nested relations that go more than one level deep is also possible, though this requires exposing some of the internals to make it work for now. Consider the (somewhat contrived) case where our `Post` also has a nested `List[Comment]`, and we want to select a `Blog` with all of its nested posts and respective comments. The `Post` class will now look a little different, and require its own `RowFlattener` and `RelationalParser`. 

```scala
case class Post(id: Long, title: String, body: String, comments: List[Comment])

object Post {

    val simple: RowParser[Post] = {
        get[Long]("posts.id") ~
        get[String]("posts.title") ~
        get[String]("posts.body") map {
            case id~title~body => Post(id, title, body, Nil)
        }
    }
    
    implicit val RowFlattener[Post, Comment]{ (post, comments) => post.copy(comments = comments) }
    
    val relationalParser = RelationalParser(simple, Comment.parser)
}
```
In order to handle another `RelationalParser` within the `Blog` `RelationalParser`, the `RowFlattener` will need a slight modification using `OneToMany.flatten`:

```scala
implicit val rf = RowFlattener[Blog, Post] { (blog, posts) => blog.copy(posts = OneToMany.flatten(posts)) }
```

Disclaimer
----------

Anorm relational 0.3.0 currently uses Anorm 2.5.3 as a dependency, which may cause it to break on earlier versions. There are currently `RowFlattener` and `RelationalParser` classes available for up to 5 one-to-many relations, though this should be more than enough for a reasonable amount of data. Version 0.3.0 is a prototype, so there is a high possibility that breaking changes may be introduced to the API in the future. While it is currently in use on production servers without any known issues, I still advise to proceed at your own risk.

License
-------

Anorm relational is distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
