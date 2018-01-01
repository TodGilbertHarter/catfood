/**
 * This software is Copyright (C) 2017 Tod G. Harter. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.giantelectronicbrain.catfood.client

import org.scalajs.dom.ext.Ajax
import scala.concurrent.ExecutionContext.Implicits.global
import upickle.default._
import org.scalajs.dom
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel
import org.scalajs.dom.document
import japgolly.scalajs.react.extra._
import japgolly.scalajs.react.extra.router._

// Application Properties
case class Props(title: String = "CatFood", subtitle: String = "The Cat is Awesome")

case class TopicId(cluster: Int, instance: Int) {
  override def toString(): String = {
    return cluster+":"+instance;
  }
}
object TopicId {
  implicit def rw: ReadWriter[TopicId] = macroRW
}

case class Topic(topicId: TopicId, name: String, content: String)
object Topic {
  implicit def rw: ReadWriter[Topic] = macroRW
}

/**
 * Service which provides access to the CatFood server's REST API for getting
 * and saving topics.
 */
object TopicService {
  
  /**
   * Make a topic id from a topic id string
   */
  def makeIdFromStr(idStr: String): TopicId = {
    val ps = idStr.split(':');
    return new TopicId(ps(0).toInt,ps(1).toInt);
  }

  /**
   * Parse a JSON string and deserialize it to a Topic
   */
  def parseTopicJSON(json: String): Topic = {
    return upickle.default.read[Topic](json);
  }
  
  /**
   * Get a topic by name from the CatFood server. Invoke the given
   * callback when the request finishes.
   */
  def getTopic(topicName: String, callback: (Topic) => Unit): Unit = {
    Ajax.get("/data/topic/byname/"+topicName).onSuccess {
      case xhr => val topicJSON = xhr.responseText
      val topic = parseTopicJSON(topicJSON)
      callback(topic)
    }
  }
}

case class State(currentTopic: Topic)
/**
 * CatFood main React component backend implementation. This does the rendering of the application, mounts routers and other
 * components, etc.
 */
class CatFoodBackend(scope: BackendScope[Props, String]) {

  def render(p: Props, s: String) = {<.div()(
      <.header(^.cls := "w3-row w3-bottombar w3-border-theme")(
      <.div(^.cls := "w3-col s2 m1 left-side")(Logo()),
      <.div(^.cls := "w3-rest w3-theme-dark w3-container")(<.span(^.cls := "w3-xxxlarge")(p.title),<.span(^.paddingLeft := 6.px)(p.subtitle))),
      CatFoodPages(),
      <.footer(^.cls := "w3-row w3-theme-light")
        (<.div(^.cls := "w3-col s10 w3-tiny w3-center")("&amp;copy; 2017 Tod G. Harter"),
            <.div(^.cls := "w3-rest w3-right-align")(Badge()))
      ) }
}
  
/**
 * The top-level exported application component. This does 2 things. First it provides a javascript-visible
 * entry point for the CatFood scalajs-react application. Secondly it acts as the root react component. When
 * the entrypoint is invoked, the component will render itself, initiating the CatFood client logic. All we
 * need is a simple JS launcher and the required JS libraries to be loaded.
 */
@JSExportTopLevel("CatFood")
object CatFood {  
  
  val component = ScalaComponent.builder[Props]("CatFood")
        .initialState[String]("42:1")
        .renderBackend[CatFoodBackend]
        .build
        
  def apply(p: Props) = component(p);
  
  /**
   * Entry point to call to start CatFood client.
   */
  @JSExport
  def meow(): Unit = {
    val appNode = document.getElementById("app-node")
    val p = new Props()
    CatFood(p).renderIntoDOM(appNode)
  }

}

/**
 * CatFood Router. This uses the scalajs-react router to allow us to display everything in the CatFood main page SPA.
 */
object CatFoodPages {

  sealed trait Page
  case object Home extends Page
  case class View(topicName: String) extends Page
  case object Edit extends Page
  
  val viewPage = ScalaComponent.builder[View]("ViewPage")
    .render(p => ViewPage(p.props.topicName))
    .build
        
  val routerConfig = RouterConfigDsl[Page].buildConfig {
    dsl => import dsl._
    
    (
      staticRoute(root, Home) ~> render(HomePage())
      | dynamicRouteCT("/" ~ "view" / string("[a-z0-9]{1,20}").caseClass[View]) ~> dynRender(viewPage(_))
      | staticRoute("edit", Edit) ~> render(EditPage())
    )
      .notFound(redirectToPage(Edit)(Redirect.Replace))
      .setTitle(p => s"View Topic - $p.props")
      .renderWith(layout(_,_))
//      .verify(Home,View,Edit)
    
  }
  
  def layout(c: RouterCtl[Page], r: Resolution[Page]) = 
    <.div(^.cls := "w3-row")(
        <.div(
            ^.cls := "w3-col s3 m2 l1 left-side")(
            <.div(navMenu(c)
        )),
        <.div(^.cls := "w3-rest", r.render())
    )
    
  val navMenu = ScalaComponent.builder[RouterCtl[Page]]("Menu")
    .render_P { ctl => 
      def nav(name: String, target: Page) =
        <.span(
            ^.cls := "w3-bar-item w3-button",
            ctl setOnClick target,
            name)
            
      <.div(
          ^.cls := "w3-bar-block",
          nav("Home",Home),
          nav("View",View("sometopicname")),
          nav("Edit",Edit))
    }
    .configure(Reusability.shouldComponentUpdate)
    .build
    
    val baseUrl = dom.window.location.hostname match {
      case "localhost" | "127.0.0.1" | "0.0.0.0" =>
        BaseUrl.fromWindowUrl(_.takeWhile(_ != '#'))
      case _ =>
        BaseUrl.fromWindowOrigin
    }
    
    def apply() = {
      var router = Router(baseUrl, routerConfig.logToConsole)
      router()
    }
}

/**
 * This is the real meat of the application. Here we load a topic using AJAX and display it with react-markdown.
 */
object TopicViewer {
  val component = ScalaComponent.static("TopicViewer")(<.div()("loading..."))

  def apply() = component()
}

object TopicEditor {
  val component = ScalaComponent.static("TopicEditor")(<.div()("editing..."))
  
  def apply() = component()
}

/**
 * These are page rendering objects which render the content of the various routes built by the CatFoodRouter
 */
object EditPage {
  val component = ScalaComponent.static("EditTopic")(TopicEditor())
  
  def apply() = component()
}

object ViewPage {
  val component = ScalaComponent.builder[String]("ViewTopic").render(tn => <.span()(tn.props)).build // TopicViewer())
  
  def apply(topicName: String) = component(topicName)
}

object HomePage {
  val component = ScalaComponent.static("HomePage")(TopicViewer())
  
  def apply() = component()
}

/**
 * Display the CatFood Logo, such as it is.
 */
object Logo {

  val component = ScalaComponent.static("Logo")(<.img(^.cls := "w3-image", ^.alt := "CatFood Logo", ^.src := "./icons/catfood.png"))
  
  def apply() = component()
}

/**
 * Display the ScalaJS badge. This is a simple and useful test component to play with.
 */
object Badge {
  val component = ScalaComponent.static("Badge")(<.img(^.src := "https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg"))

  def apply() = component()
}
