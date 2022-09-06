package com.itplh.hero.util;

import com.itplh.hero.constant.ParameterEnum;
import com.itplh.hero.constant.PickItemEnum;
import com.itplh.hero.event.AbstractEvent;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.itplh.hero.util.ElementUtil.queryURIByLastALink;
import static com.itplh.hero.util.RequestUtil.requestByLinkName;
import static com.itplh.hero.util.RequestUtil.sleepThenGETRequest;

@Slf4j
public class GameUtil {

    /**
     * 返回游戏主界面
     *
     * @param event
     * @return
     */
    public static Optional<Document> requestReturnGameMainPage(AbstractEvent event) {
        Document document = event.eventContext().currentDocument();
        String operateLog = "robot request-返回游戏";
        if (isGameMainPage(document)) {
            return Optional.ofNullable(document);
        }
        // check offline
        if (isOffline(document)) {
            log.warn("This account already is offline status.");
            return Optional.empty();
        }
        // try request return game link
        boolean hasReturnGameLink = ElementUtil.queryURILikeLinkName(document, "返回游戏").isPresent();
        if (hasReturnGameLink) {
            return requestByLinkName(event, "返回游戏", operateLog);
        }
        // try request last A link in the page, then continue return game main page
        Optional<String> lastALink = queryURIByLastALink(document);
        if (lastALink.isPresent()) {
            document = sleepThenGETRequest(lastALink.get(), event, operateLog);
            return requestReturnGameMainPage(event);
        }
        return Optional.ofNullable(document);
    }

    /**
     * 自动战斗，并返回游戏主页
     * <p>
     * 1. custom pick up some item after battle, if necessary
     * 2. supply grain after battle, if necessary
     *
     * @param document
     * @return
     */
    public static Optional<Document> requestAutoBattleThenReturnGameMainPage(Document document, AbstractEvent event) {
        // auto battle
        String operateLog = "robot request-自动战斗中";
        while (isBattlePage(document)) {
            // 默认使用普通攻击，否则使用技能1
            if (ElementUtil.queryURILikeLinkName(document, "普通攻击").isPresent()) {
                document = requestByLinkName(event, "普通攻击", operateLog).orElse(null);
            } else {
                document = ElementUtil.queryURIByFirstALink(document)
                        .map(uri -> sleepThenGETRequest(uri, event, operateLog))
                        .orElse(null);
            }
            if (Objects.isNull(document)) {
                return Optional.empty();
            }
        }

        // custom pick up some item, if necessary
        pickItemIfNecessary(event);
        // supply grain, if necessary
        requestSupplyGrainIfNecessary(event);

        return requestReturnGameMainPage(event);
    }

    /**
     * 是否为游戏主界面
     *
     * @param document
     * @return
     */
    public static boolean isGameMainPage(Document document) {
        return ElementUtil.queryURILikeLinkName(document, "游戏首页").isPresent();
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
        return ElementUtil.queryURILikeLinkName(document, "快捷键设置").isPresent();
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

    /**
     * 是否已经离线
     *
     * @param document
     * @return
     */
    public static boolean isOffline(Document document) {
        return ElementUtil.queryURILikeLinkName(document, "快速登陆").isPresent();
    }

    public static Document requestSupplyGrainIfNecessary(AbstractEvent event) {
        Document document = event.eventContext().currentDocument();
        boolean isSupplyGrain = event.eventContext().queryExtendInfo(ParameterEnum.SUPPLY_GRAIN).map(Boolean::valueOf)
                .orElse(false);
        if (isSupplyGrain) {
            document = requestSupplyGrain(event).orElse(null);
        }
        return document;
    }

    /**
     * 供给粮草
     *
     * @param event
     * @return
     */
    private static Optional<Document> requestSupplyGrain(AbstractEvent event) {
        Document document = event.eventContext().currentDocument();
        if (isNotGameMainPage(document)) {
            document = requestReturnGameMainPage(event).orElse(null);
        }
        return Optional.ofNullable(document)
                .flatMap(doc -> requestByLinkName(event, "武将", "robot request-武将"))
                .map(doc -> {
                    while (ElementUtil.queryURILikeLinkName(doc, "供给粮草").isPresent()) {
                        doc = requestByLinkName(event, "供给粮草", "robot request-供给粮草").orElse(null);
                    }
                    return doc;
                })
                .flatMap(doc -> requestReturnGameMainPage(event));
    }

    private static Document pickItemIfNecessary(AbstractEvent event) {
        Document document = event.eventContext().currentDocument();
        if (!isBattleSettlementPage(document)) {
            return document;
        }
        List<String> customPickItems = event.eventContext()
                .queryExtendInfo(ParameterEnum.PICK_ITEMS)
                .map(e -> e.split(","))
                .map(Arrays::asList)
                .orElse(Collections.EMPTY_LIST);
        // merge random items & custom items
        Collection<String> pickItems = CollectionUtil.merge(PickItemEnum.getRandomPickItems(), customPickItems);
        if (CollectionUtils.isEmpty(pickItems)) {
            return document;
        }
        for (String pickItem : pickItems) {
            String operateLog = "robot pick-" + pickItem;
            if (ElementUtil.queryURILikeLinkName(document, pickItem).isPresent()) {
                document = requestByLinkName(event, pickItem, operateLog).orElse(null);
            }
        }
        return document;
    }

}
