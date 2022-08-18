const {notification} = antd

const globalAxios = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 10000,
})

function get(url, params = {}) {
    return globalAxios({
        method: 'get',
        url: url,
        params,
    })
}

function post(url, params = {}, data = {}) {
    return globalAxios({
        method: 'post',
        url: url,
        params,
        data,
    })
}

function apiSimpleHelper(apiPromise, successCallback) {
    apiPromise.then(({data: res}) => {
        notificationHelper(res)
        if (res.success && successCallback) {
            successCallback(res)
        }
    })
}

let globalSuccessMessageLock = 0

function notificationHelper(res) {
    let dataStr = JSON.stringify(res.data)
    const description = dataStr.length > 200 ? dataStr.substring(0, 200) + " ..." : dataStr

    const durationSecond = 5
    const innerNotificationHelper = (type) => {
        notification[type]({
            message: res.message,
            description: description,
            placement: 'topRight',
            duration: durationSecond,
            onClick: () => {
            },
        })
    }

    if (res.success) {
        ++globalSuccessMessageLock
        if (globalSuccessMessageLock === 1) {
            innerNotificationHelper('success')
            setTimeout(() => globalSuccessMessageLock = 0, durationSecond * 1000)
        }
    } else {
        innerNotificationHelper('error')
    }
}

