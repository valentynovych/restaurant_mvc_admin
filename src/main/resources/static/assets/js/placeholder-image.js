function previewPlaceholder(url) {
    console.log(url)
    var images = document.querySelectorAll("img");
    for (var i = 0; i < images.length; i++) {
        if (!images[i].complete || images[i].naturalWidth === 0) {
            images[i].src = url.target;
        }
    }}