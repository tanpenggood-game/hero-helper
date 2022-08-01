package com.itplh.hero.util;

import com.itplh.hero.constant.DirectionEnum;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Optional;

import static com.itplh.hero.util.RequestUtil.sleepThenGETRequest;

public class MoveUtil {

    /**
     * 向某个方向移动一步
     *
     * @param document
     * @param direction
     * @return
     */
    public static Optional<Document> move(Document document, DirectionEnum direction) {
        return ElementUtil.queryURIByAccesskey(document, direction.getAccessValue())
                .map(uri -> sleepThenGETRequest(uri, "robot move-" + direction.getLinkName()));
    }

    /**
     * 根据路由连续移动
     *
     * @param document
     * @param routers
     * @return
     */
    public static Optional<Document> moveAndContinuous(Document document, List<DirectionEnum> routers) {
        for (DirectionEnum router : routers) {
            document = move(document, router).orElse(null);
        }
        return Optional.ofNullable(document);
    }

}
