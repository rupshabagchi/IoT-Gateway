package com.ericsson.deviceaccess.serviceschema.codegenerator.javabuilder;

import java.util.regex.Pattern;

/**
 * Helps with code generation.
 *
 * @author delma
 */
public enum JavaHelper {

    /**
     * Singleton.
     */
    INSTANSE;

    /**
     * Extends tag.
     */
    public static final String EXTENDS = "extends";
    /**
     * Implements tag.
     */
    public static final String IMPLEMENTS = "implements";
    /**
     * Indent string.
     */
    public static final String INDENT = "    ";
    /**
     * Line ending.
     */
    public static final String LINE_END = "\n";
    /**
     * End of statement.
     */
    public static final String STATEMENT_END = ";";
    /**
     * Start of block.
     */
    public static final String BLOCK_START = "{";
    /**
     * End of block.
     */
    public static final String BLOCK_END = "}";
    /**
     * Package tag.
     */
    public static final String PACKAGE = "package";
    /**
     * Import tag.
     */
    public static final String IMPORT = "import";

    /**
     * Start of parameter replacement.
     */
    public static final String REPLACEMENT_START = "#";
    /**
     * End of parameter replacement.
     */
    public static final String REPLACEMENT_END = ";";
    private static final String ENDFUL_PATTERN = REPLACEMENT_START + "\\d*" + REPLACEMENT_END;
    private static final String ENDLESS_PATTERN = REPLACEMENT_START + "\\d+";
    /**
     * Pattern for parameter replacement in code blocks.
     */
    public static final Pattern REPLACEMENT_PATTERN = Pattern.compile("(" + ENDFUL_PATTERN + ")|(" + ENDLESS_PATTERN + ")");

    /**
     * Gets code generation warning for Javadoc.
     *
     * @param generator generator of code
     * @return warning
     */
    public static String getGenerationWarning(Class<?> generator) {
        return "THIS IS AUTOMATICALLY GENERATED BY {@link " + generator.getCanonicalName() + "}.";
    }

    /**
     * Adds indent to builder.
     *
     * @param builder builder to indent
     * @param indent amount of indent
     * @return builder
     */
    public static StringBuilder indent(StringBuilder builder, int indent) {
        while (0 < indent) {
            builder.append(INDENT);
            indent--;
        }
        return builder;
    }

    /**
     * Adds empty line.
     *
     * @param builder builder to add line
     * @return builder
     */
    public static StringBuilder emptyLine(StringBuilder builder) {
        return builder.append(LINE_END);
    }

}
