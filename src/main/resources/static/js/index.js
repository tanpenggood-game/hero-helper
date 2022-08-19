const {createApp} = Vue

createApp({
    setup: function () {
        const eventManagerReturn = componentEventManager()
        const userManagerReturn = componentUserManager()
        const strategyGuideReturn = componentStrategyGuide()

        // init event dashboard data
        eventManagerReturn.refreshEventDashboard()
        // init user dashboard data
        userManagerReturn.refreshUserDashboard()

        // 返回值会暴露给模板和其他的选项式 API 钩子
        return Object.assign(
            eventManagerReturn,
            userManagerReturn,
            strategyGuideReturn,
        )
    },
}).use(antd).mount('#app')