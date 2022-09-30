package cn.lqs.quick_mapping.entity;

/**
 * 2022/9/22 19:15
 * created by @lqs
 */
public class ContentTypes {

    public final static String TEXT_HTML = "text/html";
    public final static String TEXT_PLAIN = "text/plain";
    public final static String TEXT_XML = "text/xml";
    public final static String APPLICATION_MICROSOFT_WORD = "application/msword";

    public final static String FORM_DATA = "multipart/form-data";
    public final static String OCTET_STREAM = "application/octet-stream";

    public final static String IMAGE_GIF = "image/gif";
    public final static String IMAGE_JPEG = "image/jpeg";
    public final static String IMAGE_PNG = "image/png";
    public final static String IMAGE_ICO = "image/x-icon";

    public final static String VIDEO_MP4 = "video/mpeg4";
    public final static String AUDIO_MP3 = "audio/mp3";

    public final static String APPLICATION_JSON = "application/json";
    public final static String APPLICATION_PDF = "application/pdf";


    public final static String[] TEXT_ALL =
            new String[]{
                    TEXT_HTML, TEXT_PLAIN, APPLICATION_JSON, TEXT_XML, APPLICATION_MICROSOFT_WORD
            };

    public final static String[] BINARY_ALL =
            new String[]{
                FORM_DATA, OCTET_STREAM,
                    IMAGE_GIF, IMAGE_JPEG, IMAGE_PNG, IMAGE_ICO,
                    APPLICATION_PDF, VIDEO_MP4, AUDIO_MP3
            };
}
