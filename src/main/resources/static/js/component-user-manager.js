function componentUserManager() {
    const userForm = ref({...defaultUserForm})
    const userDashboardDataSource = ref([])

    const clearUserForm = () => {
        userForm.value = {...defaultUserForm}
    }

    const save = () => {
        const form = userForm.value
        // data
        const data = parseURL(form['gameMainPageUrl'])
        const setIfPresent = (field) => {
            const value = form[field]
            if (value) {
                data[field] = value
            }
        }
        setIfPresent('scheme')
        setIfPresent('region')
        setIfPresent('roleName')

        // request api
        apiSimpleHelper(apiUserSave(data), res => {
            clearUserForm()
            refreshUserDashboard()
        })
    }

    const refreshUserDashboard = () => {
        apiSimpleHelper(apiUserGetAll(), (res) => {
            const all = []
            res.data.forEach(user => {
                user['key'] = user['sid']
                user['saveTime'] = user['createTime']
                all.push(user)
            })
            // according to saveTime desc
            all.sort((e1, e2) => new Date(e2['saveTime']) - new Date(e1['saveTime']))
            userDashboardDataSource.value = all
        })
    }

    const deleteAll = () => apiSimpleHelper(apiUserDeleteAll(), refreshUserDashboard)
    const delete0 = (sid) => apiSimpleHelper(apiUserDelete(sid), refreshUserDashboard)

    // 返回值会暴露给模板和其他的选项式 API 钩子
    return {
        userForm,
        clearUserForm,

        refreshUserDashboard,
        userDashboardDataSource,
        userDashboardColumns: userDashboardColumns(),
        deleteAll,
        delete0,
        save,
    }
}