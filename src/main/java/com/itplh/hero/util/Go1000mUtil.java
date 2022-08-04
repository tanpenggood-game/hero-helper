package com.itplh.hero.util;

import com.itplh.hero.constant.WorldMapEnum;
import org.jsoup.nodes.Document;

import java.util.Objects;
import java.util.Optional;

import static com.itplh.hero.util.ElementUtil.queryURIByLinkName;
import static com.itplh.hero.util.GameUtil.requestReturnGameMainPage;
import static com.itplh.hero.util.MoveUtil.moveAndContinuous;
import static com.itplh.hero.util.RequestUtil.requestByLinkName;
import static com.itplh.hero.util.RequestUtil.sleepThenGETRequest;

public class Go1000mUtil {

    /**
     * 神行千里
     * public
     *
     * @param uri
     * @param target
     * @param operateLog
     * @return
     */
    public static Optional<Document> go1000m(String uri, WorldMapEnum target, String operateLog) {
        return Optional.ofNullable(sleepThenGETRequest(uri, operateLog))
                .flatMap(document -> go1000m(document, target));
    }

    /**
     * 神行千里
     *
     * @param document
     * @param target
     * @return
     */
    public static Optional<Document> go1000m(Document document, WorldMapEnum target) {
        if (Objects.isNull(target)) {
            return Optional.ofNullable(document);
        }
        // return game main page
        document = requestReturnGameMainPage(document).orElse(null);
        // check is extend
        if (target.isExtend()) {
            return go1000mExtend(document, target);
        }
        // current page is target page
        boolean isTargetPage = isTargetPage(document, target);
        if (isTargetPage) {
            return Optional.ofNullable(document);
        }
        return Optional.ofNullable(document)
                // request-功能菜单
                .flatMap(doc -> requestByLinkName(doc, "功能菜单", "robot request-功能菜单"))
                // request-神行千里
                .flatMap(doc -> requestByLinkName(doc, "神行千里", "robot request-神行千里"))
                .flatMap(doc -> requestByLinkName(doc, target.getName(), "robot 神行千里-" + target.getName()));
    }

    /**
     * 神行千里
     * 扩展的传送地图点
     *
     * @param document
     * @param target
     * @return
     */
    private static Optional<Document> go1000mExtend(Document document, WorldMapEnum target) {
        // current page is target page
        if (isTargetPage(document, target)) {
            return Optional.ofNullable(document);
        }
        return go1000m(document, target.getTransferPoint())
                .flatMap(doc -> moveAndContinuous(doc, target.getRouters()));
    }

    /**
     * check current page is target page
     *
     * @param document
     * @param target
     * @return
     */
    private static boolean isTargetPage(Document document, WorldMapEnum target) {
        return queryURIByLinkName(document, target.getTargetPageUniqueLinkName()).isPresent();
    }

}
