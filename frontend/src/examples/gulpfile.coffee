# Resources could be interesting:
#
# https://blog.engineyard.com/2014/frontend-dependencies-management-part-2
# http://thibaudb.com/starting-with-gulp-coffeescript/
# http://david.nowinsky.net/gulp-book/example/angular-templates.html
# http://paislee.io/a-healthy-gulp-setup-for-angularjs-projects/
# bower support and live reload https://gist.github.com/ktmud/9384509
# http://blog.nodejitsu.com/npmawesome-9-gulp-plugins/

gulp = require 'gulp'

parameters = require './config/parameters.coffee'

order = require "gulp-order"
coffee  = require 'gulp-coffee'
concat  = require 'gulp-concat'
gutil   = require 'gulp-util'
bower = require 'gulp-bower'
bowerFiles = require 'main-bower-files'
vendor = require 'gulp-concat-vendor'
path    = require 'path'
less    = require 'gulp-less'
es      = require 'event-stream'
# gulp-change
#change  = require 'gulp-change'
modify  = require 'gulp-modify'
del = require('del')
vinylPaths = require('vinyl-paths')
flatten = require('gulp-flatten')


gulp.task 'default', ['bower-resolve', 'compile:vendor', 'css-files', 'compile:app', 'css', 'fonts']

gulp.task 'watch', ['watch:coffee', 'watch:js', 'watch:css']

gulp.task 'watch:coffee',  ->
  gulp.watch parameters.app_path + '/sources/**/*.coffee', ['compile:app' ]

gulp.task 'watch:js',  ->
  gulp.watch parameters.app_path + '/sources/**/*.js', ['compile:app' ]

gulp.task 'watch:css',  ->
  gulp.watch parameters.app_path + '/sources/**/*.css', ['css' ]

gulp.task 'compile:app', ->
  cback = (file, content) ->
    fname = file.history[file.history.length - 1].substr(file.base.length)
    process.stdout.write(' --> filename: ' + fname + '\n')
    content

  c = gulp.src parameters.app_path+'/sources/coffee/**/*.coffee'
  .pipe coffee bare: true
  .pipe order [
    "classes/*.js"
    "**/nichoshopApp.js"
    "**/nichoshopMainController.js"
    "*.js"
  ]
  .pipe(modify({fileModifier:cback}))
  j = gulp.src parameters.app_path+'/sources/javascript/**/*.js'

  es.merge j, c
  .pipe concat parameters.app_main_file
  .pipe gulp.dest parameters.web_path+'/assets/js'
  .on 'error', gutil.log

gulp.task 'bower-resolve', () ->
  bower()

gulp.task 'compile:vendor', ['clean:vendor']

# task create one js file for vendor javascripts
gulp.task 'build:vendor',['bower-resolve'], () ->
  cback = (file, content) ->
    fname = file.history[file.history.length - 1].substr(file.base.length)
    res =
      if (fname == "bootstrap.js" || fname == "bootstrap.min.js")
        "(function(window, document){ " + content + " })(wrap(window), wrap(document));"
      else
        content
    res

# Need to fix bootstrap.js on fly: https://github.com/Polymer/polymer/issues/625
  gulp.src bowerFiles('**/*.js')
  .pipe(modify({fileModifier: cback}))
  .pipe concat 'vendor.js'
  .pipe(gulp.dest(parameters.web_path + '/assets/js/test/'))
  .pipe concat 'vendor.js'
  .pipe(gulp.dest(parameters.web_path + '/assets/js/'))

gulp.task 'clean:vendor', ['build:vendor'], () ->
  del [
    parameters.web_path + '/assets/js/test/'
  ]

gulp.task 'css', () ->
  gulp.src parameters.app_path+'/sources/css/**/*.css'
  .pipe concat parameters.css_main_file
  .pipe gulp.dest parameters.web_path+'/assets/css'

#gulp.task 'bower-files',['bower-resolve'], () ->
#  gulp.src bowerFiles('**/*.js')
#  .pipe concat parameters.bower_main_file
#  .pipe gulp.dest parameters.web_path+'/js'
#  .on 'error', gutil.log

gulp.task 'css-files',['bower-resolve'], () ->
  c = gulp.src bowerFiles('**/*.css')
  l = gulp.src bowerFiles('**/*.less')
  .pipe less()

  es.merge(c, l)
  .pipe concat parameters.bower_css_file
  .pipe gulp.dest parameters.web_path+'/assets/css'
  .on 'error', gutil.log


gulp.task 'fonts', () ->
  gulp.src parameters.app_path+'/bower_components/**/*.{ttf,woff,woff2,eof,svg}'
  .pipe flatten()
  .pipe gulp.dest parameters.web_path+'/assets/fonts'
