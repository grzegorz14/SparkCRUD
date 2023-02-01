loadCarsAsync = async () => {
    let json = await fetch("/json", {method: "get"}).then(response => response.json())
    let cars = JSON.parse(json["cars"])

    let carsList = ""
    let yearList = []

    for (let i = 0; i < cars.length; i++) {
        carsList += `<tr>
                <td class="td">${cars[i]["id"]}</td>
                <td class="td">${cars[i]["model"]}</td>
                <td class="td">${cars[i]["date"]["year"]}</td>`

        let airbags = "";
        for (let j = 0; j < 4; j++) {
            airbags += cars[i]["airbags"][j]["description"] + ":" + cars[i]["airbags"][j]["value"] + "<br>"
        }
        carsList += `<td class="airbagsTd">${airbags}</td>`

        carsList += `<td class="td"><div class="colorDiv" style="background-color: ${cars[i]["color"]}"></div></td>`

        let carModels = ["Renault", "BMW", "Tesla", "Mazda", "Fiat", "Toyota", "Seat"];
        if (carModels.includes(cars[i]["model"])) {
            carsList += `<td class="td imageTd"><img class="imageDiv" src="./../images/${cars[i]["model"]}.png" alt="photo"/></td>`
        } else {
            carsList += `<td class="td imageTd"><img class="imageDiv" src="./../images/car.png" alt="photo"/></td>`
        }

        let date = cars[i]["date"]["year"] + "/" + cars[i]["date"]["month"] + "/" + cars[i]["date"]["day"]
        carsList += `<td class="td">${date}</td>
                <td class="td">${cars[i]["price"].toFixed(0)}$</td>`

        let vat = cars[i]["vat"] === "NONE" ? 0 : (cars[i]["vat"] === "SEVEN" ? 7 : 22)
        carsList += `<td class="td">${vat}%</td></tr>`

        if (!yearList.includes(cars[i]["date"]["year"])) {
            yearList.push(cars[i]["date"]["year"])
        }
    }
    document.getElementById("carsList").innerHTML = carsList

    yearList.sort()
    let selectOptions = ""
    for (let i = 0; i < yearList.length; i++) {
        selectOptions += `<option value="${yearList[i]}">${yearList[i]}</option>`
    }

    document.getElementById("yearSelect").innerHTML = selectOptions
}

document.getElementById(`generateButton`).addEventListener("click", async () => {
    let text = await generateAsync()
    console.log(text)
    await loadCarsAsync()
})

document.getElementById(`buttonInvoiceForAll`).addEventListener("click", async () => {
    let text = await invoiceForAllAsync()

    document.getElementById("invoiceForAllContainer").innerHTML += `<a class="downloadLink editButton invoiceLink tooltip" href="/download/invoice_for_all_${text}">
                                                                                Download
                                                                                <span class="tooltiptext">Invoice for all cars -> ${new Date().toLocaleString()}</span>
                                                                              </a>`
    console.log(text)
})

document.getElementById(`buttonInvoiceForYear`).addEventListener("click", async () => {
    let select = document.getElementById("yearSelect")
    let year = select.options[select.selectedIndex].text
    let text = await invoiceForYearAsync(year)

    document.getElementById("invoiceForYearContainer").innerHTML += `<a class="downloadLink editButton invoiceLink tooltip" href="/download/invoice_for_year_${text}">
                                                                                Download
                                                                                <span class="tooltiptext yearTip">Invoice for year -> ${new Date().toLocaleString()}</span>
                                                                              </a>`
    console.log(text)
})

document.getElementById(`buttonInvoiceForPriceRange`).addEventListener("click", async () => {
    let min = document.getElementById("minPrice").value
    let max = document.getElementById("maxPrice").value

    let text = await invoiceForPriceRangeAsync(min, max)
    document.getElementById("invoiceForPriceRangeContainer").innerHTML +=`<a class="downloadLink editButton invoiceLink tooltip" href="/download/invoice_for_price_range_${text}">
                                                                                        Download
                                                                                        <span class="tooltiptext priceTip">Invoice for price range -> ${new Date().toLocaleString()}</span>
                                                                                      </a>`
    console.log(text)
})

loadCarsAsync()

generateAsync = async () => {
    let response = await fetch("/generate", {method: "post"})

    if (!response.ok) {
        return response.status
    } else {
        return await response.text()
    }
}

invoiceForAllAsync = async () => {
    let response = await fetch("/invoiceForAll", {method: "post"})

    if (!response.ok) {
        return response.status
    } else {
        return await response.text()
    }
}

invoiceForYearAsync = async (year) => {
    let response = await fetch("/invoiceForYear", {method: "post", body: year})

    if (!response.ok) {
        return response.status
    } else {
        return await response.text()
    }
}

invoiceForPriceRangeAsync = async (minPrice, maxPrice) => {
    if (minPrice > maxPrice) {
        return "Maximum price is lower than minimum price!";
    }

    let data = JSON.stringify({
        min: minPrice,
        max: maxPrice
    })

    let response = await fetch("/invoiceForPriceRange", {method: "post", body: data})

    if (!response.ok) {
        return response.status
    } else {
        return await response.text()
    }
}


