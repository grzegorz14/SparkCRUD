let carModel = document.getElementById("carModel")
let carYear = document.getElementById("carYear")
let driverCheckbox = document.getElementById("checkbox1")
let passengerCheckbox = document.getElementById("checkbox2")
let backSeatsCheckbox = document.getElementById("checkbox3")
let sidesCheckbox = document.getElementById("checkbox4")
let carColor = document.getElementById("carColor")

document.getElementById("addCar").onclick = async () => {
    if (carModel.value !== "") {
        let json = await addCarAsync()
        alert(JSON.stringify(json, null, 5))
        console.log(json)
    }
    else {
        console.log("Car model field is empty!")
    }
}

addCarAsync = async () => {
    const data = JSON.stringify({
        model: carModel.value,
        year: carYear.value,
        airbags: [
            {
                description: "driver",
                value: driverCheckbox.checked
            },
            {
                description: "passenger",
                value: passengerCheckbox.checked
            },
            {
                description: "backSeats",
                value: backSeatsCheckbox.checked
            },
            {
                description: "sides",
                value: sidesCheckbox.checked
            }
        ],
        color: carColor.value
    })

    const options = {
        method: "post",
        body: data,
    };

    carModel.value = ""
    carYear.value = 2010
    driverCheckbox.checked = false
    passengerCheckbox.checked = false
    backSeatsCheckbox.checked = false
    sidesCheckbox.checked = false
    carColor.value = "#00ffff"

    let response = await fetch("/add", options)

    if (!response.ok)
        return response.status
    else
        return await response.json()
}
