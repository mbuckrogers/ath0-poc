window.addEventListener('load', function() {
  var lock = new Auth0Lock(AUTH0_CLIENT_ID, AUTH0_DOMAIN, {
    theme: {
      logo: "cropped-logo2.png",
      primaryColor: "#b81b1c"
    },
    languageDictionary: {
      title: "E-Medicus"
    },
    rememberLastLogin: false,
    allowForgotPassword: true,
    allowSignUp: true,
    auth: {
        params: {scope: 'openid email user_metadata'}
      }
  });
  
  window.onbeforeunload = function() {
	  console.log('Logout as well');
	  logout();
	}

  document.getElementById('btn-login').addEventListener('click', function() {
    lock.show();
  });

  document.getElementById('btn-logout').addEventListener('click', function() {
    logout();
  })
  
  document.getElementById('btn-public').addEventListener('click', function() {
    callapi('unsecured');
  })
  
  document.getElementById('btn-secured').addEventListener('click', function() {
    callapi('secured');
  })
 

  lock.on("authenticated", function(authResult) {
    lock.getProfile(authResult.idToken, function(error, profile) {
      if (error) {
        // Handle error
        return;
      }
      localStorage.setItem('id_token', authResult.idToken);
      display_user_profile(profile);
    });
  });

  var parseHash = function() {
    var id_token = localStorage.getItem('id_token');
    if (id_token) {
      lock.getProfile(id_token, function (err, profile) {
        if (err) {
          return alert('There was an error getting the profile: ' + err.message);
        }
        display_user_profile(profile);
      });
    }
  };

  var display_user_profile = function(profile) {
    document.getElementById('nickname').textContent = profile.nickname;
    document.getElementById('btn-login').style.display = "none";
    document.getElementById('avatar').src = profile.picture;
    document.getElementById('avatar').style.display = "inline-block";
    document.getElementById('btn-logout').style.display = "inline-block";
  };

  var logout = function() {
    localStorage.removeItem('id_token');
    lock.logout();
    window.location.href = "/";
  };
  
  var tokenExpires = setInterval(myTimer, 10000);

  function myTimer() {
	  var id_token = localStorage.getItem('id_token');
	  if(id_token) {
		  var decoded = jwt_decode(id_token);
		  if(decoded.exp * 1000 > new Date().valueOf()) {
			  $("#token-expires").html("Token expires in " + (decoded.exp * 1000 - new Date().valueOf()) + " [ms]");
		  } else {
			  localStorage.removeItem('id_token');
		  }
	  }
  }
  
  
  var callapi = function(uri) {
	  var id_token = localStorage.getItem('id_token');
	  var headers = {};
	  if(id_token) {
		  var decoded = jwt_decode(id_token);
		  // Has the token expired?
		  if(decoded.exp * 1000 > new Date().valueOf()) {
			  headers = {'Authorization':  'Bearer ' + id_token};
		  } else {
			  localStorage.removeItem('id_token');
		  }
	  }
	  
	  $.ajax({url: uri, 
		  headers: headers,
		  success: function(result){
			  $("#api-result").html(result);
          },
          error: function(jqXHR, textStatus, errorThrown){
        	  $("#api-result").html(jqXHR.status);
          }
    });
  };

  parseHash();
});