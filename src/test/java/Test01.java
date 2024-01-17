import com.aihuishou.payflow.FlowApplication;
import com.aihuishou.payflow.engine.FlowEngine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = FlowApplication.class)
public class Test01 {
    @Autowired
    private FlowEngine flowEngine;
    @Test
    public void run01(){
        Map<String, Object> dataMap =new HashMap<>();
        dataMap.put("a1","a3");
        flowEngine.execute("flow1",dataMap);
    }
}
