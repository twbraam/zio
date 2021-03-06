package zlayer.b_zlayer

import zio._
import zio.console._
import zlayer.b_zlayer.domain._
import zlayer.b_zlayer.modules.{Logging, UserRepo}


object Main extends zio.App {

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    runnable.fold(_ => 1, _ => 0)


  val user: User = User(UserId(2), "User")
  val makeUser: ZIO[Logging with UserRepo, DBError, Unit] = for {
    _ <- Logging.info(s"inserting user")  // ZIO[Logging, Nothing, Unit]
    _ <- UserRepo.createUser(user)       // ZIO[UserRepo, DBError, Unit]
    _ <- Logging.info(s"user inserted")   // ZIO[Logging, Nothing, Unit]
  } yield ()

  // compose horizontally
  val horizontal: ZLayer[Console, Nothing, Logging with UserRepo] = Logging.consoleLogger ++ UserRepo.inMemory

  // fulfill missing deps, composing vertically
  val fullLayer: Layer[Nothing, Logging with UserRepo] = Console.live >>> horizontal

  // provide the layer to the program
  val runnable: ZIO[Any, DBError, Unit] = makeUser.provideLayer(fullLayer)
}
