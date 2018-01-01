define(function(require,exports) {
    var React = require('react');

var Hello = React.createClass({
	render : function() {
	    console.log("what the hell is up");
	    return(<h1>Hello {this.props.toWhat}</h1>);
	}
});
exports.default = Hello;
});

