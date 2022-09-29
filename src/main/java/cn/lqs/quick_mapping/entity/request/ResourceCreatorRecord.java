package cn.lqs.quick_mapping.entity.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2022/9/27 14:37
 * created by @lqs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCreatorRecord {

    private String key;
    private int saveType;
    private String contentType;
    private String textContent;
    private boolean textBody;
    private String uploadedAt;
    private String fileKey;

}
