package com.tr.ccmsbatchclient.util

import akka.actor.ActorSystem

object ActorSystemUtil {

  def withActorSystem(function: (ActorSystem) => Any): Any = {
    val actorSystem: ActorSystem = ActorSystem()
    val result: Any = try {
      function(actorSystem)
    } finally {
      actorSystem.terminate()
    }
    result
  }
}

