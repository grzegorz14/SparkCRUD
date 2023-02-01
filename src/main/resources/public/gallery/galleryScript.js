fetch("/getImages", {method: "post"})
    .then(response => response.text())
    .then(data => {
        console.log(data)
        let imagesObject = JSON.parse(data)
        let images = JSON.parse(imagesObject["imagesUuids"])

        let imagesText = ""

        for (let i = 0; i < images.length; i++) {
            imagesText += `<div class="galleryImage">
                                <img class="imageDiv" src="getImage?uuid=${images[i]}.jpg" alt="image">
                                    <a href="/editImage/editImage.html?uuid=${images[i]}" class="editLink">edit</a>
                                </img>
                            </div>`

        }
        if (imagesText === "") {
            imagesText = `<h2>Nothing here :(</h2>`
        }
        document.getElementById("galleryBox").innerHTML = imagesText
    })
    .catch(error => console.log(error))

