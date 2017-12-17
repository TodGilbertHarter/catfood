//import { Button } from 'react-bootstrap';
import { Button } from 'reactstrap';
//import { Ajax } from 'react-ajax';

define(function(require,exports) {

    // why does require work here when we normally use Import? fudge if I know! 
    var Ajax = require('react-ajax');
    var ReactMarkdown = require('react-markdown');
    
    class ViewTopic extends React.Component {

        constructor(props) {
            super(props);
            this.state = { markup : "<p>please wait</p>"};
            this._responseHandler = this._responseHandler.bind(this);
        }

        render() {
            var Content = <Button>FOO</Button>; // this.state.markup;
//            return <Content/>;
            
            return(<div id={'ViewTopic_'+this.props.topicname} className="row"><Button bsSize='xsmall'>EDIT</Button>
                    <ReactMarkdown source={this.state.markup}/>
                    <Ajax url={'/data/topic/byname/'+this.props.topicname} onResponse={this._responseHandler} accept='json'/>
            </div>);
        }

        _renderMarkup() {
            var celement = React.createElement(this.state.markup);
        }
        
        _responseHandler(err,data) {
            if(data.statusType == 4 && data.status == 200) {
                this.setState({ markup : data.text});
            } else {
                var markup = data.body != null ? data.body.content : this.props.topicname+' could not be found';
                this.setState({ markup : markup, url : this.state.url });
            }
        }
        
    }
    
    exports.ViewTopic = ViewTopic;
});