document.querySelector("html").ondragover = function (e) {
    e.preventDefault()
    e.stopPropagation()
}

document.querySelector("html").ondrop = function (e) {
    e.preventDefault()
    e.stopPropagation()
}

let uploadBox = document.querySelector("#uploadBox")

uploadBox.ondragenter = function (e) {
    e.stopPropagation()
    e.preventDefault()
}

uploadBox.ondragover = function (e) {
    e.stopPropagation()
    e.preventDefault()
}

uploadBox.ondragleave = function (e) {
    e.stopPropagation()
    e.preventDefault()
}

let uuids = []

uploadBox.ondrop = async function (e) {
    let tip = document.getElementById("dragTip");
    if (typeof (tip) != 'undefined' && tip != null) {
        tip.remove()
    }

    console.log("Images dropped for upload")
    e.stopPropagation()
    e.preventDefault()

    const files = e.dataTransfer.files;
    console.log(files)

    const fd = new FormData()

    for (let i = 0; i < files.length; i++) {
        fd.append('file', files[i])
        console.log(files[i])
    }

    fetch("/upload", {method: "post", body: fd})
        .then(response => response.text())
        .then(data => {
            let uuidsObject = JSON.parse(data)
            uuids = JSON.parse(uuidsObject["uuids"])
            load(uuids)
        })
        .catch(error => console.log(error))

    //let json = await fetch("/getImages", { method: "get" }).then(response => response.json())
    //let images = JSON.parse(json["images"])
    //uploadBox.innerHTML += `<div class="uploadedImage"><button class="deleteButton">X</button><img class="imageDiv" src="${URL.createObjectURL(files[i])}" alt="image"/></div>`
}

function load() {
    for (let i = 0; i < uuids.length; i++) {
        uploadBox.innerHTML += `<div class="uploadedImage">
                                            <img class="imageDiv" src="getImage?uuid=${uuids[i]}.jpg" alt="image">
                                                <button id="delete${uuids[i]}" class="deleteButton">X</button>
                                            </img>
                                        </div>`
    }

    for (let i = 0; i < uuids.length; i++) {
        document.getElementById("delete" + uuids[i]).addEventListener("click", () => remove(uuids[i]))
    }
}

function remove(uuidToDelete) {
    uploadBox.innerHTML = ""
    const index = uuids.indexOf(uuidToDelete);
    if (index > -1) {
        uuids.splice(index, 1)
        load()
    }
}

document.getElementById("saveButton").addEventListener("click", async () => {
    let text = await saveAsync()
    console.log(text)
})

saveAsync = async () => {
    let response = await fetch("/saveUpload", {method: "post", body: uuids })
    uuids = []
    uploadBox.innerHTML = ""
    load()
    alert("Images saved!")

    if (!response.ok) {
        return response.status
    } else {
        return await response.text()
    }
}