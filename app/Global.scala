/**
 * Copyright 2014 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import auth.{SocialUserService, SocialUser, CustomEventListener}
import play.api.GlobalSettings
import play.api.mvc._
import scala.concurrent.Future
import play.api.mvc.Results._
import java.lang.reflect.Constructor
import securesocial.core.RuntimeEnvironment

object Global extends GlobalSettings{

  /**
   * The runtime environment for this sample app.
   */
  object MyRuntimeEnvironment extends RuntimeEnvironment.Default[SocialUser] {
    override lazy val userService: SocialUserService = new SocialUserService()
    override lazy val eventListeners = List(new CustomEventListener())
  }

  override def doFilter(next: EssentialAction): EssentialAction = {
    Filters(super.doFilter(next), AccessLoggingFilter)
  }

  /**
   * An implementation that checks if the controller expects a RuntimeEnvironment and
   * passes the instance to it if required.
   *
   * This can be replaced by any DI framework to inject it differently.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    val instance  = controllerClass.getConstructors.find { c =>
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[RuntimeEnvironment[SocialUser]]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(MyRuntimeEnvironment)
    }
    instance.getOrElse(super.getControllerInstance(controllerClass))
  }

  override def onHandlerNotFound(request: RequestHeader) = {
    implicit val env = Global.MyRuntimeEnvironment
    implicit val header = request
    Future.successful(
      NotFound(views.html.notfound(None))
    )
  }
}
