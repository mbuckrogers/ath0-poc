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
      params: {client: 'browser'},
      redirect: true,
      redirectUrl: location.href + 'redirect',
      responseMode: 'form_post',
      responseType: "id_token",
      params: { scope: 'openid offline_access email user_metadata xsrf_token' }
    }
  });




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
  } else {
    // try to automatically login with the refresh cookie
    console.log("Trying to renew id_token")
    //renewtoken();
  }


  /* window.onbeforeunload = function() {
     console.log('Logout as well');
     logout();
   }
   */

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


// document.ready(function(){
//     function getCookie(c_name) {
//         if(document.cookie.length > 0) {
//             c_start = document.cookie.indexOf(c_name + "=");
//             if(c_start != -1) {
//                 c_start = c_start + c_name.length + 1;
//                 c_end = document.cookie.indexOf(";", c_start);
//                 if(c_end == -1) c_end = document.cookie.length;
//                 return unescape(document.cookie.substring(c_start,c_end));
//             }
//         }
//         return "";
//     }

//     $(function () {
//         $.ajaxSetup({
//             headers: {
//                 "X-XSRF-TOKEN": getCookie("XSRF-TOKEN")
//             }
//         });
//     });
// });



  lock.on("authenticated", function (authResult) {
    // localStorage.setItem('id_token', authResult.idToken);
    lock.getProfile(authResult.idToken, function (error, profile) {
      if (error) {
        // Handle error
        return;
      }

      display_user_profile(profile);
    });

    storeRefreshToken(authResult.refreshToken, authResult.idToken);

  });

  function readJwt() {
    var jwt = Cookies.get('jwt');
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
    Cookies.expire('jwt');
    lock.logout();
    window.location.href = 'https://thelf.eu.auth0.com/v2/logout?client_id=' + AUTH0_CLIENT_ID + 
    '&returnTo=' + location.href;
  };

  var tokenExpires = setInterval(myTimer, 1000);

  function myTimer() {
    var id_token =  readJwt();
    if (id_token) {
      var decoded = jwt_decode(id_token);
      if (decoded.exp * 1000 > new Date().valueOf()) {
        $("#token-expires").html("Token expires in " + (decoded.exp * 1000 - new Date().valueOf()) + " [ms]");
      } else {
        $("#token-expires").html("Token has expired");
      }
    }
  }

  function renewtoken() {
    var headers = insertAuthentication();
    $.ajax({
      type: "POST",
      url: 'renewtoken',
      headers: headers,
      success: function (data, textStatus, request) {
        $("#api-result").html(textStatus);
      },
      error: function (jqXHR, textStatus, errorThrown) {
        $("#api-result").html(jqXHR.status);
      }
    });
  }

  function storeRefreshToken(refreshToken, id_token) {
    var headers = insertAuthentication();
    var data = { refreshToken: refreshToken,  id_token: id_token};


    console.log(JSON.stringify(data))
    $.ajax({
      type: "POST",
      url: 'storerefresh',
      headers: {'Authorization': 'Bearer ' + id_token,
                'Content-Type': 'application/json'}, 
      data: JSON.stringify(data),
      success: function (result) {
        $("#api-result").html(result);
      },
      error: function (jqXHR, textStatus, errorThrown) {
        $("#api-result").html(jqXHR.status);
      }
    });

  }

  function validToken() {
    var jwt = Cookies.get('jwt');
    var valid = false;
    if(jwt) {
      var decoded = jwt_decode(jwt);
      valid =  (decoded.exp * 1000 > new Date().valueOf());
      // not valid remove
      if(!valid) {
        Cookies.remove('jwt')
      }
    } 
    return valid;
  }



  function insertAuthentication() {
    var headers = {};
    // var id_token = localStorage.getItem('id_token');
    // if (id_token) {
    //   var decoded = jwt_decode(id_token);
    //   // Has the token expired?
    //   if (decoded.exp * 1000 > new Date().valueOf()) {
    //     headers = {
    //       'Authorization': 'Bearer ' + id_token,
    //       'Content-Type': 'application/json'
    //     };
    //     // headers.Authorization = 'Bearer ' + id_token;
    //   } else {
    //     localStorage.removeItem('id_token');
    //   }
    // }
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

  parseHash();
});