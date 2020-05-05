package zlayer.c_updated.modules

import zio._
import zlayer.c_updated.domain.{DBConnection, DBError, User, UserId}


object UserRepo extends DBConnection {
  trait Service {
    def getUser(userId: UserId): IO[DBError, Option[User]]
    def createUser(user: User): IO[DBError, Unit]
  }
  //accessor methods
  def getUser(userId: UserId): ZIO[UserRepo, DBError, Option[User]] =
    ZIO.accessM(_.get.getUser(userId))
  def createUser(user: User): ZIO[UserRepo, DBError, Unit] =
    ZIO.accessM(_.get.createUser(user))


  // This simple live version depends only on a DB Connection
  val inMemory: Layer[Nothing, UserRepo] = ZLayer.succeed(
    new Service {
      def getUser(userId: UserId): IO[Nothing, Option[User]] = UIO(Some(User(userId, s"user ${userId.id}")))
      def createUser(user: User): IO[Nothing, Unit] = UIO(println(s"Create user: $user success"))
    }
  )

  val postgres: Layer[Nothing, UserRepo] = ZLayer.succeed(new UserRepo.Service {
    override def getUser(userId: UserId): IO[DBError, Option[User]] = UIO.succeed(Some(User(userId, s"userfromPostgres ${userId.id}")))
    override def createUser(user: User): IO[DBError, Unit] = UIO.succeed(println(s"Create user in Postgres: $user success"))
  })
}
