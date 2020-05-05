package zlayer.c_updated.domain

final case class DBError(private val message: String = "DB Error yo",
                         private val cause: Throwable = None.orNull)
  extends Throwable