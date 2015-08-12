package jmh

import java.util.concurrent._

import com.lmax.disruptor._
import com.lmax.disruptor.dsl.ProducerType
import org.openjdk.jmh.annotations._

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(3)
@State(Scope.Thread)
class Benchmarks {

  var blockingWaitStrategy: DisruptorQueue = _

  var liteBlockingWaitStrategy: DisruptorQueue = _

  var busySpinWaitStrategy: DisruptorQueue = _

  var yieldingWaitStrategy: DisruptorQueue = _

  var sleepWaitStrategy: DisruptorQueue = _

  var abq: ArrayBlockingQueue[Int] = _

  var lbq: LinkedBlockingDeque[Int] = _

  var executor: ExecutorService = _

  @Setup
  def setup: Unit = {
    blockingWaitStrategy = new DisruptorQueue(ProducerType.MULTI, new BlockingWaitStrategy)
    liteBlockingWaitStrategy = new DisruptorQueue(ProducerType.MULTI, new LiteBlockingWaitStrategy)
    busySpinWaitStrategy = new DisruptorQueue(ProducerType.MULTI, new BusySpinWaitStrategy)
    yieldingWaitStrategy = new DisruptorQueue(ProducerType.MULTI, new YieldingWaitStrategy)
    sleepWaitStrategy = new DisruptorQueue(ProducerType.MULTI, new SleepingWaitStrategy())
    abq = new ArrayBlockingQueue[Int](10000)
    lbq = new LinkedBlockingDeque[Int]()
    executor = Executors.newCachedThreadPool()
    executor.execute(newWorker(abq))
    executor.execute(newWorker(lbq))
  }

  def newWorker(queue: BlockingQueue[Int]) = {
    new Runnable {
      override def run(): Unit = {
        try {
          while (true) {
            queue.take()
          }
        }
        catch {
          case e: Exception =>
        }
        finally {}
      }
    }
  }

  @TearDown
  def teardown: Unit = {
    abq.clear()
    lbq.clear()
    blockingWaitStrategy.close()
    liteBlockingWaitStrategy.close()
    busySpinWaitStrategy.close()
    yieldingWaitStrategy.close()
    sleepWaitStrategy.close()
    executor.shutdownNow()
    executor.awaitTermination(10, TimeUnit.SECONDS)
  }

  @Benchmark
  @Warmup(iterations = 10)
  @Measurement(iterations = 10)
  def blockingWaitStrategyTest(): Unit = {
    blockingWaitStrategy.push(213)
  }

  @Benchmark
  @Warmup(iterations = 10)
  @Measurement(iterations = 10)
  def liteBlockingWaitStrategyTest(): Unit = {
    liteBlockingWaitStrategy.push(213)
  }

  @Benchmark
  @Warmup(iterations = 10)
  @Measurement(iterations = 10)
  def busySpinWaitStrategyTest(): Unit = {
    busySpinWaitStrategy.push(213)
  }

  @Benchmark
  @Warmup(iterations = 10)
  @Measurement(iterations = 10)
  def yieldingWaitStrategyTest(): Unit = {
    yieldingWaitStrategy.push(213)
  }

  @Benchmark
  @Warmup(iterations = 10)
  @Measurement(iterations = 10)
  def sleepWaitStrategyTest(): Unit = {
    sleepWaitStrategy.push(213)
  }

  @Benchmark
  @Warmup(iterations = 10)
  @Measurement(iterations = 10)
  def arrayBlockingQueue(): Unit = {
    abq.offer(213)
  }

  @Benchmark
  @Warmup(iterations = 10)
  @Measurement(iterations = 10)
  def linkedBlockingQueue(): Unit = {
    lbq.offer(213)
  }
}
