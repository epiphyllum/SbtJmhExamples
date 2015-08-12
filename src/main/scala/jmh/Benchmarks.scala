package jmh

import java.util.concurrent.{ArrayBlockingQueue, LinkedBlockingDeque, TimeUnit}

import com.lmax.disruptor.dsl.ProducerType
import org.openjdk.jmh.annotations._

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@State(Scope.Thread)
class Benchmarks {

  var sdq: DisruptorQueue = _

  var mdq: DisruptorQueue = _

  var abq: ArrayBlockingQueue[Int] = _

  var lbq: LinkedBlockingDeque[Int] = _

  @Setup
  def setup: Unit = {
    sdq = new DisruptorQueue(ProducerType.SINGLE)
    mdq = new DisruptorQueue(ProducerType.MULTI)
    abq = new ArrayBlockingQueue[Int](10000)
    lbq = new LinkedBlockingDeque[Int]()
  }

  @TearDown
  def teardown: Unit = {
    abq.clear()
    lbq.clear()
    sdq.close()
    mdq.close()
  }

  @Benchmark
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  def singleProducerDisruptor(): Unit = {
    sdq.push(213)
  }

  @Benchmark
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  def multiProducerDisruptor(): Unit = {
    mdq.push(213)
  }

  @Benchmark
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  def arrayBlockingQueue(): Unit = {
    if (abq.remainingCapacity() == 0) {
      abq.clear()
    }
    abq.offer(213)
  }

  @Benchmark
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  def linkedBlockingQueue(): Unit = {
    if (lbq.size() >= 10000) {
      lbq.clear()
    }
    lbq.offer(213)
  }
}
