function apiResourceGet(eventName) {
    return get('/resource/get', {eventName})
}

function apiResourcePickItems(eventName) {
    return get('/resource/pick-items', {eventName})
}
