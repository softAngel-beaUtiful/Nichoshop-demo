require('coffee-script/register');
require('./gulpfile.coffee');

/*
gulp.task('connect', function(){
    connect.server({
        root: './app', 
	port: 9500,
    middleware: function(connect, o) {
      return [ (function() {
        var url = require('url');
        var proxy = require('proxy-middleware');
        var options = url.parse('http://localhost:8080');
        options.route = '/api';
        return proxy(options);
      })()]
    }
    });
});
*/
