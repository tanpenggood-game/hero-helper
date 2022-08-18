function componentEventManager() {
    const triggerEventForm = ref({...defaultTriggerEventForm})
    const resourceOptions = ref([])
    const resourceSelectMode = ref('multiple')
    const userOptions = ref(globalUserOptions)
    const eventDashboardDataSource = ref([])

    const clearTriggerEventForm = () => {
        triggerEventForm.value = {...defaultTriggerEventForm}
    }

    const triggerEvent = () => {
        const form = triggerEventForm.value
        // data
        const data = {
            eventName: form['eventName'],
            sids: form['sids'],
            targetRunRound: form['targetRunRound'],
            extendInfo: {}
        }
        // handle extendInfo field
        const setExtendInfoIfPresent = (field) => {
            const value = form[field]
            if (value) {
                data['extendInfo'][field] = value
            }
        }
        setExtendInfoIfPresent('isExclude')
        setExtendInfoIfPresent('novice')
        setExtendInfoIfPresent('isSupplyGrain')
        setExtendInfoIfPresent('resources')
        if (Array.isArray(form['resources'])) {
            data['extendInfo']['resources'] = form['resources'].join(',')
        }
        // request api
        apiSimpleHelper(apiEventTrigger(data), res => {
            clearTriggerEventForm()
            refreshEventDashboard()
        })
    }

    const refreshResourceOptions = (eventName) => apiResourceGet(eventName)
        .then(({data: res}) => {
            resourceOptions.value = res.data

            const form = triggerEventForm.value
            resourceSelectMode.value = form['eventName'] === 'NPCFixedEvent' ? 'combobox' : 'multiple'
            if (form['resources']) {
                form['resources'] = undefined
            }
        })

    const refreshUserOptions = () => {
        apiUserGetAll().then(({data: res}) => {
            const options = []
            const users = res.data || []
            users.sort((u1, u2) => new Date(u2['createTime']) - new Date(u1['createTime']))
            users.forEach(user => {
                const sid = user['sid']
                options.push({
                    key: sid,
                    value: sid,
                    label: showUserName(user),
                })
            })
            userOptions.value = options
        })
    }

    const refreshEventDashboard = () => {
        apiSimpleHelper(apiEventGetAll(), (res) => {
            const all = []
            res.data.forEach(e => {
                const event = e['source']
                const user = event['user']
                event['key'] = user['sid']
                event['sid'] = user['sid']
                event['user'] = showUserName(user)
                event['extendInfo'] = JSON.stringify(event['extendInfo'])
                event['timestamp'] = e['timestamp']
                event['triggerTime'] = formatDate(e['timestamp'])
                if (event['currentOperationResource']) {
                    event['currentOperationResource'] = event['currentOperationResource']['operateName']
                }
                all.push(event)
            })
            // according to timestamp desc
            all.sort((e1, e2) => e2['timestamp'] - e1['timestamp'])
            eventDashboardDataSource.value = all

            refreshUserOptions()
        })
    }

    const pauseAll = () => apiSimpleHelper(apiEventPauseAll(), refreshEventDashboard)
    const pause = (sid) => apiSimpleHelper(apiEventPause(sid), refreshEventDashboard)
    const restartAll = () => apiSimpleHelper(apiEventRestartAll(), refreshEventDashboard)
    const restart = (sid) => apiSimpleHelper(apiEventRestart(sid), refreshEventDashboard)
    const closeAll = () => apiSimpleHelper(apiEventCloseAll(), refreshEventDashboard)
    const close = (sid) => apiSimpleHelper(apiEventClose(sid), refreshEventDashboard)

    // 返回值会暴露给模板和其他的选项式 API 钩子
    return {
        triggerEventForm,
        clearTriggerEventForm,
        triggerEvent,
        resourceOptions,
        refreshResourceOptions,
        resourceSelectMode,
        userOptions,

        eventDashboardDataSource,
        eventDashboardColumns: eventDashboardColumns(),
        refreshEventDashboard,
        pauseAll,
        pause,
        restartAll,
        restart,
        closeAll,
        close,
    }
}

function showUserName(user = {}) {
    let showUserName = ''
    if (user['port']) {
        showUserName += user['port'] + '-'
    }
    if (user['roleName']) {
        showUserName += user['roleName'] + '-'
    }
    showUserName += user['sid']
    return showUserName
}