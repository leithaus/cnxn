// -*- mode: Scala;-*- 
// Filename:    Claimant.scala 
// Authors:     lgm                                                    
// Creation:    Tue Feb 11 11:00:15 2014 
// Copyright:   Not supplied 
// Description: 
// ------------------------------------------------------------------------

package com.protegra_ati.agentservices.protocols

import com.biosimilarity.evaluator.distribution.{PortableAgentCnxn, PortableAgentBiCnxn}
import com.biosimilarity.evaluator.distribution.diesel.DieselEngineScope._
import com.biosimilarity.evaluator.distribution.ConcreteHL.PostedExpr
import com.protegra_ati.agentservices.protocols.msgs._
import com.biosimilarity.lift.model.store.CnxnCtxtLabel
import com.biosimilarity.lift.lib._
import scala.util.continuations._
import java.util.UUID

trait DealershipBehaviorT extends ProtocolBehaviorT with Serializable {
  import com.biosimilarity.evaluator.distribution.utilities.DieselValueTrampoline._
  import com.protegra_ati.agentservices.store.extensions.StringExtensions._

  def run(
    node : Being.AgentKVDBNode[PersistedKVDBNodeRequest, PersistedKVDBNodeResponse],
    cnxns : Seq[PortableAgentCnxn],
    filters : Seq[CnxnCtxtLabel[String, String, String]]
  ): Unit = {
    BasicLogService.tweet(
      (
        "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
        + "\ndealership -- behavior instantiated and run method invoked " 
        + "\nnode: " + node
        + "\ncnxns: " + cnxns
        + "\nfilters: " + filters
        + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
      )
    )
    println(
      (
        "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
        + "\ndealership -- behavior instantiated and run method invoked " 
        + "\nnode: " + node
        + "\ncnxns: " + cnxns
        + "\nfilters: " + filters
        + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
      )
    )
    receiveContent( node, cnxns )
  }  
  def provideHashedConnections() : Seq[PortableAgentCnxn] 
  def receiveContent(
    node: Being.AgentKVDBNode[PersistedKVDBNodeRequest, PersistedKVDBNodeResponse],
    cnxns: Seq[PortableAgentCnxn]
  ): Unit = {    
    cnxns match {
      case recipientToSomebody :: _ => {
        val agntRecipientCnxnsRdWr =
          for( cnxn <- cnxns ) yield {
            (
              acT.AgentCnxn( cnxn.src, cnxn.label, cnxn.trgt ),
              acT.AgentCnxn( cnxn.trgt, cnxn.label, cnxn.src )
            )
          }

        for( ( agntCnxn, _ ) <- agntRecipientCnxnsRdWr ) {
          BasicLogService.tweet(
            (
              "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
              + "\ndealership -- waiting for content publication on: " 
              + "\nagntCnxn: " + agntCnxn
              + "\nlabel: " + PublishWithReo.toLabel
              + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
            )
          )
          println(
            (
              "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
              + "\nclaimant -- waiting for initiate claim on: " 
              + "\nagntCnxn: " + agntCnxn
              + "\nlabel: " + PublishWithReo.toLabel
              + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
            )
          )
          
          reset {
            for( eCheckForNewMessages <- node.subscribe( agntCnxn )( CheckForNewMessages.toLabel ) ) {
              rsrc2V[AuresoProtocolMessage]( eCheckForNewMessages ) match {
                case Left( CheckForNewMessages( sidPC, cidPC, msgLimit, msgPriority, msgDesc ) ) => {
                  BasicLogService.tweet(
                    (
                      "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                      + "\ndealership -- received CheckForNewMessages request: " + eCheckForNewMessages
                      + "\ncnxn: " + agntCnxn
                      + "\nlabel: " + CheckForNewMessages.toLabel
                      + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                    )
                  )
                  println(
                    (
                      "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                      + "\ndealership -- received CheckForNewMessages request: " + eCheckForNewMessages
                      + "\ncnxn: " + agntCnxn
                      + "\nlabel: " + CheckForNewMessages.toLabel
                      + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                    )
                  )

                  for( eNewMessagesResponse <- node.subscribe( agntCnxn )( msgDesc.toLabel ) ) {
                    rsrc2V[AuresoProtocolMessage]( eNewMessagesResponse ) match {
                      case Left( eNewMessagesResponse( sidPC, cidPC, msgLimit, msgPriority ) ) => {
                      }
                }
                case Right( true ) => {
                  BasicLogService.tweet(
                    (
                      "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                      + "\ndealership -- still waiting for CheckForNewMessages request"
                      + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                    )
                  )
                  println(
                    (
                      "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                      + "\ndealership -- still waiting for CheckForNewMessages request"
                      + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                    )
                  )
                }
                case _ => {
                  // BUGBUG : lgm -- protect against strange and
                  // wondrous toString implementations (i.e. injection
                  // attack ) for eInitiateClaim
                  BasicLogService.tweet(
                    (
                      "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                      + "\ndealership -- while waiting for CheckForNewMessages request"
                      + "\nreceived unexpected protocol message : " + eCheckForNewMessages
                      + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                    )
                  )
                  println(
                    (
                      "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                      + "\ndealership -- while waiting for CheckForNewMessages request"
                      + "\nreceived unexpected protocol message : " + eCheckForNewMessages
                      + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
                    )
                  )
                }
              }
            }
          }
        }         
      }
      case _ => {
        BasicLogService.tweet(
          (
            "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
            + "\ndealership -- at least one cnxn expected : " + cnxns
            + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
          )
        )
        println(
          (
            "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
            + "\ndealership -- at least one cnxn expected : " + cnxns
            + "\n||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||"
          )
        )
        throw new Exception( "at least one cnxn expected : " + cnxns )
      }
    }
  }
}

class DealershipBehavior(
) extends DealershipBehaviorT {
  override def run(
    kvdbNode: Being.AgentKVDBNode[PersistedKVDBNodeRequest, PersistedKVDBNodeResponse],
    cnxns: Seq[PortableAgentCnxn],
    filters: Seq[CnxnCtxtLabel[String, String, String]]
  ): Unit = {
    super.run(kvdbNode, cnxns, filters)
  }
  def provideHashedConnections() : Seq[PortableAgentCnxn] =
    List[PortableAgentCnxn]( )
}

object DealershipBehavior {
  def apply( ) : DealershipBehavior = new DealershipBehavior()
  def unapply( cb : DealershipBehavior ) = Some( () )
}
