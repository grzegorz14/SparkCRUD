let uuid = window.location.search.substr(1)

document.getElementById(`cropButton`).addEventListener("click", async () => {
    let text = await cropAsync(uuid)
    window.location.reload()
    console.log(text)
})

document.getElementById(`rotateButton`).addEventListener("click", async () => {
    let text = await editImage(uuid, "rotate")
    window.location.reload()
    console.log(text)
})

document.getElementById(`flipHorizontalButton`).addEventListener("click", async () => {
    let text = await editImage(uuid, "flipHorizontal")
    window.location.reload()
    console.log(text)
})

document.getElementById(`flipVerticalButton`).addEventListener("click", async () => {
    let text = await editImage(uuid, "flipVertical")
    window.location.reload()
    console.log(text)
})

cropAsync = async (uuid) => {
    if (rect.startX == null) {
        return
    }
    const data = JSON.stringify({
        x: rect.startX < rect.endX ? rect.startX : rect.endX,
        y: rect.startY < rect.endY ? rect.startY : rect.endY,
        w: Math.abs(rect.w),
        h: Math.abs(rect.h),
        uuid: uuid
    })

    let response = await fetch("/cropImage", { method: "post", body: data })

    if (!response.ok) {
        return response.status
    }
    else {
        return await response.text()
    }
}


editImage = async (uuid, operation) => {
    const data = JSON.stringify({
        type: operation,
        uuid: uuid
    })

    let response = await fetch("/editImage", { method: "post", body: data })

    if (!response.ok) {
        return response.status
    }
    else {
        return await response.text()
    }
}

// my drawing
let rect = {};
let drag = false;
let canvas = document.getElementById('imageCanvas');
let context = canvas.getContext('2d');

let image = new Image();
image.src = `getImage?${uuid}.jpg`

image.onload = () => {
    context.canvas.width = image.width
    context.canvas.height = image.height
    context.drawImage(image, 0, 0);
    canvas.addEventListener('mousedown', mouseDown, false);
    canvas.addEventListener('mouseup', mouseUp, false);
    canvas.addEventListener('mousemove', mouseMove, false);

}

function mouseDown(e) {
    rect.startX = getMousePosition(e).x;
    rect.startY =  getMousePosition(e).y;
    drag = true;
}

function mouseUp() {
    drag = false;
}

function mouseMove(e) {
    if (drag) {
        context.clearRect(0, 0, canvas.width, canvas.height);
        context.drawImage(image, 0, 0);

        rect.endX = getMousePosition(e).x
        rect.endY = getMousePosition(e).y

        rect.w = rect.endX - rect.startX;
        rect.h = rect.endY - rect.startY;
        context.fillStyle="rgba(0, 150, 0, 0.4)";
        drawMarker();
    }
}

function drawMarker() {
    context.fillRect(rect.startX, rect.startY, rect.w, rect.h);
}


//copied function
function getMousePosition(e) {
    let r = canvas.getBoundingClientRect(), // abs. size of element
        scaleX = canvas.width / r.width,    // relationship bitmap vs. element for x
        scaleY = canvas.height / r.height;  // relationship bitmap vs. element for y

    return {
        x: (e.clientX - r.left) * scaleX,   // scale mouse coordinates after they have
        y: (e.clientY - r.top) * scaleY     // been adjusted to be relative to element
    }
}

