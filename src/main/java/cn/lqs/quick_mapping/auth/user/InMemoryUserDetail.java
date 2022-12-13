package cn.lqs.quick_mapping.auth.user;

import cn.lqs.quick_mapping.infrastructure.util.AesUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.lqs.quick_mapping.config.QMConstants.DATA_DIR;
import static cn.lqs.quick_mapping.config.QMConstants.USER_INFO_F_NAME;

/**
 *
 */
@Slf4j
@Getter
@Component
public final class InMemoryUserDetail {

    private String username;
    private String password;

    private String passwordMD5;

    private final static Pattern USERNAME_PAT = Pattern.compile("^[a-zA-Z0-9_]{4}$");
    private final static Pattern PASSWORD_PAT = Pattern.compile("^[a-zA-Z0-9_@#$&]{4,15}$");

    @Autowired
    public InMemoryUserDetail() throws IOException, IllegalBlockSizeException, BadPaddingException {
        File userF = Path.of(DATA_DIR, USER_INFO_F_NAME).toFile();
        if (userF.exists() && userF.canRead()) {
            log.info("Init user info from file [{}].", userF.getName());
            String[] up = new String(AesUtil.decrypt(FileUtils.readFileToByteArray(userF)),
                    StandardCharsets.UTF_8).split("/");
            this.username = up[0];
            this.password = up[1];
        } else {
            log.info("No user info file found. Use default U/P [quick_mapping/quick_mapping]");
            this.username = "quick_mapping";
            this.password = "quick_mapping";
        }
        this.passwordMD5 = DigestUtils.md5DigestAsHex(this.password.getBytes(StandardCharsets.UTF_8));
    }


    public boolean validateUP(String username, String password) {
        boolean isOk = StringUtils.hasText(username) && StringUtils.hasText(password) && this.username.length() == username.length();
        if (isOk) {
            return this.passwordMD5.equalsIgnoreCase(password);
        }
        return false;
    }

    public boolean setUsername(String username) {
        Matcher matcher = USERNAME_PAT.matcher(username);
        if (matcher.find()) {
            this.username = username;
            return saveToUserF();
        }
        return false;
    }

    public boolean setPassword(String password) {
        Matcher matcher = PASSWORD_PAT.matcher(password);
        if (matcher.find()) {
            this.password = password;
            this.passwordMD5 = DigestUtils.md5DigestAsHex(this.password.getBytes(StandardCharsets.UTF_8));
            return saveToUserF();
        }
        return false;
    }

    public boolean saveToUserF() {
        try {
            File userF = Path.of(DATA_DIR, USER_INFO_F_NAME).toFile();
            String up = this.username + "/" + this.password;
            FileUtils.writeByteArrayToFile(userF, AesUtil.encrypt(up.trim().getBytes(StandardCharsets.UTF_8)));
            return true;
        }catch (Exception e){
            log.error("update .userinfo failed!", e);
        }
        return false;
    }

}
