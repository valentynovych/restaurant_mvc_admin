$(document).ready(function() {

    $("#previewIconFile").on("change", previewImage);
    function previewImage() {
        if (this.files && this.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $("#previewIcon").attr("src", e.target.result);
            };
            reader.readAsDataURL(this.files[0]);
        }
    }
});