package deviceHandler;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@ApiClient(type = ClientType.URL)
@Component
@Slf4j
public class DeviceHandlerUrlImpl implements DeviceHandler {

    @Override
    public void remoteAddBatch(Integer envValue) {
    	System.out.println("RemoteAddBatch from URL impl");
    }

    @Override
    public void remoteDeleteBatch(Integer envValue) {
    	System.out.println("RemoteAddBatch from URL impl");
    }
}
