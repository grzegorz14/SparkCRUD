loadCarsAsync = async () => {
    let json = await fetch("/json", { method: "get" }).then(response => response.json())
    let cars = JSON.parse(json["cars"])
    //console.log(cars)

    let carsList = ""

    for (let i = 0; i < cars.length; i++) {
        carsList += `<tr> 
                <td class="idTd">${cars[i]["id"]}</td>
                <td class="uuidTd">${cars[i]["uuid"]}</td>
                <td class="modelTd">${cars[i]["model"]}</td>
                <td class="yearTd">${cars[i]["date"]["year"]}</td>`

        let airbags = "";
        for (let j = 0; j < 4; j++) {
            airbags += cars[i]["airbags"][j]["description"] + ":" + cars[i]["airbags"][j]["value"] + "<br>"
        }
        carsList += `<td class="airbagsTd">${airbags}</td>`

        carsList += `<td class="colorTd"><div class="colorDiv" style="background-color: ${cars[i]["color"]}"></div></td>
            <td class="buttonTd"><button class="warningButton" id="invoice${cars[i]["uuid"]}">Generete invoice</button></td>
            <td class="buttonTd">`

        if (cars[i]["invoice"]) {
            carsList += `<a class="downloadLink editButton" href="/download/${cars[i]["uuid"]}">Download</a>`
        }

        carsList += `</td></tr>`
    }
    document.getElementById("carsList").innerHTML = carsList

    for (let i = 0; i < cars.length; i++) {
        document.getElementById(`invoice${cars[i]["uuid"]}`).addEventListener("click", async () => {
            let text = await invoiceAsync(cars[i]["uuid"])
            console.log(text)
            await loadCarsAsync()
            document.getElementById(`invoice${cars[i]["uuid"]}`).disabled = true;
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

invoiceAsync = async (uuid) => {
    let response = await fetch("/invoice", { method: "post", body: uuid })

    if (!response.ok) {
        return response.status
    }
    else {
        return await response.text()
    }
}
