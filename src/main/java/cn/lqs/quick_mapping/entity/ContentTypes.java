package cn.lqs.quick_mapping.entity;

/**
 * 2022/9/22 19:15
 * created by @lqs
 */
public class ContentTypes {

    public final static String TEXT_HTML = "text/html";
    public final static String TEXT_PLAIN = "text/plain";
    public final static String TEXT_XML = "text/xml";

    public final static String MULTIPART_FORM_DATA = "multipart/form-data";
    public final static String OCTET_STREAM = "application/octet-stream";

    public final static String APPLICATION_JSON = "application/json";


    public final static String[] TEXT_ALL =
            new String[]{
                    TEXT_HTML, TEXT_PLAIN, APPLICATION_JSON, TEXT_XML
            };

    public final static String[] BINARY_ALL =
            new String[]{
                MULTIPART_FORM_DATA, OCTET_STREAM
            };
}
