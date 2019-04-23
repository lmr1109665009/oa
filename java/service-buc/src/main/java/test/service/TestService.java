package test.service;

import com.suneee.eas.common.bean.push.PushMessage;
import com.suneee.eas.common.bean.push.PushResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@FeignClient(value = "service-push")
public interface TestService {

    @RequestMapping("/push/messages/ios")
    public PushResult pushMessagesToIos(PushMessage message);
}
