package jmh

import java.util.Random
import java.util.concurrent.{ThreadLocalRandom, TimeUnit}

import jmh.BenchmarkStates.BenchmarkState
import org.openjdk.jmh.annotations._

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
class Benchmarks {

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput, Mode.AverageTime))
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  def random(state: BenchmarkState): Unit = {
    state.random.nextInt(100)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput, Mode.AverageTime))
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  def tlr(state: BenchmarkState): Unit = {
    ThreadLocalRandom.current().nextInt(100)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput, Mode.AverageTime))
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  def nanotime(state: BenchmarkState): Unit = {
    System.nanoTime()
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput, Mode.AverageTime))
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  def millitime(state: BenchmarkState): Unit = {
    System.currentTimeMillis()
  }
}

object BenchmarkStates {

  @State(Scope.Benchmark)
  class BenchmarkState {
    val random = new Random()
  }
}
