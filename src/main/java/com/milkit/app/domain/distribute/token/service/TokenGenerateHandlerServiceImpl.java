package com.milkit.app.domain.distribute.token.service;

import com.milkit.app.api.pay.request.DistributeRequest;
import com.milkit.app.domain.distribute.Distribute;
import com.milkit.app.domain.distribute.service.DistributeServiceImpl;
import com.milkit.app.service.pay.token.SimpleRandomTokenGenerateDelegateServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerateHandlerServiceImpl {
    
    @Autowired
    private SimpleRandomTokenGenerateDelegateServiceImpl simpleRandomTokenGenerateDelegateService;

	@Autowired
	private DistributeServiceImpl distributeService;

    public String generateToken(DistributeRequest distributeRequest) throws Exception {
        String token = null;
        
        do {
            String tmpToken = simpleRandomTokenGenerateDelegateService.generateToken();
            Distribute distribute = distributeService.getDistribute(tmpToken);

            if(distribute == null) {
                token = tmpToken;
            }
        } while(token == null);

        return token;
    }

}
