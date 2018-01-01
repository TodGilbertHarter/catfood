package com.giantelectronicbrain.catfood.client;

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel
import io.github.shogowada.scalajs.reactjs.VirtualDOM._
import io.github.shogowada.scalajs.reactjs.{React, ReactDOM}
import org.scalajs.dom

/**
 * Top level application object for the CatFood client.
 */
@JSExportTopLevel("CatFood")
object CatFood {


  /**
   * Entry point to call to start CatFood client.
   */
  @JSExport
  def main(): Unit = {
    val appNode = dom.document.getElementById("app-node")
    ReactDOM.render(<.div(^.className := "container")(<.h1()("funny money!")), appNode)
  }
}
