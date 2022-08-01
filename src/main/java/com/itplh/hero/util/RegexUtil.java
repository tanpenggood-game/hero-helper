package com.itplh.hero.util;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class RegexUtil {

    public static final List<Character> SPECIAL_CHARACTERS = Arrays.asList('*', '.', '?', '+', '$', '^', '[', ']', '(', ')', '{', '}', '|', '\\', '/');

    /**
     * 构建或正则，满足其中一个即可
     *
     * @param texts
     * @return
     */
    public static String buildAnyOneRegex(String... texts) {
        StringBuilder builder = new StringBuilder();
        for (String text : texts) {
            builder.append("(").append(escapeIfNecessary(text)).append(")").append("|");
        }
        builder.deleteCharAt(builder.length() - 1);
        return "^" + builder.toString() + "$";
    }

    /**
     * 特殊符号转义
     *
     * @return
     */
    public static String escapeIfNecessary(String text) {
        if (StringUtils.hasText(text)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (SPECIAL_CHARACTERS.contains(c)) {
                    builder.append("\\" + c);
                    continue;
                }
                builder.append(c);
            }
            text = builder.toString();
        }
        return text;
    }

}
