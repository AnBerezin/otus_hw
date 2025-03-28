package ru.calculator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMHTest {
    private static long COUNTER = 500_000_000;
    private Summator summator;

    public static void main(String[] args) throws RunnerException {
        var opt = new OptionsBuilder()
                .include(JMHTest.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void setup() throws Exception {
        summator = new Summator();
    }

    @Benchmark
    public void summatorTest() {
        for (var idx = 0; idx < COUNTER; idx++) {
            var data = new Data(idx);
            summator.calc(data);
        }
    }
}
