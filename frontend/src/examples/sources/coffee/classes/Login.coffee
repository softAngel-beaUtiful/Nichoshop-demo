class Login
  data: {}

  capchaRequired: false

  constructor: (@scope, @http, @location) ->

  loginSubmit: () ->
    console.log("data: " + JSON.stringify(@data))
    @http.defaults.headers.common['Authorization'] = 'Basic ' + btoa(@data.login + ':' + @data.password)
    rememberMe = ""
    if (@data.rememberMe)
      rememberMe = "yes"
    grecaptchaToSend = ""
    if (@capchaRequired)
      grecaptchaToSend = grecaptcha.getResponse()
    @http(_.extend(Methods.sessions, {data: $.param({grecaptcha: grecaptchaToSend, rememberMe: rememberMe})}))
    .success (data, status, headers, config) =>
      @data.login = ""
      @data.password = ""
#      @scope.setupUserData(data)
      toastr.success("Logged in as " + data.firstName + ' ' + data.lastName, "Success!")
      @location.path('/').replace()
    .error (data, status, headers, config) =>
      toastr.error("Error: " + data.error, 'Error!')
      @setCaptchaRequired data["grecaptcha-required"]


  setCaptchaRequired: (value) ->
    if (value == "true")
      @capchaRequired = true
    else
      @capchaRequired = false