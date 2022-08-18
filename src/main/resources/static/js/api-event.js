function apiEventGetAll() {
    return get('/event/get-all')
}

function apiEventPauseAll() {
    return post('/event/pause-all')
}

function apiEventPause(sid) {
    return post('/event/pause', {sid})
}

function apiEventRestartAll() {
    return post('/event/restart-all')
}

function apiEventRestart(sid) {
    return post('/event/restart', {sid})
}

function apiEventCloseAll() {
    return post('/event/close-all')
}

function apiEventClose(sid) {
    return post('/event/close', {sid})
}

function apiEventTrigger(data = {eventName: null, sid: null, targetRunRound: 1, extendInfo: {}}) {
    return post('/event/trigger', {}, data)
}
