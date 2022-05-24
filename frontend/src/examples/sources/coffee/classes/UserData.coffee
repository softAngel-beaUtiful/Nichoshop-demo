class UserData
  data: null

  constructor: (@scope, @http) ->
    @http.get("/api/sessions/status").success((data, status, headers, config) =>
      @scope.setupUserData(data)
    ).error((data, status, headers, config) =>
      console.log("not logged in")
      @scope.login.setCaptchaRequired(data["grecaptcha_required"])
    )
  logout: () ->
    @http.post("/api/sessions/logout").success((data, status, headers, config) =>
      @scope.resetUserData()
    )