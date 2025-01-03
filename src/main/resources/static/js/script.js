
$(document).ready(function() {


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
						: `/user/editorder/${orderId}`;

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



	var el = $('#wrapper');
	var toggleButton = $('#menu-toggle');

	toggleButton.on('click', function() {
		el.toggleClass('toggled');
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
});







//console.log("Hello");


