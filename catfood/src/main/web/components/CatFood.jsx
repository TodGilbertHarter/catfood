import { HashRouter } from '/libs/HashRouter';
import { warning } from '/libs/warning';

var Panel = ReactBootstrap.Panel;
var PageHeader = ReactBootstrap.PageHeader;
var Grid = ReactBootstrap.Grid;
var Row = ReactBootstrap.Row;
var Col = ReactBootstrap.Col;
var Image = ReactBootstrap.Image;
var Nav = ReactBootstrap.Nav;
var NavItem = ReactBootstrap.NavItem;

function CfPageHead(props) { return <PageHeader>{props.title}</PageHeader> };

var CatFood = React.createClass({
	render : function() {
		return(<Grid fluid={true}>
		        <Row>
		         <Col sm={1}><Image src="icons/catfood.png" /></Col>
		         <Col sm={11}>
 		          <Panel header={CfPageHead(this.props)}>This is some stuff in the panel</Panel>
 		         </Col>
 		        </Row>
 		        <Row>
                 <HashRouter>
 		          <Col sm={1}>
 		           <Nav bsStyle='pills' stacked>
 		            <LinkItem to='/'><NavItem>View</NavItem></LinkItem>
                    <LinkItem to='/edit'><NavItem>Edit</NavItem></LinkItem>
 		           </Nav>
 		          </Col>
 		          <Col sm={11}>
                     <div className='content'>
 		              <Route exact path='/' component='ViewTopic'/>
 		              <Route path='/edit' component='EditTopic'/>
                     </div>
 		          </Col>
                 </HashRouter>
 		        </Row>
		       </Grid>);
	}
});