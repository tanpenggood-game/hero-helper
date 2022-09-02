package com.itplh.hero.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class ElementUtil {

    /**
     * @param document
     * @param linkNames
     * @param likeMatch true like match, false match all.
     * @return
     */
    public static Collection<Element> queryElementsByLinkName(Document document, Collection<String> linkNames, boolean likeMatch) {
        if (CollectionUtils.isEmpty(linkNames) || Objects.isNull(document)) {
            return Collections.EMPTY_LIST;
        }
        return document.getElementsByTag("a")
                .stream()
                .filter(e -> {
                    // 100% match
                    if (!likeMatch) {
                        return linkNames.contains(e.text());
                    }
                    // like match
                    for (String linkName : linkNames) {
                        if (e.text().indexOf(linkName) != -1) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public static Optional<String> queryURIByLinkName(Document document, Collection<String> linkNames, boolean likeMatch) {
        if (CollectionUtils.isEmpty(linkNames)) {
            return Optional.empty();
        }
        return queryElementsByLinkName(document, linkNames, likeMatch)
                .stream()
                .findFirst()
                .map(e -> e.attr("href"));
    }

    /**
     * query uri, like match link name
     *
     * @param document
     * @param linkNames
     * @return
     */
    public static Optional<String> queryURILikeLinkName(Document document, Collection<String> linkNames) {
        return queryURIByLinkName(document, linkNames, true);
    }

    public static Optional<String> queryURILikeLinkName(Document document, String linkName) {
        return queryURILikeLinkName(document, Arrays.asList(linkName));
    }

    public static Optional<String> queryURIByAccesskey(Document document, String accessValue) {
        return Optional.ofNullable(document)
                .map(doc -> doc.getElementsByAttributeValue("accesskey", accessValue).first())
                .map(e -> e.attr("href"));
    }

    public static Optional<String> queryURIByLastALink(Document document) {
        return Optional.ofNullable(document)
                .map(doc -> doc.getElementsByTag("a").last())
                .map(e -> e.attr("href"));
    }

    public static Optional<String> queryURIByFirstALink(Document document) {
        return Optional.ofNullable(document)
                .map(doc -> doc.getElementsByTag("a").first())
                .map(e -> e.attr("href"));
    }

}
