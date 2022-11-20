package cn.lqs.quick_mapping.service.manager;

import cn.lqs.quick_mapping.entity.response.UserTokenNote;
import cn.lqs.quick_mapping.infrastructure.util.AesUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * @author @lqs
 */
@Slf4j
@Service
public class UserTokenManager implements TokenManager<UserTokenNote>{

    private final ObjectMapper objectMapper;

    @Autowired
    public UserTokenManager(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public String generate(UserTokenNote info) {
        try {
            String jsonInfo = objectMapper.writeValueAsString(info);
            return AesUtil.encrypt(jsonInfo);
        } catch (JsonProcessingException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("生成用户 token 信息失败!", e);
        }
        return null;
    }


    @Override
    public UserTokenNote parseToken(String token) {
        try {
            String jsonInfo = AesUtil.decrypt(token);
            return objectMapper.readValue(jsonInfo, UserTokenNote.class);
        } catch (IllegalBlockSizeException | BadPaddingException | JsonProcessingException e) {
            log.error("解析用户 token 发生错误", e);
        }
        return null;
    }

    @Override
    public boolean validToken(String token) {
        return parseToken(token) != null;
    }

}
