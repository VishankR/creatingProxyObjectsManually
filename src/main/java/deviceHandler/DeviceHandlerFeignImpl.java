package deviceHandler;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@ApiClient(type = ClientType.FEIGN)
@Component
@Slf4j
public class DeviceHandlerFeignImpl implements DeviceHandler {

    @Override
    public void remoteAddBatch(Integer envValue) {
       System.out.println("RemoteAddBatch from FEIGN impl");
       }

    @Override
    public void remoteDeleteBatch(Integer envValue) {   
    	System.out.println("RemoteDeleteBatch from FEIGN impl");
    }   
  
}
