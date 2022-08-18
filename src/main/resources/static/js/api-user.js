function apiUserSave(data) {
    return post('/user/save', {}, data)
}

function apiUserDelete(sid) {
    return post('/user/delete', {sid})
}

function apiUserDeleteAll() {
    return post('/user/delete-all')
}

function apiUserGetAll() {
    return get('/user/get-all')
}