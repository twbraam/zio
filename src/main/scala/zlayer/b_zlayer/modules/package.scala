package zlayer.b_zlayer

import zio.Has

package object modules {
  type UserRepo = Has[UserRepo.Service]
  type Logging = Has[Logging.Service]

}
