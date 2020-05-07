package zlayer.a_simple

import zio._
import zlayer.a_simple.domain._


object Main extends zio.App {

  def run(args: List[String]) =
    runtime.unsafeRun(ZIO(runnable))

  val runtime = Runtime.default

  def getUser(userId: UserId): ZIO[DBConnection, Nothing, Option[User]] = UIO.succeed(Some(User(userId, s"user ${userId.id}")))
  def createUser(user: User): ZIO[DBConnection, Nothing, Unit] = UIO.succeed(println(s"Create user: $user success"))

  val user: User = User(UserId(1), "User1")
  val created: ZIO[DBConnection, Nothing, Int] = for {
    maybeUser <- getUser(user.id)
    res <- maybeUser.fold(createUser(user).as(1))(_ => ZIO.succeed(0))
  } yield res

  val dbConnection: DBConnection = DBConnection()
  val runnable: ZIO[Any, Nothing, Int] = created.provide(dbConnection)
}
