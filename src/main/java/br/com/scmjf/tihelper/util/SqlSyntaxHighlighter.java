package br.com.scmjf.tihelper.util;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public final class SqlSyntaxHighlighter {

    private static final String[] KEYWORDS = {
            "ADD", "ALTER", "AND", "AS", "ASC", "BETWEEN", "BY", "CASE", "CAST", "COUNT", "CREATE",
            "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "END", "EXISTS", "FROM", "GROUP", "HAVING",
            "IN", "INNER", "INSERT", "INTO", "IS", "JOIN", "LEFT", "LIKE", "MAX", "MIN", "NOT", "NULL",
            "ON", "OR", "ORDER", "OUTER", "RIGHT", "ROWNUM", "SELECT", "SET", "SUM", "THEN", "TO_DATE",
            "TRUNC", "UNION", "UPDATE", "VALUES", "WHEN", "WHERE"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PARAMETER_PATTERN = ":[A-Za-z][A-Za-z0-9_]*";
    private static final String STRING_PATTERN = "'([^'\\\\]|\\\\.)*'";
    private static final String NUMBER_PATTERN = "\\b\\d+(?:\\.\\d+)?\\b";
    private static final String COMMENT_PATTERN = "--[^\\n]*|/\\*(.|\\R)*?\\*/";

    private static final Pattern SQL_PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PARAMETER>" + PARAMETER_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")",
            Pattern.CASE_INSENSITIVE);

    private SqlSyntaxHighlighter() {
    }

    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = SQL_PATTERN.matcher(text);
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastKeywordEnd = 0;

        while (matcher.find()) {
            String styleClass = styleClass(matcher);
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKeywordEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKeywordEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKeywordEnd);
        return spansBuilder.create();
    }

    private static String styleClass(Matcher matcher) {
        if (matcher.group("KEYWORD") != null) {
            return "sql-keyword";
        }
        if (matcher.group("PARAMETER") != null) {
            return "sql-parameter";
        }
        if (matcher.group("STRING") != null) {
            return "sql-string";
        }
        if (matcher.group("NUMBER") != null) {
            return "sql-number";
        }
        return "sql-comment";
    }
}
