$(document).ready(function() {

    $("#previewIconFile").on("change", previewImage);
    function previewImage() {
        if (this.files && this.files[0]) {
            const type = this.files[0].type;
            const reader = new FileReader();
            reader.onload = function (e) {
                var $previewIcon = $("#previewIcon");
                if (type === "image/jpeg" || type === "image/jpg" || type === "image/png") {
                    $previewIcon.attr("src", e.target.result);
                    $previewIcon.removeClass('is-invalid');
                    $('.error-message').remove();

                } else {
                    $previewIcon.addClass("is-invalid")
                        .after($('<p class="error-message text-center text-danger m-0">' +
                            'Вибраний файл не є зображенням у форматі jpeg, jpg або png.</p>'
                        ))
                }
            };
            reader.readAsDataURL(this.files[0]);
        }
    }

});