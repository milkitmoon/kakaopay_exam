package com.milkit.app.service.pay.token;

import com.milkit.app.common.DistributeSizeCommon;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.RandomStringGenerator;
import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SimpleRandomTokenGenerateDelegateServiceImpl implements TokenGenerateDelegateService {

    protected static final String BASE62_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Override
    public String generateToken() throws Exception {
//        String token = RandomStringUtils.random(DistributeSizeCommon.TOKEN_SIZE, BASE62_STR);
        RandomStringGenerator generator = new RandomStringGenerator.Builder().selectFrom(BASE62_STR.toCharArray()).build();
        String token = generator.generate(DistributeSizeCommon.TOKEN_SIZE);
        
//log.debug("token:"+token);
        return token;
    }
    
}
