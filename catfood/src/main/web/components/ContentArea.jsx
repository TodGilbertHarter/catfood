import Button from '/components/lib/Button';

class ContentArea extends React.Component {

    constructor(props) {
        super(props);
        this.state = { markup : "please wait"};
        this._responseHandler = this._responseHandler.bind(this);
    }

    render() {
		return(<div id={this.props.id} className="row"><Button bsSize='xsmall'>EDIT</Button>
	            {this.state.markup}
	            <Ajax url={this.props.id} onResponse={this._responseHandler} accept='json'/>
		</div>);
	}

    _responseHandler(err,data) {
        if(data.statusType == 4 && data.status == 200) {
            this.setState({ markup : data.text});
        } else {
            this.setState({ markup : data.body.markup, url : this.state.url });
        }
    }
    
}
