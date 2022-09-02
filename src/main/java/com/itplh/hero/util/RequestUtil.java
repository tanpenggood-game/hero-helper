package com.itplh.hero.util;

import com.itplh.hero.domain.SimpleUser;
import com.itplh.hero.event.AbstractEvent;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.Assert;
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
     * @param event
     * @param operateLog
     * @return
     */
    public static Document sleepThenGETRequest(String uri, AbstractEvent event, String operateLog) {
        Document document = sleepThenRequestPlus(RequestHelper.get(uri, event, operateLog),
                (requestHelper) -> request(requestHelper));
        // real time update current document
        event.eventContext().realTimeUpdateDocument(document);
        return document;
    }

    public static Optional<Document> requestByLinkName(AbstractEvent event,
                                                       Collection<String> linkNames,
                                                       String operateLog) {
        return ElementUtil.queryURILikeLinkName(event.eventContext().currentDocument(), linkNames)
                .map(uri -> sleepThenGETRequest(uri, event, operateLog));
    }

    public static Optional<Document> requestByLinkName(AbstractEvent event,
                                                       String linkName,
                                                       String operateLog) {
        return requestByLinkName(event, Arrays.asList(linkName), operateLog);
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

    private static Document sleepThenRequestPlus(RequestHelper requestHelper,
                                                 Function<RequestHelper, Document> requestFunction) {
        ThreadUtil.sleep(SLEEP_MILLISECONDS);
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
        String completeDomain = scheme + "://" + domain + ":" + port;

        String uri = requestHelper.getURI();
        if (StringUtils.hasText(uri)) {
            String p = uri.startsWith("/") ? "" : "/";
            uri = p + uri;
        }

        return completeDomain + uri;
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

        private RequestHelper(String uri, AbstractEvent event, String operateLog) {
            Assert.hasText(uri, "uri is required.");
            this.URI = uri;

            SimpleUser user = event.eventContext().getUser();
            this.scheme = user.getScheme();
            this.domain = user.getDomain();
            this.port = user.getPort();

            this.operateLog = operateLog;
        }

        public static RequestHelper get(String uri, AbstractEvent event, String operateLog) {
            return new RequestHelper(uri, event, operateLog).setMethod(Connection.Method.GET);
        }

        public static RequestHelper post(String uri,
                                         AbstractEvent event,
                                         Map<String, String> data,
                                         String operateLog) {
            return new RequestHelper(uri, event, operateLog).setData(data).setMethod(Connection.Method.POST);
        }

    }

}
