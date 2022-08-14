package com.itplh.hero.util;

import org.jsoup.nodes.Document;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;


public class ElementUtil {

    public static Optional<String> queryURIByLinkName(Document document, Collection<String> linkNames, boolean matchAll) {
        if (CollectionUtils.isEmpty(linkNames)) {
            return Optional.empty();
        }
        return Optional.ofNullable(document)
                .map(doc -> doc.getElementsByTag("a"))
                .flatMap(elements -> elements.stream()
                        .filter(e -> {
                            // 100% match
                            if (matchAll) {
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
                        .findFirst())
                .map(e -> e.attr("href"));
    }

    public static Optional<String> queryURIByLinkName(Document document, Collection<String> linkNames) {
        return queryURIByLinkName(document, linkNames, false);
    }

    public static Optional<String> queryURIByLinkName(Document document, String linkName) {
        return queryURIByLinkName(document, Arrays.asList(linkName));
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
