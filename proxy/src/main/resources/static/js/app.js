window.addEventListener('load', function () {
  var lock = new Auth0Lock(AUTH0_CLIENT_ID, AUTH0_DOMAIN, {
    theme: {
      logo: "cropped-logo2.png",
      primaryColor: "#b81b1c"
    },
    languageDictionary: {
      title: "E-Medicus"
    },
    rememberLastLogin: true,
    allowForgotPassword: true,
    allowSignUp: true,
	
    auth: {
      params: { scope: 'openid  email user_metadata xsrf_token' }
    }
  });

  var auth0 = new Auth0({clientID: AUTH0_CLIENT_ID, domain: AUTH0_DOMAIN});
  



  // Check if we are still logged in
  // is it still valid
  if (validToken()) {
    // we can load the profile
    lock.getProfile(readJwt(), function (error, profile) {
      if (error) {
        // Handle error
        return;
      }
      display_user_profile(profile);
    });
  } 

  document.getElementById('btn-login').addEventListener('click', function () {
    lock.show();
  });

  document.getElementById('btn-logout').addEventListener('click', function () {
    logout();
  })

  document.getElementById('btn-public').addEventListener('click', function () {
    callapi('unsecured');
  })

  document.getElementById('btn-secured').addEventListener('click', function () {
    callapi('secured');
  })

  document.getElementById('btn-renewtoken').addEventListener('click', function () {
    renewtoken();
  })


 var renewtoken = function() {
   if(validToken && calcExpiresIn() < 7000) {
    var token = readJwt();
    var headers = insertAuthentication();

      $.ajax({
        url: 'renew',
        headers: headers,
        success: function (result) {
          localStorage.setItem('id_token', result);
        },
        error: function (jqXHR, textStatus, errorThrown) {
          $("#api-result").html(jqXHR.status);
        }
      });
   }
 }


  lock.on("authenticated", function (authResult) {
    console.log(authResult);
    localStorage.setItem('id_token', authResult.idToken);
    var renewTokenTimer = setInterval(renewtoken,  2000);
    lock.getProfile(authResult.idToken, function (error, profile) {
      if (error) {
        // Handle error
        return;
      }

      display_user_profile(profile);
    });
  });

  function readJwt() {
    var jwt = localStorage.getItem('id_token')
    return jwt;
  }

  var parseHash = function () {
    var id_token = readJwt();
    if (id_token) {
      lock.getProfile(id_token, function (err, profile) {
        if (err) {
          return alert('There was an error getting the profile: ' + err.message);
        }
        display_user_profile(profile);
      });
    }
  };

  var display_user_profile = function (profile) {
    document.getElementById('nickname').textContent = profile.nickname;
    document.getElementById('btn-login').style.display = "none";
    document.getElementById('avatar').src = profile.picture;
    document.getElementById('avatar').style.display = "inline-block";
    document.getElementById('btn-logout').style.display = "inline-block";
  };

  var logout = function () {
    localStorage.removeItem('id_token');
    lock.logout();
    window.location.href = 'https://' + AUTH0_DOMAIN + '/v2/logout?client_id=' + AUTH0_CLIENT_ID + '&returnTo=' + location.href;
  };

  var tokenExpires = setInterval(myTimer, 1000);

  function calcExpiresIn() {
    var id_token =  readJwt();
    var expiresIn = -1;
    if (id_token) {
      var decoded = jwt_decode(id_token);
      expiresIn = decoded.exp * 1000 - new Date().valueOf()
    }
    return expiresIn;
  }

  function myTimer() {
    var id_token =  readJwt();
    if (id_token) {
      var decoded = jwt_decode(id_token);
      if (calcExpiresIn() > 0) {
        $("#token-expires").html("Token expires in " + calcExpiresIn() + " [ms]");
      } else {
        $("#token-expires").html("Token has expired");
      }
    }
  }

 
  function validToken() {
    var jwt =  readJwt();
    var valid = false;
    if(jwt) {
      var decoded = jwt_decode(jwt);
      valid =  (decoded.exp * 1000 > new Date().valueOf());
      // not valid remove
      if(!valid) {
        localStorage.removeItem('id_token');
      }
    } 
    return valid;
  }



  function insertAuthentication() {
    var headers = {};
    var id_token = readJwt();
    if(validToken()) {
          headers = { 'Authorization': 'Bearer ' + id_token};
    }
    return headers;
  }

  var callapi = function (uri) {
    headers = insertAuthentication();

    $.ajax({
      url: uri,
      headers: headers,
      success: function (result) {
        $("#api-result").html(result);
      },
      error: function (jqXHR, textStatus, errorThrown) {
        $("#api-result").html(jqXHR.status);
      }
    });
  };
});