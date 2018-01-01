//import { Panel, Button } from 'react-bootstrap';
import { Panel, Button } from 'reactstrap';
// import { LinkContainer } from 'ReactRouterBootstrap';

define(function(require,exports) {
    
    var Home = React.createClass({
        render : function() {
//            return(<Panel header='The Home Topic'><span>Viewing the home topic.</span><LinkContainer to='/view/TestTopic'><Button>TestTopic</Button></LinkContainer></Panel>);
            return(<Panel header='The Home Topic'><span>Viewing the home topic.</span></Panel>);
        }
    });
    exports.Home = Home;
});