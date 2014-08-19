package controllers

import securesocial.core._
import play.api.mvc.RequestHeader
import auth.SocialUser

class Application(override implicit val env: RuntimeEnvironment[SocialUser]) extends securesocial.core.SecureSocial[SocialUser] {

  def index = UserAwareAction { implicit request =>
      Ok(views.html.index("Hey, Tony!")(request.user))
  }

  def editpost(postName: Option[String]) = UserAwareAction { implicit request =>
    //TODO: add post contents in case of EDIT action
    Ok(views.html.editpost("Hey, Tony!")(request.user))
  }

  def posts(postName: String) = UserAwareAction { implicit request =>
    Ok(views.html.post("Hey, Tony!")(request.user))
  }
}

// An Authorization implementation that only authorizes uses that logged in using twitter
case class WithProvider(provider: String) extends Authorization[SocialUser] {
  def isAuthorized(user: SocialUser, request: RequestHeader) = {
    user.profile.providerId == provider
  }
}