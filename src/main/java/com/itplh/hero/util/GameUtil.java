package com.itplh.hero.util;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Optional;

import static com.itplh.hero.util.ElementUtil.queryURIByLastALink;
import static com.itplh.hero.util.ElementUtil.queryURIByLinkName;
import static com.itplh.hero.util.RequestUtil.requestByLinkName;
import static com.itplh.hero.util.RequestUtil.sleepThenGETRequest;

@Slf4j
public class GameUtil {

    /**
     * 返回游戏主界面
     *
     * @param document
     * @return
     */
    public static Optional<Document> requestReturnGameMainPage(Document document) {
        String operateLog = "robot request-返回游戏";
        if (isGameMainPage(document)) {
            return Optional.ofNullable(document);
        }
        // check offline
        boolean isOffline = queryURIByLinkName(document, "快速登陆").isPresent();
        if (isOffline) {
            log.warn("This account already is offline status.");
            return Optional.empty();
        }
        // try request return game link
        boolean hasReturnGameLink = queryURIByLinkName(document, "返回游戏").isPresent();
        if (hasReturnGameLink) {
            return requestByLinkName(document, "返回游戏", operateLog);
        }
        // try request last A link in the page, then continue return game main page
        Optional<String> lastALink = queryURIByLastALink(document);
        if (lastALink.isPresent()) {
            document = sleepThenGETRequest(lastALink.get(), operateLog);
            return requestReturnGameMainPage(document);
        }
        return Optional.ofNullable(document);
    }

    /**
     * 供给粮草
     *
     * @param document
     * @return
     */
    public static Optional<Document> requestSupplyGrain(Document document) {
        if (isNotGameMainPage(document)) {
            document = requestReturnGameMainPage(document).orElse(null);
        }
        return Optional.ofNullable(document)
                .flatMap(doc -> requestByLinkName(doc, "武将", "robot request-武将"))
                .map(doc -> {
                    while (ElementUtil.queryURIByLinkName(doc, "供给粮草").isPresent()) {
                        doc = requestByLinkName(doc, "供给粮草", "robot request-供给粮草").orElse(null);
                    }
                    return doc;
                })
                .flatMap(doc -> requestReturnGameMainPage(doc));
    }

    /**
     * 自动战斗
     *
     * @param document
     * @return
     */
    public static Optional<Document> requestAutoBattle(Document document) {
        String operateLog = "robot request-自动战斗中";
        if (isBattlePage(document)) {
            do {
                // 默认使用技能1
                document = ElementUtil.queryURIByFirstALink(document)
                        .map(uri -> sleepThenGETRequest(uri, operateLog))
                        .orElse(null);
                if (Objects.isNull(document)) {
                    return Optional.empty();
                }
            } while (!isBattleSettlementPage(document));
            // battle end, return game main page
            return requestReturnGameMainPage(document);
        }
        return Optional.ofNullable(document);
    }

    /**
     * 是否为游戏主界面
     *
     * @param document
     * @return
     */
    public static boolean isGameMainPage(Document document) {
        return ElementUtil.queryURIByLinkName(document, "游戏首页").isPresent();
    }

    public static boolean isNotGameMainPage(Document document) {
        return !isGameMainPage(document);
    }

    /**
     * 是否为战斗页面
     *
     * @param document
     * @return
     */
    public static boolean isBattlePage(Document document) {
        return ElementUtil.queryURIByLinkName(document, "控制").isPresent();
    }

    /**
     * 是否为战斗结束页面
     *
     * @param document
     * @return
     */
    public static boolean isBattleSettlementPage(Document document) {
        return Optional.ofNullable(document)
                .map(doc -> doc.getElementsContainingOwnText("战斗已经结束!"))
                .map(elements -> !CollectionUtils.isEmpty(elements))
                .orElse(false);
    }

}
