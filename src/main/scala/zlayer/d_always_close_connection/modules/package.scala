package zlayer.d_always_close_connection

import zio.Has

package object modules {
  type UserRepo = Has[UserRepo.Service]
  type Logging = Has[Logging.Service]

}
