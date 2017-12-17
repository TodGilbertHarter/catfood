/**
 * This software is Copyright (C) 2016 Tod G. Harter. All rights reserved.
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

// import { Panel, PageHeader, Grid, Row, Col, Image, Nav, NavItem, LinkItem } from 'react-bootstrap';
import { UncontrolledAlert } from 'reactstrap';
import { BrowserRouter, Router, Route, NavLink } from 'react-router-dom';
// import { LinkContainer } from 'ReactRouterBootstrap';
import { warning } from 'warning';
import { ViewTopic } from 'ViewTopic';
import { EditTopic } from 'EditTopic';
import { Home } from 'Home';

// No idea why we need this, seems there is some bug somewhere in some transpiling logic!
var _warning2 = { default: function() {} };

function CfPageHead(props) { return <PageHeader>{props.title}</PageHeader> };

// We have to have this because react routers don't actually populate the params element of the routed components props!!!!!
function getTopicNameFromLocation(props) { 
    const location = props.location;
    var propVal = '';
    for(var property in location) {
        propVal += property + '=' + location[property] + ',';
    }

    var rePathParam = /\/(edit|view)\/(.*)$/;
    var foo = location.pathname;
    var foo2 = foo.replace(rePathParam,"$2");
    
    return foo2;
    }

var CatFood = React.createClass({
	render : function() {
	        return(<UncontrolledAlert>I am an alert</UncontrolledAlert>);
    	    }
});

/*
return(<Grid fluid={true}>
        <Row>
         <Col sm={1}><Image src="icons/catfood.png" style={{'border': '5px black solid', 'width':'100%'}} rounded={true} /></Col>
         <Col sm={11}>
          <Panel header={CfPageHead(this.props)}>{this.props.subTitle}</Panel>
         </Col>
        </Row>
        <BrowserRouter>
         <Row>
          <Col sm={1}>
           <Nav bsStyle='pills' stacked>
            <LinkContainer to="/" onlyActiveOnIndex={true}><NavItem>Home</NavItem></LinkContainer>
            <LinkContainer to="/edit/SomeOtherTopic"><NavItem>Edit</NavItem></LinkContainer>
           </Nav>
          </Col>
          <Col sm={11}>
           <div className='content'>
            <Route exact path='/' component={Home}/>
            <Route path='/edit/:topicname' render={(props) => <EditTopic topicname={getTopicNameFromLocation(props)}/>}/>
            <Route path='/view/:topicname' render={(props) => <ViewTopic topicname={getTopicNameFromLocation(props)}/>}/>
           </div>
          </Col>
         </Row>
        </BrowserRouter>
       </Grid>);
}
*/
