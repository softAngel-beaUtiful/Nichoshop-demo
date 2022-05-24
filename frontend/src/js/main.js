var LOGIN_URL = '/api/sessions';
var REGISTER_URL = '/api/signup/';
var EMAIL_CHECK_URL = '/api/signup/check_email/'; // need to apend email after it

var ERR_REG_FIRST_NAME = 'First name can not be empty';
var ERR_REG_LAST_NAME = 'Last name can not be empty';
var ERR_REG_USERNAME = 'Username can not be empty';
var ERR_REG_EMAIL_EMPTY = 'Email can not be empty';
var ERR_REG_EMAIL_INVALID = 'Email is invalid';
var ERR_REG_EMAIL_IN_USE = 'This email is already used';
var ERR_REG_PASSWORD = 'Password can not ne empty';
var ERR_REG_PASSWORD_CONFIRM = 'Please confirm your password';
var ERR_REG_PASSWORD_DONT_MATCH = 'Passwords does not match';
var ERR_REG_PASSWORD_NOT_STRONG = 'Please use a stronger password';
var ERR_REG_CONNECTION_ERROR = 'An eror occurred';

var EMAIL_REGEX = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
var ALLOWED_LOGIN_ATTEMPTS = 2;

$(document).ready(function() {

	var loginAttempt = 1;

	// TODO REMOVE
	var passwordRecoveryEmail;
	
	// TODO REMOVE WHEN CONNECTING WITH SERVER SIDE
	$("#password-recovery-email-submit-button").on("click", function(e) {
		e.preventDefault();
		var email = $(".password-recovery-email").val();

  		if(!EMAIL_REGEX.test(email)){
  			$(".password-recovery-container form").addClass('has-error');
			$(".register-error-msg-header-box").show();
  		} else {
  			passwordRecoveryEmail = email;
  			$(".password-recovery-container").hide();
  			$(".password-recovery-type-container").show();
  			$(".password-recovery-agreement-msg").show();
  		}
	});
	// TODO REMOVE WHEN CONNECTING WITH SERVER SIDE
	$(".password-recovery-send-email").on("click", function(e) {
		e.preventDefault();

		$(".password-recovery-types-container").hide();
		$(".password-recovery-agreement-msg").hide();
		$(".password-recovery-email-link-container").show();
		$(".password-recovery-email-link-container .register-success-msg-header-box .register-success-msg-title").html("We sent an email to " + passwordRecoveryEmail);
	});

	// TODO REMOVE WHEN CONNECTING WITH SERVER SIDE
	$(".password-recovery-give-call, .password-recovery-send-text").on("click", function(e) {
		e.preventDefault();

		$(".password-recovery-types-container").hide();
		$(".password-recovery-agreement-msg").hide();
		$(".password-recovery-enter-pin-container").show();
	});

	// TODO REMOVE WHEN CONNECTING WITH SERVER SIDE
	$("#password-recovery-pin-submit-button").on("click", function(e) {
		e.preventDefault();

		var pin = $("#password-recovery-pin").val();

		if (pin == 2000) {
  			$(".password-recovery-container form").addClass('has-error');
			$(".register-error-msg-header-box").show();
		}

		if (pin == 1000) {
			$(".password-recovery-enter-pin-container").hide();
			$(".password-recovery-alternatives-container").hide();
			$(".password-recovery-new-passwords-container").show();
		}
	});

	// TOOLTIPS
	$("#tos-checkbox").on("change", function(event, ui) {
			
		if ($(this).prop("checked")) {

			$('#tos-label').tooltipster({
		        content: $("<span>If this is a public or shared device, <br> un-tick and sign out when you're <br> done to protect your account.</span>"),
		    	theme: 'tooltipster-light',
		    	position: 'right',
		    	trigger: 'click',
    			touchDevices: false,
		    });
		}
	});

	$('#register-password-1').tooltipster({
        content: $("<span>Create a secure password.</span> <br/> <br/> <ul><li>At least 8 characters</li><li>UPPER CASE letter</li><li>lower case letter</li><li>special character</li><li>number</ul>"),
    	theme: 'tooltipster-light',
    	position: 'left',
    	trigger: 'hover',
    	touchDevices: false,
    });

	$('#register-password-2').tooltipster({
        content: $("<span>Repeat the first password.</span>"),
    	theme: 'tooltipster-light',
    	position: 'right',
    	trigger: 'hover',
    	touchDevices: false,
    });

	$('.password-recovery-new-password').tooltipster({
        content: $("<span>Create a secure password.</span> <br/> <br/> <ul><li>At least 8 characters</li><li>UPPER CASE letter</li><li>lower case letter</li><li>special character</li><li>number</ul>"),
    	theme: 'tooltipster-light',
    	position: 'right',
    	trigger: 'hover',
    	touchDevices: false,
    });

	$('.password-recovery-confirm-new-password').tooltipster({
        content: $("<span>Repeat the password</span>"),
    	theme: 'tooltipster-light',
    	position: 'right',
    	trigger: 'hover',
    	touchDevices: false,
    });

	// LOGIN
    $('#login-button').on('click', function() {
    	var loginButton = $(this);
    	loginButton.prop('disabled', true);

    	var username = $('.login-container .login-username').val();
    	var password = $('.login-container .login-password').val();
    	var tos = $('.login-container #tos-checkbox').is(':checked') ? true : false

		var data = {
		    "grecaptcha":"<google recaptcha>",
		    "rememberMe": tos
		}

		if (loginAttempt > ALLOWED_LOGIN_ATTEMPTS) {
			data["grecaptcha"] = grecaptcha.getResponse();
		}

		var successCallback = function (response) {
			if (response.error) {
				loginAttempt++;

				$('.login-container .login-msg-box').show();
				$('.login-container .login-msg-box span').html(response.error);
			} else {
				loginAttempt = 0;

				// TODO replace with actual login
				alert("Logged as " + response.first_name + ' ' + response.last_name + ' with userId: ' + response.user_id);
			}
		}
		var failCallback = function (jqXHR, status, error) {
			loginAttempt++;
			$('.login-container .login-msg-box').show();
			$('.login-container .login-msg-box span').html(error);
		}
		var alwaysCallback = function (response) {
    		loginButton.prop('disabled', false);
			
			var responseJSON = JSON.parse(response.responseText);
    		if ((typeof responseJSON != 'undefined' && responseJSON['grecaptcha_required']) || loginAttempt > ALLOWED_LOGIN_ATTEMPTS) {
    			toggleLoginRecaptcha(true);
    		}
		}

    	$.ajax
		({
			type: 'POST',
			url: LOGIN_URL,
			dataType: 'json',
			headers: {"Authorization": "Basic " + btoa(username + ":" + password)},
			data: data,
		})
		.done(successCallback)
		.fail(failCallback)
		.always(alwaysCallback);	
    });

	// REGISTER
	$('#register-button').on('click', function(e) {
		e.preventDefault();
    	var regButton = $(this);
    	regButton.prop('disabled', true);

    	// collect data
    	var firstnameField = $('#register-first-name');
    	var firstnameWrapper = firstnameField.parent();
    	var firstnameError = firstnameWrapper.find('.register-error-msg-box');
    	var firstname = firstnameField.val();

    	var lastnameField = $('#register-last-name');
    	var lastnameWrapper = lastnameField.parent();
    	var lastnameError = lastnameWrapper.find('.register-error-msg-box');
    	var lastname = lastnameField.val();

    	var usernameField = $('#register-username');
    	var usernameWrapper = usernameField.parent();
    	var usernameError = usernameWrapper.find('.register-error-msg-box');
    	var username = usernameField.val();

    	var emailField = $('#register-email');
    	var emailWrapper = emailField.parent();
    	var emailError = emailWrapper.find('.register-error-msg-box');
    	var email = emailField.val();

    	var password1Field = $('#register-password-1');
    	var password1Wrapper = password1Field.parent();
    	var password1Error = password1Wrapper.find('.register-error-msg-box');
    	var password1 = password1Field.val();

    	var password2Field = $('#register-password-2');
    	var password2Wrapper = password2Field.parent();
    	var password2Error = password2Wrapper.find('.register-error-msg-box');
    	var password2 = password2Field.val();

    	// collect errors and highlight error fields 
		var errors = [];
		$('.login-container div').removeClass('has-error');

    	if (!firstname) {
    		errors.push(ERR_REG_FIRST_NAME);
			firstnameWrapper.addClass('has-error');
			firstnameError.html(ERR_REG_FIRST_NAME);
    	}
    	if (!lastname) {
    		errors.push(ERR_REG_LAST_NAME);
			lastnameWrapper.addClass('has-error');
    	}
    	if (!username) {
    		errors.push(ERR_REG_USERNAME);
			usernameWrapper.addClass('has-error');
    	}
    	if (!email) {
    		errors.push(ERR_REG_EMAIL_EMPTY);
			emailWrapper.addClass('has-error');
    	}
    	if (email && !EMAIL_REGEX.test(email)) {
    		errors.push(ERR_REG_EMAIL_INVALID);
			emailWrapper.addClass('has-error');
    	}
    	if (!password1) {
    		errors.push(ERR_REG_PASSWORD);
			password1Wrapper.addClass('has-error');
    	}
    	if (password1 && !password2) {
    		errors.push(ERR_REG_PASSWORD_CONFIRM);
			password2Wrapper.addClass('has-error');
    	}
    	if (password1 && password2 && password1 != password2) {
    		errors.push(ERR_REG_PASSWORD_DONT_MATCH);
			password1Wrapper.addClass('has-error');
			password2Wrapper.addClass('has-error');
    	}
    	if (password1 && password2 && password1 == password2 && password1.length <= 8) {
    		errors.push(ERR_REG_PASSWORD_NOT_STRONG);
			password1Wrapper.addClass('has-error');
			password2Wrapper.addClass('has-error');
    	}

    	// show errors or send data if everything is ok
    	if (errors.length > 0) {
    		$(".register-error-msg-header-box").show();
    		var errosList = $(".register-error-msg-header-box ul");
    		errosList.empty();
    		$(errors).each(function(i) {
    			errosList.append('<li>' + this + '</li>');
    		});

    		regButton.prop('disabled', false);
			
    	} else {
			var data = {
			    "grecaptcha": grecaptcha.getResponse(),
			    "userid": username,
			    "email": email,
			    "fname": firstname,
			    "lname": lastname,
			    "password": password1
			}

			var successCallback = function(data, status, jqXHR) {
				if (data.success) {
					//TODO replace with actual login
					alert('Logged as '+ data.email)
				} else {
					$(".register-error-msg-header-box").show();
		    		var errosList = $(".register-error-msg-header-box ul");
		    		errosList.empty();
		    		errosList.append('<li>' + data.error + '</li>');
				}
			}

			var failCallback = function () {
				$(".register-error-msg-header-box").show();
	    		var errosList = $(".register-error-msg-header-box ul");
	    		errosList.empty();
	    		errosList.append('<li>' + ERR_REG_CONNECTION_ERROR + '</li>');
			}

			var alwaysCallback = function () {
				regButton.prop('disabled', false);
			}

	    	postRequest(REGISTER_URL, data, successCallback, failCallback, alwaysCallback);
    	}
	});

	$('.login-container .form-horizontal input:not(#register-email)').on('blur', function() {
		var inputField = $(this);
    	var inputValue = inputField.val();
    	var inputWrapper = inputField.parent();
    	var inputError = inputWrapper.find('.register-error-msg-box');

    	if (inputValue) {
    		inputWrapper.removeClass('has-error');
    		inputError.html('');
    	} else {
    		inputWrapper.addClass('has-error');

    		if (inputField.attr('id') == 'register-first-name') {
    			inputError.html(ERR_REG_FIRST_NAME);
    		} else if (inputField.attr('id') == 'register-last-name') {
    			inputError.html(ERR_REG_LAST_NAME);
    		} else if (inputField.attr('id') == 'register-username') {
    			inputError.html(ERR_REG_USERNAME);
    		} else if (inputField.attr('id') == 'register-password-1') {
    			inputError.html(ERR_REG_PASSWORD);
    		} else if (inputField.attr('id') == 'register-password-2') {
    			inputError.html(ERR_REG_PASSWORD_CONFIRM);
    		} else {

    		}
    	}
	});

	$('.login-container .form-horizontal input#register-email').on('blur', function() {
		var emailField = $(this);
    	var emailValue = emailField.val();
    	var emailWrapper = emailField.parent();
    	var emailError = emailWrapper.find('.register-error-msg-box');

    	if (!emailValue) {
    		emailWrapper.addClass('has-error');
			emailError.html(ERR_REG_EMAIL_EMPTY);
    	} else if (!EMAIL_REGEX.test(emailValue)) {
    		emailWrapper.addClass('has-error');
			emailError.html(ERR_REG_EMAIL_INVALID);
    	} else {
    		var successCallback = function(data) {
    			if (data.exists) {
		    		emailWrapper.addClass('has-error');
					emailError.html(ERR_REG_EMAIL_IN_USE);
    			} else {
					emailWrapper.removeClass('has-error');
					emailError.html('');
    			}
    		}
    		
    		var failCallback = function() {
				emailWrapper.removeClass('has-error');
				emailError.html('');
    		}

			getRequest(EMAIL_CHECK_URL + emailValue, undefined, successCallback, failCallback)
    	}
	});
});

function postRequest(url, data, successCallback, failCallback, alwaysCallback) {
	$.ajax({
		type: "POST",
		url: url,
		data: JSON.stringify(data),
		success: successCallback,
		dataType: 'json',
		contentType: 'application/json;charset=UTF-8',
	})
	.fail(failCallback)
	.always(alwaysCallback);
}

function getRequest(url, data, successCallback, failCallback, alwaysCallback) {
	var jqxhr = $.get(url, data, successCallback, 'JSON')
	.fail(failCallback)
	.always(alwaysCallback);
}

function toggleLoginRecaptcha(toggle) {
	var recaptcha = $('#login-recaptcha');

	if (toggle) {
		//show login recaptcha
		recaptcha.removeClass('hidden');
	} else {
		//hide login recaptcha
		recaptcha.addClass('hidden');
	}
}
