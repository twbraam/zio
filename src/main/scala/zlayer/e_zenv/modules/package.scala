package zlayer.e_zenv

import zio.Has

package object modules {
  type UserRepo = Has[UserRepo.Service]
  type Logging = Has[Logging.Service]

}
