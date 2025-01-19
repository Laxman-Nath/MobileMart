
$(document).ready(function(event) {

	function preventBack() {
		window.history.forward();
	}

	setTimeout(preventBack, -1000);

	$(window).on('unload', function() {

		null;
	});

	$("#submit-button").removeAttr("disabled");
	console.log("This is js file.......");

	const activeLink = sessionStorage.getItem('activeNavLink');
	if (activeLink) {
		console.log("This is old tab");
		$('.nav-link.activenav').removeClass('activenav');
		$(`.nav-link[href = "${activeLink}"]`).addClass('activenav');

	}
	if (window.location.pathname === '/user' || window.location.pathname === '/user/' || window.location.pathname === '/') {
		console.log("This is user tab");
		$('.nav-link.activenav').removeClass('activenav');
		$('.nav-link[href="/"]').addClass('activenav');
	}
	if (window.location.pathname === '/login' && (window.location.search === '?logout' || window.location.search === '?error')) {
		console.log("This is logout tab");
		$('.nav-link.activenav').removeClass('activenav');
		$('.nav-link[href="/login"]').addClass('activenav');
	}

	if (window.location.pathname === '/login') {
		$('.nav-link.activenav').removeClass('activenav');
		$('.nav-link[href="/login"]').addClass('activenav');
	}
	if (window.location.pathname === '/register') {
		$('.nav-link.activenav').removeClass('activenav');
		$('.nav-link[href="/register"]').addClass('activenav');
	}







	$('.nav-link').on('click', function(event) {
		event.preventDefault();
		sessionStorage.setItem('activeNavLink', $(this).attr('href'));
		window.location.href = $(this).attr('href');
	});


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




	$('.PasswordIcon').on('click', function() {
		let passwordValue = $('.LoginPassword');
		let passwordType = passwordValue.attr('type');
		if (passwordType === 'password') {
			passwordValue.attr('type', 'text');
			$(this).find('i').removeClass('fas fa-eye').addClass('fas fa-eye-slash');
		}
		else {
			passwordValue.attr('type', 'password');
			$(this).find('i').removeClass('fas fa-eye-slash').addClass('fas fa-eye');

		}


	});









	$(document).on('click', '.delete-btn', function() {
		console.log("Button clicked");

		var itemId = $(this).data('item-id');
		var orderId = $(this).data('order-id');
		var customerId = $(this).data('customer-id');
		var title = $(this).attr('title');
		var currentUrl = window.location.pathname;

		console.log(title);

		var url, method, confirmMessage, message;

		// Determine action based on button title
		switch (title) {
			case 'Delete':
				url = currentUrl.startsWith("/admin")
					? `/admin/deleteProductFromOrder/${itemId}?orderId=${orderId}`
					: `/user/deleteProductFromOrder/${itemId}?orderId=${orderId}`;
				method = 'DELETE';
				confirmMessage = "Are you sure you want to delete this product? This action cannot be undone.";
				message = "Successfully deleted!";
				break;

			case 'add':
				url = currentUrl.startsWith("/admin")
					? `/admin/increaseProductFromOrder/${itemId}?orderId=${orderId}`
					: `/user/increaseProductFromOrder/${itemId}?orderId=${orderId}`;
				method = 'PATCH';
				confirmMessage = "Are you sure you want to increase the number of this product? This action cannot be undone.";
				message = "Successfully added!";
				break;

			case 'subtract':
				url = currentUrl.startsWith("/admin")
					? `/admin/decreaseProductFromOrder/${itemId}?orderId=${orderId}`
					: `/user/decreaseProductFromOrder/${itemId}?orderId=${orderId}`;
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
					alert(`Action successfully completed: ${response.message || message}`);

					// Redirect based on user context
					var redirectUrl = currentUrl.startsWith("/admin")
						? `/admin/editorder/${orderId}`
						: `/user/editorder/${orderId}/${customerId}`;

					window.location.href = redirectUrl; // Redirect to the appropriate order edit page
				},
				error: function(xhr) {
					let errorMessage = 'An error occurred while trying to process the request.';
					if (xhr.responseJSON && xhr.responseJSON.message) {
						errorMessage = xhr.responseJSON.message; // Use server-provided error message
					}
					console.error('Error:', errorMessage);
					alert(errorMessage); // Show the error message to the user
				}
			});
		}
	});







	console.log("Script is running");

	var socket = new SockJS('/unlock-notifications');
	var stompClient = Stomp.over(socket);

	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);

		stompClient.subscribe('/topic/unlockMessage', function(message) {
			console.log('Received message: ' + message.body);
			$('#unlockMessage').text(message.body);
			$('#error').text('');
		});
	}, function(error) {
		console.error('WebSocket error: ' + error);
	});

	// hide/show admin dashboard


	var el = $('#wrapper');
	var toggleButton = $('#menu-toggle');

	toggleButton.on('click', function() {
		el.toggleClass('toggled');
		console.log($('#wrapper').attr('class'));
	});

	////////////---registration form-----////////




	// Function to update districts based on selected state
	function updateDistricts() {
		const districtsData = {
			"Province No. 1": ["Bhojpur", "Dhankuta", "Ilam", "Jhapa", "Khotang", "Morang", "Okhaldhunga", "Panchthar", "Sankhuwasabha", "Solukhumbu", "Sunsari", "Taplejung", "Terhathum", "Udayapur"],
			"Province No. 2": ["Bara", "Dhanusha", "Mahottari", "Parsa", "Rautahat", "Saptari", "Sarlahi", "Siraha"],
			"Bagmati Province": ["Bhaktapur", "Chitwan", "Dhading", "Dolakha", "Kathmandu", "Kavrepalanchok", "Lalitpur", "Makwanpur", "Nuwakot", "Ramechhap", "Rasuwa", "Sindhuli", "Sindhupalchok"],
			"Gandaki Province": ["Baglung", "Gorkha", "Kaski", "Lamjung", "Manang", "Mustang", "Myagdi", "Nawalpur", "Parbat", "Syangja", "Tanahun"],
			"Lumbini Province": ["Arghakhanchi", "Banke", "Bardiya", "Dang", "Gulmi", "Kapilvastu", "Palpa", "Pyuthan", "Rolpa", "Rukum East", "Rupandehi", "Nawalparasi West"],
			"Karnali Province": ["Dailekh", "Dolpa", "Humla", "Jajarkot", "Jumla", "Kalikot", "Mugu", "Rukum West", "Salyan", "Surkhet"],
			"Sudurpaschim Province": ["Achham", "Baitadi", "Bajhang", "Bajura", "Dadeldhura", "Darchula", "Doti", "Kailali", "Kanchanpur"]
		};

		// Handle state change event
		$("#state").on("change", function() {
			console.log("inside update district");
			const selectedState = $("#state").val();
			console.log("Selected state is:", selectedState);
			const $districtSelect = $("#district");
			$districtSelect.html("<option value=''>Select District</option>");

			// Populate district options based on selected state
			if (selectedState && districtsData[selectedState]) {
				districtsData[selectedState].forEach(district => {
					$districtSelect.append(`<option value="${district}">${district}</option>`);
				});
			}

			// Set previously selected district if it exists
			const selectedDistrict = $("#state").data("district");
			console.log("The selected district is:", selectedDistrict);
			if (selectedDistrict) {
				$districtSelect.val(selectedDistrict);
			}
		});

		// Trigger state change on page load if there is a pre-selected state
		const selectedState = $("#state").val();
		if (selectedState) {
			$("#state").trigger('change');
		}
	}

	// General validation functions
	function validateField(inputSelector, errorSelector, validationFunc) {
		$(inputSelector).on("input", function() {
			const inputValue = $(this).val().trim();
			const errorMessage = validationFunc(inputValue);
			const errorElement = $(errorSelector);
			errorElement.text(errorMessage).toggle(!!errorMessage);
		});

		// Show error when the user clicks (focus) on the input field
		$(inputSelector).on("focus", function() {
			const inputValue = $(this).val().trim();
			const errorMessage = validationFunc(inputValue);
			const errorElement = $(errorSelector);
			errorElement.text(errorMessage).show();
		});

		// Optional: Hide error on blur (when the user leaves the input field)
		$(inputSelector).on("blur", function() {
			const inputValue = $(this).val().trim();
			const errorMessage = validationFunc(inputValue);
			const errorElement = $(errorSelector);
			if (!errorMessage) {
				errorElement.hide(); // Hide error if there's no error message
			}
		});
	}

	// Example validation functions
	function validateName(value) {

		const namePattern = /^[A-Za-z\s]+$/;
		const nameParts = value.split(" ");
		if (nameParts.length < 2) {
			return "Please enter your full name (first and last name).";
		}
		if (nameParts.some(name => name.length < 3 || !namePattern.test(name))) {
			return "Each name must be at least 3 letters and cannot contain numbers.";
		}
		return "";
	}

	function validateMobileNumber(value) {
		const phonePattern = /^[0-9]{10}$/;
		if (!phonePattern.test(value)) {
			return "Mobile number must be exactly 10 digits.";
		}
		return "";
	}

	function validateEmail(value) {
		const emailPattern = /^[^\s@]+@gmail\.com$/;
		if (!emailPattern.test(value)) {
			return "Email must be in the format username@gmail.com.";
		}
		return "";
	}

	function validatePassword(value) {
		if (value.length < 8) {
			return "Password must be at least 8 characters.";
		}
		return "";
	}

	function validateConfirmPassword(password, confirmPassword) {
		if (password !== confirmPassword) {
			return "Passwords do not match.";
		}
		return "";
	}

	function validateRequired(value) {
		return value ? "" : "This field is required.";
	}

	function validateNumber(value) {
		return /^[1-9]\d*$/.test(value) ? "" : "Please enter a valid number greater than 0.";
	}


	function validateImage(value) {
		const fileInput = $("#file")[0].files[0];
		const validExtensions = /(\.jpg|\.jpeg|\.png|\.gif)$/i;
		if (!fileInput) return "Please upload an image file.";
		if (!validExtensions.exec(fileInput.name)) return "Invalid file type.";
		if (fileInput.size > 2097152) return "File size must be less than 2 MB.";
		return "";
	}

	function validateProductName(value) {
		if (!value) {
			return "Product name is required.";
		}
		if (value.length < 3) {
			return "Product name should be at least 3 characters long.";
		}
		const validCharacters = /^[A-Za-z0-9\s\-\_\.]+$/; // Letters, numbers, spaces, dashes, underscores, and dots
		if (!validCharacters.test(value)) {
			return "Product name can only contain letters, numbers, spaces, dashes, underscores, and dots.";
		}
		return "";
	}

	function validateCategoryName(value) {
		if (!value) {
			return "Category name is required.";
		}
		if (value.length < 3) {
			return "Category name should be at least 3 characters long.";
		}
		const validCharacters = /^[A-Za-z0-9\s\-\_\.]+$/; // Letters, numbers, spaces, dashes, underscores, and dots
		if (!validCharacters.test(value)) {
			return "Category name can only contain letters, numbers, spaces, dashes, underscores, and dots.";
		}
		return "";
	}



	function validatePrice(value) {
		if (!value) {
			return "Price is required.";
		}
		if (value <= 0) {
			return "Price must be a positive number.";
		}
		return "";
	}

	function validateDiscount(value) {
		if (value && (value < 0 || value > 100)) {
			return "Discount must be a number between 0 and 100.";
		}
		return "";
	}

	function validateQuantity(value) {
		if (!value) {
			return "Quantity is required.";
		}
		if (isNaN(value) || value <= 0) {
			return "Quantity must be a positive number.";
		}
		return "";
	}

	function validatePasswordFields(oldPasswordSelector, newPasswordSelector, reNewPasswordSelector) {
		// Old Password Validation
		validateField(oldPasswordSelector, '#old-password-error', function(value) {
			if (value.trim() === '') {
				return "Old password cannot be empty.";
			}
			if (value.length < 8) {
				return "Old password must be at least 8 characters.";
			}
			return ""; // No error
		});

		// New Password Validation
		validateField(newPasswordSelector, '#new-password-error', function(value) {
			if (value.length < 8) {
				return "New password must be at least 8 characters.";
			}
			return ""; // No error
		});

		// Confirm New Password Validation
		validateField(reNewPasswordSelector, '#re-new-password-error', function(value) {
			const newPassword = $(newPasswordSelector).val().trim();
			if (value !== newPassword) {
				return "Confirm password does not match the new password.";
			}
			return ""; // No error
		});
	}


	// Form submission validation
	$("form").on("submit", function(event) {
		let error = "";
		let valid = true;
		$(".error").text("").hide(); // Clear all error messages

		$(this).find(":input").each(function() {
			const input = $(this);
			const inputName = input.attr("name");
			const inputType = input.attr("type");
			const id = $(`[name="${inputName}"]`).attr('id');
			const inputValue = input.val().trim();
			const inputError = $(`#${inputName}-error`);

			// Check required fields
			if (input.attr("required") && !inputValue) {
				alert("required is invalid");
				inputError.text(`${inputName.replace(/([A-Z])/g, ' $1').toLowerCase()} is required.`).show();
				valid = false;
			}

			// number validation

			if (inputType === "number" && inputName == "quantity" && validateNumber(inputValue)) {
				//alert("invalid number");
				inputError.text(validateNumber(inputValue)).show();
				valid = false;
				error = validateNumber(inputValue);
			}

			if (inputType === "number" && inputName == "ward" && validateNumber(inputValue)) {
				//alert("invalid number");
				inputError.text(validateNumber(inputValue)).show();
				valid = false;
				error = validateNumber(inputValue);
			}



			// Custom validations
			if (inputName === "name" && validateName(inputValue) && id != "pname" && id != "categoryname") {

				inputError.text(validateName(inputValue)).show();
				valid = false;
				error = validateName(inputValue);
			}
			if (inputName === "phone" && validateMobileNumber(inputValue)) {
			//	alert("phone is invalid");
				inputError.text(validateMobileNumber(inputValue)).show();
				valid = false;
				error = validateMobileNumber(inputValue);
			}
			if (inputName === "email" && validateEmail(inputValue)) {
				//alert("email is invalid");
				inputError.text(validateEmail(inputValue)).show();
				valid = false;
				error = validateEmail(inputValue);
			}
			if (inputName === "password" && validatePassword(inputValue)) {
				//alert("password is invalid");
				inputError.text(validatePassword(inputValue)).show();
				valid = false;
				error = validatePassword(inputValue);
			}
			if (inputName === "cpassword" && validateConfirmPassword($('input[name="password"]').val(), $('input[name="cpassword"]').val())) {

				inputError.text(validateConfirmPassword($('input[name="password"]').val(), $('input[name="cpassword"]').val())).show();
				valid = false;
				error = validateConfirmPassword($('input[name="password"]').val(), $('input[name="cpassword"]').val());
			}
			if (inputName == "image" && validateImage(inputValue)) {
				//alert("image is invalid");
				inputError.text(validateImage(inputValue)).show();
				valid = false;
				error = validateImage(inputValue);
			}

			if (inputName === "name" && id == "pname" && validateProductName(inputValue)) {
				//alert("Product name is invalid");
				inputError.text(validateProductName(inputValue)).show();
				valid = false;
				error = validateProductName(inputValue);
			}
			if (inputName === "price" && validatePrice(inputValue)) {
				//alert("Price is invalid");
				inputError.text(validatePrice(inputValue)).show();
				valid = false;
				error = validatePrice(inputValue);
			}
			if (inputName === "discountPercent" && validateDiscount(inputValue)) {
				//alert("discount is invalid");
				inputError.text(validateDiscount(inputValue)).show();
				valid = false;
				error = validateDiscount(inputValue);
			}
			if (inputName === "quantity" && validateQuantity(inputValue)) {
				//	alert("quanity name is invalid");
				inputError.text(validateQuantity(inputValue)).show();
				valid = false;
				error = validateQuantity(inputValue);
			}

			if (inputName === "name" && id == "categoryname" && validateCategoryName(inputValue)) {
				//alert("Category name is invalid");
				$("#categoryname-error").text(validateProductName(inputValue)).show();
				valid = false;
				error = validateProductName(inputValue);
			}

			if (id === "oldpassword" && validatePasswordFields('#oldpassword', '#newpassword', '#renewpassword')) {
				$("#old-password-error").text(validatePasswordFields('#oldpassword', '#newpassword', '#renewpassword'));
				valid = false;
				error = validatePasswordFields('#oldpassword', '#newpassword', '#renewpassword');
			}
			if (id === "newpassword" && validatePasswordFields('#oldpassword', '#newpassword', '#renewpassword')) {
				$("#new-password-error").text(validatePasswordFields('#oldpassword', '#newpassword', '#renewpassword'));
				valid = false;
				error = validatePasswordFields('#oldpassword', '#newpassword', '#renewpassword');
			}
			if (id === "renewpassword" && validateConfirmPassword($('#newpassword').val(), $('#renewpassword').val())) {
				$("#re-new-password-error").text(validateConfirmPassword($('#newpassword').val(), $('#renewpassword').val()));
				valid = false;
				error = validateConfirmPassword($('#newpassword').val(), $('#renewpassword').val());
			}



			// Hide error messages if the field is valid
			if (valid) {
				inputError.hide();
			}
		});

		if (!valid) {
			alert(error);
			event.preventDefault(); // Prevent form submission if validation fails
		}
	});

	//calling update district method
	updateDistricts();

	// Initialize the districts and validation functions
	updateDistricts();
	validateField("#name", "#name-error", validateName);
	validateField("#phone", "#phone-error", validateMobileNumber);
	validateField("input[name='ward']", "#ward-error", validateNumber);
	validateField("#email", "#email-error", validateEmail);
	validateField('input[name="password"]', "#password-error", validatePassword);
	validateField('input[name="cpassword"]', "#password-error", () => validateConfirmPassword($('input[name="password"]').val(), $('input[name="cpassword"]').val()));
	validateField("#muncipility", "#muncipility-error", validateRequired);
	validateField("#ward", "#ward-error", validateRequired);
	validateField("#tole", "#tole-error", validateRequired);

	// add product form
	validateField("#pname", "#pname-error", validateRequired);
	validateField("#status", "#status-error", validateRequired);
	validateField("input[name='dimension']", "#dimension-error", validateRequired);
	validateField("input[name='weight']", "#weight-error", validateRequired);
	validateField("input[name='build']", "#build-error", validateRequired);
	validateField("input[name='sim']", "#sim-error", validateRequired);
	validateField("input[name='color']", "#color-error", validateRequired);
	validateField("input[name='type']", "#type-error", validateRequired);
	validateField("input[name='size']", "#size-error", validateRequired);
	validateField("input[name='resolution']", "#resolution-error", validateRequired);
	validateField("input[name='os']", "#os-error", validateRequired);
	validateField("input[name='chipSet']", "#chipSet-error", validateRequired);
	validateField("input[name='cpu']", "#cpu-error", validateRequired);
	validateField("input[name='gpu']", "#gpu-error", validateRequired);
	validateField("input[name='cardSlot']", "#cardSlot-error", validateRequired);
	validateField("input[name='internal']", "#internal-error", validateRequired);
	validateField("input[name='bFeatures']", "#bFeatures-error", validateRequired);
	validateField("input[name='bVideo']", "#bVideo-error", validateRequired);
	validateField("input[name='fFeatures']", "#fFeatures-error", validateRequired);
	validateField("input[name='fVideo']", "#fVideo-error", validateRequired);
	validateField("input[name='loudSpeaker']", "#loudSpeaker-error", validateRequired);
	validateField("input[name='sFeatures']", "#sFeatures-error", validateRequired);
	validateField("input[name='bType']", "#bType-error", validateRequired);
	validateField("input[name='charging']", "#charging-error", validateRequired);
	validateField("input[name='cFeatures']", "#cFeatures-error", validateRequired);
	validateField("input[name='security']", "#security-error", validateRequired);
	validateField("#category", "#category-error", validateRequired);
	validateField("#policySelect", "#policy-error", validateRequired);
	validateField("input[name='price']", "#price-error", validateNumber);
	validateField("input[name='discountPercent']", "#discount-error", validateNumber);
	validateField("input[name='quantity']", "#quantity-error", validateNumber);
	validateField("#file", "#file-error", validateImage);
	validateField("[name='name']", "#pname-error", validateProductName);
	validateField("[name='price']", "#price-error", validatePrice);
	validateField("[name='discountPercent']", "#discount-error", validateDiscount);
	validateField("[name='quantity']", "#quantity-error", validateQuantity);



	//category form
	validateField("#categoryname", "#categoryname-error", validateRequired);
	validateField("#status", "#status-error", validateRequired);
	validateField("[name='name']", "#categoryname-error", validateCategoryName);

	// change password validation
	validatePasswordFields('#oldpassword', '#newpassword', '#renewpassword');



	///////////change pasword //////////////////////////
	// Function to validate old password via AJAX

	// Function to validate password fields


	// Call the function for both Admin and Customer forms




	// Prevent form submission if validation fails
	//if (!isValid) {
	//	event.preventDefault();
	//}

});







//console.log("Hello");


