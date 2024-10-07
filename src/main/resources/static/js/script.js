
$(document).ready(function() {
	// Category

	$(".category-carousel").owlCarousel({
		loop: true,
		nav: true,
		dots: false,

		responsive: {
			0: {
				items: 1,
			},
			600: {
				items: 3,
			},
			1000: {
				items: 5,
			}
		}

	});

	$(".latest-carousel").owlCarousel({
		loop: true,
		nav: true,
		dots: false,

		responsive: {
			0: {
				items: 1,
			},
			600: {
				items: 3,
			},
			1000: {
				items: 5,
			}
		}

	});

	$(".onsale .sale-carousel").owlCarousel({
		dots: true,
		items: 1

	});




	$('#PasswordIcon').on('click', function() {
		let passwordValue = $('#LoginPassword');
		let passwordType = passwordValue.attr('type');
		if (passwordType === 'password') {
			passwordValue.attr('type', 'text');
			$(this).find('i').removeClass('fas fa-eye-slash').addClass('fas fa-eye');
		}
		else {
			passwordValue.attr('type', 'password');
			$(this).find('i').removeClass('fas fa-eye').addClass('fas fa-eye-slash');
		}


	});




	$('.nav-link').on('click', function(event) {

		event.preventDefault();
		$('.nav-link.active').removeClass('active');
		$(this).addClass('active');
		window.location.href = $(this).attr('href');
	});


	/*	$(document).on('submit', '.submit-form', function(event) {
			event.preventDefault();
			alert("Form submitted");
	
	
		}); */


	/*	$(".list-group-item").on('click',function(event){
			$(".list-group-item").removeClass("active");
			$(this).addClass("active");
		});
		*/


		$(document).on('click', '.delete-btn', function() {
		    console.log("Button clicked");
		    
		    var itemId = $(this).data('item-id');
		    var orderId = $(this).data('order-id');
		    var title = $(this).attr('title');
		    var currentUrl = window.location.pathname;

		    console.log(title);
		    
		    var url, method, confirmMessage, message;

		    // Determine action based on button title
		    switch (title) {
		        case 'Delete':
		            url = currentUrl.startsWith("/admin") 
		                ? `/admin/deleteProductFromOrder/${itemId}?orderId=${orderId}` 
		                : `/user/deleteProductFromOrder/${itemId}?orderId=${orderId}`; // Adjust URL for user
		            method = 'DELETE';
		            confirmMessage = "Are you sure you want to delete this product? This action cannot be undone.";
		            message = "Successfully deleted!";
		            break;

		        case 'add':
		            url = currentUrl.startsWith("/admin") 
		                ? `/admin/increaseProductFromOrder/${itemId}?orderId=${orderId}` 
		                : `/user/increaseProductFromOrder/${itemId}?orderId=${orderId}`; // Adjust URL for user
		            method = 'PATCH';
		            confirmMessage = "Are you sure you want to increase the number of this product? This action cannot be undone.";
		            message = "Successfully added!";
		            break;

		        case 'subtract':
		            url = currentUrl.startsWith("/admin") 
		                ? `/admin/decreaseProductFromOrder/${itemId}?orderId=${orderId}` 
		                : `/user/decreaseProductFromOrder/${itemId}?orderId=${orderId}`; // Adjust URL for user
		            method = 'PATCH';
		            confirmMessage = "Are you sure you want to decrease the number of this product? This action cannot be undone.";
		            message = "Successfully decreased!";
		            break;

		        default:
		            console.error("Unknown action title: ", title);
		            return;
		    }

		    // Confirm action
		    if (confirm(confirmMessage)) {
		        $.ajax({
		            url: url,
		            method: method,
		            contentType: 'application/json',
		            headers: {
		                'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
		            },
		            data: JSON.stringify({ orderId: orderId }),
		            success: function(response) {
		                alert(`Action successfully completed: ${message}`);
		                
		                // Redirect based on whether the user is an admin or a regular user
		                var redirectUrl;
		                if (currentUrl.startsWith("/admin")) {
		                    redirectUrl = `/admin/editorder/${orderId}`; // Admin redirect
		                } else if (currentUrl.startsWith("/user")) {
		                    redirectUrl = `/user/editorder/${orderId}`; // User redirect
		                } else {
		                    console.error("Unexpected URL:", currentUrl);
		                    return; // Exit if the URL doesn't match expected paths
		                }
		                
		                window.location.href = redirectUrl; // Redirect to the appropriate order edit page
		            },
		            error: function(xhr, status, error) {
		                console.error('Error:', error);
		                alert('An error occurred while trying to process the request.');
		            }
		        });
		    }
		});


	var el = $('#wrapper');
	var toggleButton = $('#menu-toggle');

	toggleButton.on('click', function() {
		el.toggleClass('toggled');
	});


	//--------------------------register validation-------------------------------//
	$(document).ready(function () {
    // Update districts based on selected state
    $("#state").on("change", function () {
        const districtsData = {
                "Province No. 1": [
                    "Bhojpur", "Dhankuta", "Ilam", "Jhapa", "Khotang", "Morang", 
                    "Okhaldhunga", "Panchthar", "Sankhuwasabha", "Solukhumbu", 
                    "Sunsari", "Taplejung", "Terhathum", "Udayapur"
                ],
                "Province No. 2": [
                    "Bara", "Dhanusha", "Mahottari", "Parsa", "Rautahat", 
                    "Saptari", "Sarlahi", "Siraha"
                ],
                "Bagmati Province": [
                    "Bhaktapur", "Chitwan", "Dhading", "Dolakha", "Kathmandu", 
                    "Kavrepalanchok", "Lalitpur", "Makwanpur", "Nuwakot", 
                    "Ramechhap", "Rasuwa", "Sindhuli", "Sindhupalchok"
                ],
                "Gandaki Province": [
                    "Baglung", "Gorkha", "Kaski", "Lamjung", "Manang", 
                    "Mustang", "Myagdi", "Nawalpur", "Parbat", 
                    "Syangja", "Tanahun"
                ],
                "Lumbini Province": [
                    "Arghakhanchi", "Banke", "Bardiya", "Dang", "Gulmi", 
                    "Kapilvastu", "Palpa", "Pyuthan", "Rolpa", 
                    "Rukum East", "Rupandehi", "Nawalparasi West"
                ],
                "Karnali Province": [
                    "Dailekh", "Dolpa", "Humla", "Jajarkot", "Jumla", 
                    "Kalikot", "Mugu", "Rukum West", "Salyan", "Surkhet"
                ],
                "Sudurpashchim Province": [
                    "Achham", "Baitadi", "Bajhang", "Bajura", "Dadeldhura", 
                    "Darchula", "Doti", "Kailali", "Kanchanpur"
                ]
            };
            

        let selectedState = $(this).val();
        let $districtSelect = $("#district");
        $districtSelect.html("<option value=''>Select District</option>");

        if (selectedState && districtsData[selectedState]) {
            districtsData[selectedState].forEach(function (district) {
                $districtSelect.append(`<option value="${district}">${district}</option>`);
            });
        }
    });

    // Validate full name
    $("#name").on("input", function () {
        let nameInput = $(this).val().trim().split(" ");
        let nameError = $("#name-error");
        let namePattern = /^[A-Za-z\s]+$/;

        if (nameInput.length < 2) {
            nameError.text("Please enter your full name (first and last name).");
        } else {
            let valid = nameInput.every(name => name.length >= 3 && namePattern.test(name));
            if (!valid) {
                nameError.text("Each name must be at least 3 letters and cannot contain numbers.");
            } else {
                nameError.text("");
            }
        }
    });

    // Validate mobile number
    $("#phone").on("input", function () {
        let phoneInput = $(this).val();
        let phoneError = $("#phone-error");
        let phonePattern = /^[0-9]{10}$/;

        if (!phonePattern.test(phoneInput)) {
            phoneError.text("Mobile number must be exactly 10 digits.");
        } else {
            phoneError.text("");
        }
    });

    // Validate email format
    $("#email").on("input", function () {
        let emailInput = $(this).val();
        let emailError = $("#email-error");
        let emailPattern = /^[^\s@]+@[^\s@]+\.[a-zA-Z]{3,}$/;

        if (!emailPattern.test(emailInput)) {
            emailError.text("Email must be in the format username@gmail.com.");
        } else {
            emailError.text("");
        }
    });

    // Validate password
    $('input[name="password"]').on("input", function () {
        let passwordInput = $(this).val();
        let passwordError = $("#password-error");

        if (passwordInput.length < 8) {
            passwordError.text("Password must be at least 8 characters.");
        } else {
            passwordError.text("");
        }
    });

    // Validate password confirmation
    $('input[name="cpassword"]').on("input", function () {
        let passwordInput = $('input[name="password"]').val();
        let confirmPasswordInput = $(this).val();
        let passwordError = $("#password-error");

        if (passwordInput !== confirmPasswordInput) {
            passwordError.text("Passwords do not match.");
        } else {
            passwordError.text("");
        }
    });

    // Validate file upload
    $("#file").on("change", function () {
        let fileInput = this.files[0];
        let fileError = $("#file-error");
        let validExtensions = /(\.jpg|\.jpeg|\.png|\.gif)$/i;

        if (!validExtensions.exec($(this).val())) {
            fileError.text("Invalid file type. Only JPG, JPEG, PNG, or GIF files are allowed.");
        } else if (fileInput.size > 2097152) { // 2MB limit
            fileError.text("File size must be less than 2 MB.");
        } else {
            fileError.text("");
        }
    });

    // Validate the entire form on submit
    $("form").on("submit", function (event) {
        let valid = true;

        if ($("#name-error").text() || $("#phone-error").text() || $("#email-error").text() || $("#password-error").text() || $("#file-error").text()) {
            valid = false;
        }

        if (!valid) {
            event.preventDefault();
        }
    });
});


	// ------------------------------------------login validatiion---------------------------------------------------//
	$(document).ready(function () {
  // Improved Email validation regex pattern
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{3,}$/;

  // Email input validation on typing
  $("#form2Example17").on("input", function () {
    const email = $(this).val();
    if (!emailPattern.test(email)) {
      $("#emailError").show().text("Invalid email format.");
    } else {
      $("#emailError").hide();
    }
  });

  // Password input validation
  $("#LoginPassword").on("input", function () {
    const password = $(this).val();
    if (password.length < 8) {
      $("#passwordError")
        .show()
        .text("Password must be at least 8 characters.");
    } else {
      $("#passwordError").hide();
    }
  });

  // Login button click event
  $("#loginButton").click(function (event) {
    event.preventDefault(); // Prevent default form submission

    const username = $("#form2Example17").val();
    const password = $("#LoginPassword").val();

    // Validate empty inputs
    let valid = true;

    if (username === "") {
      $("#emailError").show().text("Email is required.");
      valid = false;
    }
    if (password === "") {
      $("#passwordError").show().text("Password is required.");
      valid = false;
    }

    if (valid) {
      // Send login request via AJAX if validation passes
      $.post(
        "/login",
        { username: username, password: password },
        function (response) {
          alert(response); // Handle login response
        }
      );
    }
  });
});




});






//console.log("Hello");


