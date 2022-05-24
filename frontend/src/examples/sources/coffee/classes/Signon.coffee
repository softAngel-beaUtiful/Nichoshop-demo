class Signon
  data: {}
  phoneError: true
  recaptchaId: -1

  constructor: (@scope, @http, @location) ->

  checkPhone: () ->
    @phoneError = (!@data.phone || @data.phone == '')

  setRecaptchaId: (id) =>
    console.log("Got recaptchaId: #{id}")
    @recaptchaId = id

  registerSubmit: () ->
    if (@recaptchaId != -1)
      @data.grecaptcha = @scope.vcRecaptchaService.getResponse(@recaptchaId)

      @http.post("/api/signup/", @data).success((data, status, headers, config) =>
        @data = {}
        @data.confirmation = {
          email: data.email
        }

        toastr.success "Registration was successfull!"
        @location.path("/registerConfirm").replace()

      ). error( (data, status, headers, config) =>
        toastr.error("Error: " + data.error)
      )
