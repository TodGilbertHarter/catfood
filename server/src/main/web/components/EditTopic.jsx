
define(function(require,exports) {
    
    var EditTopic = React.createClass({

        render : function() {
/*            var propVal = '';
            for(var property in this.props) {
                propVal += property + '=' + this.props[property] + ',';
                if(property == 'location'){
                    propVal += '['
                    for(var pproperty in this.props[property]) {
                        propVal += pproperty + "=" + this.props[property][pproperty] + ","
                    }
                    propVal += ']';
                }
            } */
            
            return(<div>Editing {this.props.topicname}</div>);
        }
    });
    exports.EditTopic = EditTopic;
});