package com.itplh.hero.util;

import com.itplh.hero.context.HeroRegionUserContext;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;

@Slf4j
public class RequestUtil {

    private static final int SLEEP_MILLISECONDS = 600;
    private static HashMap<String, String> headers = new HashMap<>();

    static {
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        headers.put("Cache-Control", "max-age-0");
        headers.put("Connection", "keep-alive");
        headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.105 Safari/537.36");
    }

    /**
     * GET请求枭雄页面
     * <p>
     * 1.请求之前会先sleep，目的是防止触发游戏设置的频繁访问拒绝策略
     * 2.请求失败会重试
     *
     * @param uri
     * @param operateLog
     * @return
     */
    public static Document sleepThenGETRequest(String uri, String operateLog) {
        ThreadUtil.sleep(SLEEP_MILLISECONDS);
        return requestPlus(RequestHelper.get(uri, operateLog),
                (requestHelper) -> request(requestHelper));
    }

    public static Optional<Document> requestByLinkName(Document document, Collection<String> linkNames, String operateLog) {
        return ElementUtil.queryURIByLinkName(document, linkNames)
                .map(uri -> sleepThenGETRequest(uri, operateLog));
    }

    public static Optional<Document> requestByLinkName(Document document, String linkName, String operateLog) {
        return requestByLinkName(document, Arrays.asList(linkName), operateLog);
    }

    public static Optional<Document> requestByAccesskey(Document document,
                                                        String accessValue,
                                                        String operateLog) {
        return ElementUtil.queryURIByAccesskey(document, accessValue)
                .map(uri -> sleepThenGETRequest(uri, operateLog));
    }

    private static Document request(RequestHelper requestHelper) {
        String url = buildURL(requestHelper);
        Document document = null;
        try {
            Connection connection = Jsoup.connect(url).headers(headers).timeout(15000);
            switch (requestHelper.getMethod()) {
                case GET:
                    document = connection.get();
                    break;
                case POST:
                    document = connection.data(requestHelper.getData()).post();
                    break;
                default:
                    break;
            }
            log.trace("[{}] [{}] [{}] [{}]", requestHelper.getMethod(), requestHelper.getSuccess(), requestHelper.getOperateLog(), url);
        } catch (Throwable e) {
            log.error("[{}] [{}] [{}] [{}]", requestHelper.getMethod(), requestHelper.getError(), requestHelper.getOperateLog(), url);
        }
        return document;
    }

    private static Document requestPlus(RequestHelper requestHelper,
                                        Function<RequestHelper, Document> requestFunction) {
        Document document = requestFunction.apply(requestHelper);
        // second retry
        if (Objects.isNull(document)) {
            ThreadUtil.sleep(SLEEP_MILLISECONDS);
            requestHelper.setSuccess("second request success").setError("second request error");
            document = requestFunction.apply(requestHelper);
        }
        // third retry
        if (Objects.isNull(document)) {
            ThreadUtil.sleep(SLEEP_MILLISECONDS);
            requestHelper.setSuccess("third request success").setError("third request error");
            document = requestFunction.apply(requestHelper);
        }
        // fourth retry
        if (Objects.isNull(document)) {
            ThreadUtil.sleep(SLEEP_MILLISECONDS);
            requestHelper.setSuccess("fourth request success").setError("fourth request error");
            document = requestFunction.apply(requestHelper);
        }
        return document;
    }

    private static String buildURL(RequestHelper requestHelper) {
        String scheme = requestHelper.getScheme();
        String domain = requestHelper.getDomain();
        int port = requestHelper.getPort();
        String url = scheme + "://" + domain + ":" + port;

        String defaultURI = "/gCmd.do?cmd=1&sid=sn0a8a5mgdrn051klfvh4j";
        if (StringUtils.hasText(requestHelper.getURI())) {
            String p = requestHelper.getURI().startsWith("/") ? "" : "/";
            defaultURI = p + requestHelper.getURI();
        }
        return url + defaultURI;
    }

    private static Map<String, String> parseURIParameters(String uri) {
        HashMap<String, String> parameterMap = new HashMap<>();

        int parameterStartIndex = uri.lastIndexOf("?") + 1;
        String parameters = uri.substring(parameterStartIndex);
        for (String parameterPair : parameters.split("&")) {
            String[] parameter = parameterPair.split("=");
            if (parameter.length == 2) {
                parameterMap.put(parameter[0], parameter[1]);
            }
        }

        return parameterMap;
    }

    private static String parseSid(String uri) {
        return parseURIParameters(uri).get("sid");
    }

    @Data
    @Accessors(chain = true)
    private static class RequestHelper {

        private String URI;
        private Connection.Method method;
        private String success = "request success";
        private String error = "request error";
        private Map<String, String> data = Collections.EMPTY_MAP;
        private String scheme;
        private String domain;
        private int port;
        private String operateLog;

        private RequestHelper(String uri, String operateLog) {
            this.URI = uri;
            this.operateLog = operateLog;

            HeroRegionUserContext.get(parseSid(uri)).ifPresent(heroRegionUser -> {
                this.scheme = heroRegionUser.getScheme();
                this.domain = heroRegionUser.getDomain();
                this.port = heroRegionUser.getPort();
            });
        }

        public static RequestHelper get(String uri, String operateLog) {
            return new RequestHelper(uri, operateLog).setMethod(Connection.Method.GET);
        }

        public static RequestHelper post(String uri, Map<String, String> data, String operateLog) {
            return new RequestHelper(uri, operateLog).setData(data).setMethod(Connection.Method.POST);
        }

    }

}
