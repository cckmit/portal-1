function ServerResponse (resp) {


    this.process = function (flags) {

        var eflags = $.extend({},flags, {onFailRedirect : true, message : true});

        if (eflags.message && typeof(resp.message) === 'string') {
            alert (resp.message);
        }

        if (typeof (eflags.error) === 'function') {
            eflags.error(resp);
        }

        var checkRedir = resp.isOk || eflags.onFailRedirect;

        if (checkRedir && typeof(resp.redirect) === 'string') {
            window.location.replace(resp.redirect);
            return;
        }

        if (typeof (eflags.complete) === 'function') {
            eflags.complete(resp);
        }
    }

    return this;
}

$(document.body).ready(function () {

    //setup default ajax-error handler
    $( document ).ajaxError(function(event, jqxhr, settings, thrownError) {
        alert ("internal server error: " + thrownError);
    });

    // sign-in function
   $(".signInForm .submitBlock").click(function () {

       var req = {
           ulogin : $(".signInForm input[name=ulogin]").val(),
           upass : $(".signInForm input[name=upass]").val()
       };

       var message = {
           url : $(this).attr('action'),
           method : "POST",
           data : req,
           dataType : "json",
           success : function (resp) {

               new ServerResponse (resp).process ({
                   onFailRedirect : false,
                   error : function () {
                       $(".signInForm input[name=upass]").val("");
                   }
               });
           }
       };

       $.ajax(message);

   });

});