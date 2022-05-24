app_path = '.'

config =
  app_path: app_path
  web_path: '.'
  vendor_path: 'vendor'
  assets_path: app_path + '/assets'

  app_main_file: 'app.js'
  css_main_file: 'app.css'
  styles_main_file: app_path + '/app.less' # or app.sass if you prefer Sass
  templates_file: 'app.templates.js'
  templates_module: 'myapp'
  vendor_main_file: 'vendor.js'
  bower_main_file: 'bower-vendor.js'
  bower_css_file: 'vendor.css'
  manifest_file: 'myapp.appcache'

module.exports = config