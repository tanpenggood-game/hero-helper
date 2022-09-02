package com.itplh.hero.util;

import com.itplh.hero.constant.WorldMapEnum;
import com.itplh.hero.event.AbstractEvent;
import org.jsoup.nodes.Document;

import java.util.Objects;
import java.util.Optional;

import static com.itplh.hero.util.GameUtil.requestReturnGameMainPage;
import static com.itplh.hero.util.MoveUtil.moveAndContinuous;
import static com.itplh.hero.util.RequestUtil.requestByLinkName;
import static com.itplh.hero.util.RequestUtil.sleepThenGETRequest;

public class Go1000mUtil {

    /**
     * 神行千里
     * public
     *
     * @param event
     * @param target
     * @param operateLog
     * @return
     */
    public static Optional<Document> go1000m(String uri,
                                             WorldMapEnum target,
                                             AbstractEvent event,
                                             String operateLog) {
        return Optional.ofNullable(sleepThenGETRequest(uri, event, operateLog))
                .flatMap(document -> go1000m(event, target));
    }

    /**
     * 神行千里
     * <p>
     * return game main page, if {@param target} is null
     *
     * @param event
     * @param target
     * @return
     */
    public static Optional<Document> go1000m(AbstractEvent event, WorldMapEnum target) {
        // return game main page
        Document document = requestReturnGameMainPage(event).orElse(null);
        if (Objects.isNull(target)) {
            return Optional.ofNullable(document);
        }
        // check is extend
        if (target.isExtend()) {
            return go1000mExtend(event, target);
        }
        // current page is target page
        boolean isTargetPage = isTargetPage(document, target);
        if (isTargetPage) {
            return Optional.ofNullable(document);
        }
        return Optional.ofNullable(document)
                // request-功能菜单
                .flatMap(doc -> requestByLinkName(event, "功能菜单", "robot request-功能菜单"))
                // request-神行千里
                .flatMap(doc -> requestByLinkName(event, "神行千里", "robot request-神行千里"))
                .flatMap(doc -> requestByLinkName(event, target.getName(), "robot 神行千里-" + target.getName()));
    }

    /**
     * 神行千里
     * 扩展的传送地图点
     *
     * @param event
     * @param target
     * @return
     */
    private static Optional<Document> go1000mExtend(AbstractEvent event, WorldMapEnum target) {
        Document document = event.eventContext().currentDocument();
        // current page is target page
        if (isTargetPage(document, target)) {
            return Optional.ofNullable(document);
        }
        return go1000m(event, target.getTransferPoint())
                .flatMap(doc -> moveAndContinuous(event, target.getRouters()));
    }

    /**
     * check current page is target page
     *
     * @param document
     * @param target
     * @return
     */
    private static boolean isTargetPage(Document document, WorldMapEnum target) {
        return ElementUtil.queryURILikeLinkName(document, target.getTargetPageUniqueLinkName()).isPresent();
    }

}
