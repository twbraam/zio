package zlayer.e_zenv

import zlayer.e_zenv.modules.{Logging, UserRepo}
import zlayer.e_zenv.domain._
import zio._
import zio.clock.Clock
import zio.console._
import zio.random.Random


object Main extends zio.App {

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    runnable.fold(_ => 1, _ => 0)


  val horizontal: ZLayer[Console, Nothing, Logging with UserRepo] = Logging.consoleLogger ++ UserRepo.inMemory
  val fullLayer: Layer[Nothing, Logging with UserRepo] = Console.live >>> horizontal

  val makeUser: ZIO[Logging with UserRepo with Clock with Random, DBError, Unit] = for {
    uId       <- zio.random.nextLong.map(UserId)
    createdAt <- zio.clock.currentDateTime.orDie
    _         <- Logging.info(s"inserting user")
    _         <- UserRepo.createUser(User(uId, "Chet"))
    _         <- Logging.info(s"user inserted, created at $createdAt")
  } yield ()

  val runnable: ZIO[ZEnv, DBError, Unit] = makeUser.provideCustomLayer(fullLayer)

}
