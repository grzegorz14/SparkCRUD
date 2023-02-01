loadCarsAsync = async () => {
    let json = await fetch("/json", { method: "get" }).then(response => response.json())
    let cars = JSON.parse(json["cars"])
    //console.log(cars)

    let carsList = ""

    for (let i = 0; i < cars.length; i++) {
        carsList += "<tr>" +
            `<td>${cars[i]["id"]}</td>` +
            `<td style="font-size: 12px">${cars[i]["uuid"]}</td>` +
            `<td style="margin-left: 5px">${cars[i]["model"]}</td>` +
            `<td style="margin-left: 10px">${cars[i]["date"]["year"]}</td>`

        let airbags = "";
        for (let j = 0; j < 4; j++) {
            airbags += cars[i]["airbags"][j]["description"] + ":" + cars[i]["airbags"][j]["value"] + "<br>"
        }
        carsList += `<td style="font-size: 12px; margin-left: 10px;">${airbags}</td>`

        carsList += `<td style="margin-left: 20px"><div class="colorDiv" style="background-color: ${cars[i]["color"]}"></div></td>` +
            `<td style="margin-left: 25px"><button class="deleteButton" id="delete${cars[i]["uuid"]}">Delete</button></td>` +
            `<td style="margin-left: 25px"><button class="editButton" id="edit${cars[i]["uuid"]}">Edit</button></td>`

        carsList += `<td style="margin-left: 30px" class="link"><a href="upload?uuid=${cars[i]["uuid"]}">Upload</a></td>` +
            `<td style="margin-left: 30px" class="link"><a href="gallery?uuid=${cars[i]["uuid"]}">Gallery</a></td>` +
            "</tr>"
    }
    document.getElementById("carsList").innerHTML = carsList

    for (let i = 0; i < cars.length; i++) {
        document.getElementById(`delete${cars[i]["uuid"]}`).addEventListener("click", async () => {
            let text = await deleteCarAsync(cars[i]["uuid"])
            console.log(text)
        })

        document.getElementById(`edit${cars[i]["uuid"]}`).addEventListener("click", async () => {
            document.getElementById("editCarContainer").style.visibility = "visible"

            document.getElementById("editModelInput").value = cars[i]["model"]
            document.getElementById("editCarYear").value = cars[i]["date"]["year"]

            let editCar = document.getElementById("editCar")
            editCar.replaceWith(editCar.cloneNode(true))
            document.getElementById("editCar").addEventListener("click", async () => {
                let text = await editCarAsync(cars[i], document.getElementById("editModelInput").value, document.getElementById("editCarYear").value)
                console.log(text)
                document.getElementById("editCarContainer").style.visibility = "collapse"
            })
        })
    }
}

document.getElementById(`generateButton`).addEventListener("click", async () => {
    let text = await generateAsync()
    console.log(text)
    await loadCarsAsync()
})

loadCarsAsync()

generateAsync = async () => {
    let response = await fetch("/generate", { method: "post" })

    if (!response.ok) {
        return response.status
    }
    else {
        return await response.text()
    }
}

deleteCarAsync = async (uuid) => {
    let response = await fetch("/delete", { method: "post", body: uuid })

    if (!response.ok) {
        return response.status
    }
    else {
        await loadCarsAsync()
        return await response.text()
    }
}

editCarAsync = async (car, model, year) => {
    console.log(model + " " + car["uuid"])
    const data = JSON.stringify({
        id: car["id"],
        uuid: car["uuid"],
        model: model,
        year: year,
        airbags: [
            {
                description: "driver",
                value: car["airbags"][0]["value"]
            },
            {
                description: "passenger",
                value: car["airbags"][1]["value"]
            },
            {
                description: "backSeats",
                value: car["airbags"][2]["value"]
            },
            {
                description: "sides",
                value: car["airbags"][3]["value"]
            }
        ],
        color: car["color"]
    })

    let response = await fetch("/update", { method: "post", body: data })

    if (!response.ok) {
        return response.status
    }
    else {
        await loadCarsAsync()
        return await response.text()
    }
}
