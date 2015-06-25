// -*- mode: Scala;-*- 
// Filename:    CheckForNewMessages.scala 
// Authors:     lgm                                                    
// Creation:    Tue Feb 11 11:06:08 2014 
// Copyright:   Not supplied 
// Description: 
// ------------------------------------------------------------------------

package com.protegra_ati.agentservices.protocols.msgs

import com.biosimilarity.evaluator.distribution.PortableAgentCnxn
import com.biosimilarity.lift.model.store.CnxnCtxtLabel
import com.protegra_ati.agentservices.store.extensions.StringExtensions._

case class CheckForNewMessages(
  override val sessionId : String,
  override val correlationId : String,
  msgLimit : Int,
  msgPriority : Int
) extends AuresoProtocolMessage( sessionId, correlationId ) {
  override def toLabel : CnxnCtxtLabel[String, String, String] = {
    CheckForNewMessages.toLabel( sessionId )
  }
}

object CheckForNewMessages {
  def toLabel(): CnxnCtxtLabel[String, String, String] = {
    "protocolMessage(publishWithReo(sessionId(_)))".toLabel
  }

  def toLabel(sessionId: String): CnxnCtxtLabel[String, String, String] = {
    s"""protocolMessage(publishWithReo(sessionId(\"$sessionId\")))""".toLabel
  }
}
