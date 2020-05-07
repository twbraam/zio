package zlayer.d_always_close_connection

import zio._
import zio.console._
import zlayer.d_always_close_connection.domain._
import zlayer.d_always_close_connection.modules.{Logging, UserRepo}


object Main extends zio.App {

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    runnable.fold(_ => 1, _ => 0) // This one will fail because we cannot make the connection

  val user: User = User(UserId(2), "User")
  val makeUser: ZIO[Logging with UserRepo, DBError, Unit] = for {
    _ <- Logging.info(s"inserting user")  // ZIO[Logging, Nothing, Unit]
    _ <- UserRepo.createUser(user)       // ZIO[UserRepo, DBError, Unit]
    _ <- Logging.info(s"user inserted")   // ZIO[Logging, Nothing, Unit]
  } yield ()


  val horizontal: ZLayer[Console, DBError, Logging with UserRepo] = Logging.consoleLogger ++ UserRepo.fullRepo

  val fullLayer: Layer[DBError, Logging with UserRepo] = Console.live >>> horizontal
  val runnable: ZIO[Any, DBError, Unit] = makeUser.provideLayer(fullLayer)

}
