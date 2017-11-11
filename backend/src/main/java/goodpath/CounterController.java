package goodpath;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CounterController {

    private final AtomicInteger counter = new AtomicInteger();

    @RequestMapping("/counter")
    public Counter getCounter(){
        return new Counter(counter.get());
    }

    @RequestMapping("/counterinc")
    public Counter getAndIncrementCounter(){
        return new Counter(counter.incrementAndGet());
    }
}