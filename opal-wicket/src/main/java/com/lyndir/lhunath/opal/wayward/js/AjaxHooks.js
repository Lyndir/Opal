AjaxHooks = {
    // Functions to call after AJAX responses.
	postAjaxCallbacks: [],
	// IDs of DOM elements that were added to the AjaxResponseTarget
	updatedDomIds: [],

	// Register a function to call after AJAX requests.
	registerPostAjax: function(fn) {

		this.postAjaxCallbacks.push(fn);
	},

	// Invoked by Wicket during the AJAX response.  Populates the updatedDomIds.
	setUpdatedDomIds: function(updatedDomIds) {

		this.updatedDomIds = updatedDomIds;
	},

	// Fire all the registered post-AJAX callbacks passing the updated DOM elements as defined by the previous handle call.
	firePostAjax: function() {

		if (AjaxHooks.updatedDomIds.length) {
			// Select the updated elements by ID.
			var selector = AjaxHooks.updatedDomIds.join(',');

			// Invoke the callbacks passing the elements.
			var updatedDomElements = $(selector);
			$.each(AjaxHooks.postAjaxCallbacks, function() {
				this(updatedDomElements);
			});

			// Clean up the list of updated IDs.
			AjaxHooks.updatedDomIds = [];
		}
	}
};

// Register AjaxHooks
$(document).ready(function() {

	if (Wicket.Ajax)
	    Wicket.Ajax.registerPostCallHandler(AjaxHooks.firePostAjax);
});
