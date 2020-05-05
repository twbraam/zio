package zlayer.d_always_close_connection.domain

final case class DBError(private val message: String = "DB Error yo",
                         private val cause: Throwable = None.orNull)
  extends Throwable