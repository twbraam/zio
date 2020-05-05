package zlayer.c_updated

import zio.Has

package object modules {
  type UserRepo = Has[UserRepo.Service]
  type Logging = Has[Logging.Service]

}
