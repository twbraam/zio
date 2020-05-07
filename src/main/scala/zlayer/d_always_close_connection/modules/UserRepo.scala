package zlayer.d_always_close_connection.modules

import zio._
import zlayer.d_always_close_connection.domain.{DBConnection, DBError, User, UserId}


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


  import java.sql.{Connection, DriverManager}

  def makeConnection: IO[DBError, Connection] = Task(DriverManager.getConnection("jdbc:mysql://localhost:6666/jcg", "root", "password"))
    .catchAll {
    case _ : java.sql.SQLException =>
      IO.fail(DBError())
  }
  val connectionLayer: Layer[DBError, Has[Connection]] =
    ZLayer.fromAcquireRelease(makeConnection)(c => UIO(c.close()))

  val postgresConnectionLayer: ZLayer[Has[Connection], Nothing, UserRepo] =
    ZLayer.fromFunction { hasC =>
      new UserRepo.Service {
        override def getUser(userId: UserId): IO[DBError, Option[User]] = UIO.succeed(Some(User(userId, s"usermegaposgresyo ${userId.id}")))
        override def createUser(user: User): IO[DBError, Unit] = UIO.succeed(println(s"Create user in usermegaposgresyo: $user success"))
      }
    }

  val fullRepo: Layer[DBError, UserRepo] = connectionLayer >>> postgresConnectionLayer
}
