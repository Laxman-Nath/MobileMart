
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


});






//console.log("Hello");


