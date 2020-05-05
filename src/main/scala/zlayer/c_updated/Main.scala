package zlayer.c_updated

import zlayer.c_updated.modules.{Logging, UserRepo}
import zlayer.c_updated.domain._
import zio._
import zio.clock.Clock
import zio.console._
import zio.random.Random


object Main extends zio.App {

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    runnable.fold(_ => 1, _ => 0)

  val user: User = User(UserId(2), "User")
  val makeUser: ZIO[Logging with UserRepo, DBError, Unit] = for {
    _ <- Logging.info(s"inserting user")  // ZIO[Logging, Nothing, Unit]
    _ <- UserRepo.createUser(user)       // ZIO[UserRepo, DBError, Unit]
    _ <- Logging.info(s"user inserted")   // ZIO[Logging, Nothing, Unit]
  } yield ()
  

  val horizontal: ZLayer[Console, Nothing, Logging with UserRepo] = Logging.consoleLogger ++ UserRepo.inMemory
  val updatedHorizontal: ZLayer[Console, Nothing, Logging with UserRepo] = horizontal ++ UserRepo.postgres

  val fullLayer: Layer[Nothing, Logging with UserRepo] = Console.live >>> updatedHorizontal
  val runnable: ZIO[Any, DBError, Unit] = makeUser.provideLayer(fullLayer)
}
