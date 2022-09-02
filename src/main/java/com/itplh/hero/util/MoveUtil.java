package com.itplh.hero.util;

import com.itplh.hero.constant.DirectionEnum;
import com.itplh.hero.event.AbstractEvent;
import com.itplh.hero.event.HeroEventContext;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Optional;

import static com.itplh.hero.util.RequestUtil.sleepThenGETRequest;

@Slf4j
public class MoveUtil {

    /**
     * 向某个方向移动一步
     *
     * @param event
     * @param direction
     * @return document after move, if direction is exists; otherwise return original document.
     */
    public static Optional<Document> move(AbstractEvent event, DirectionEnum direction) {
        Document document = event.eventContext().currentDocument();
        // move, if direction is exists
        Optional<String> directionURIOptional = ElementUtil.queryURIByAccesskey(document, direction.getAccessValue());
        if (directionURIOptional.isPresent()) {
            document = sleepThenGETRequest(directionURIOptional.get(), event, "robot move-" + direction.getLinkName());
        } else {
            HeroEventContext heroEventContext = event.eventContext();
            log.debug("direction isn't exists [sid={}] [eventName={}] [operateName={}] [directionName={}]",
                    heroEventContext.getUser().getSid(), heroEventContext.getEventName(),
                    heroEventContext.getCurrentOperationResource().getOperateName(), direction.getName());
        }
        return Optional.ofNullable(document);
    }

    /**
     * 根据路由连续移动
     *
     * @param event
     * @param routers
     * @return
     */
    public static Optional<Document> moveAndContinuous(AbstractEvent event, List<DirectionEnum> routers) {
        Document document = null;
        for (DirectionEnum router : routers) {
            document = move(event, router).orElse(null);
        }
        return Optional.ofNullable(document);
    }

}
