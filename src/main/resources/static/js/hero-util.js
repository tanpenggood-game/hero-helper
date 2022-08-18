/**
 * format timestamp to string date time
 *
 * @param timestamp
 * @returns {*}
 */
function formatDate(timestamp) {
    return moment(parseInt(timestamp)).format('yyyy-MM-DD HH:mm:ss')
}

function parseURL(url) {
    let sid = null
    let domain = null
    let port = null
    if (url) {
        if (url.includes('?')) {
            const arr = url.split('?')
            if (arr.length === 2) {
                let part1 = arr[0]
                // parse domain & port
                if (part1) {
                    part1 = part1.toLowerCase()
                    if (part1.startsWith('http://')) {
                        domain = part1.substring('http://'.length)
                        domain = domain.substring(0, domain.indexOf('/'))
                    } else if (part1.startsWith('https://')) {
                        domain = part1.substring('https://'.length)
                        domain = domain.substring(0, domain.indexOf('/'))
                    } else {
                        domain = part1.substring(0, part1.indexOf('/'))
                    }
                    if (domain.includes(':')) {
                        const domainAndPort = domain.split(':')
                        domain = domainAndPort[0]
                        port = domainAndPort[1]
                    }
                }
                // parse sid
                const part2 = arr[1]
                if (part2) {
                    const paramPairs = part2.split('&')
                    if (paramPairs.length > 0) {
                        paramPairs.filter(paramPair => paramPair.startsWith('sid='))
                            .forEach(paramPair => sid = paramPair.substring('sid='.length))
                    }
                }
            }
        }
    }

    return {
        sid,
        domain,
        port,
    }
}