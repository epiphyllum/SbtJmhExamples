package jmh

import java.util.concurrent.{Executors, TimeUnit}

import com.lmax.disruptor.dsl.{ProducerType, Disruptor}
import com.lmax.disruptor._

class DisruptorQueue(pt: ProducerType) {

  // Executor that will be used to construct new threads for consumers
  private[this] val executor = Executors.newCachedThreadPool()

  // Construct the Disruptor
  private[this] val disruptor = {
    // The factory for the event
    val factory = new EventFactory[Event] {
      def newInstance() = new Event()
    }

    // Specify the size of the ring buffer, must be power of 2.
    val bufferSize = 1024

    val d = new Disruptor[Event](factory, bufferSize, executor, pt, new BlockingWaitStrategy)

    // Connect the handler
    d.handleEventsWith(new InternalEventHandler(1), new InternalEventHandler(2))

    // Start the Disruptor, starts all threads running
    d.start()

    d
  }

  // Get the ring buffer from the Disruptor to be used for publishing.
  private[this] val ringBuffer = disruptor.getRingBuffer()

  private[this] val TRANSLATOR =
    new EventTranslatorOneArg[Event, Long]() {
      def translateTo(event: Event, sequence: Long, value: Long) {
        event.set(value)
      }
    }

  def push(value: Long): Unit = {
    ringBuffer.publishEvent(TRANSLATOR, value)
  }

  def close(): Unit = {
    disruptor.shutdown(5, TimeUnit.SECONDS)
    executor.shutdown()
    executor.awaitTermination(5, TimeUnit.SECONDS)
  }

  class Event {
    private[this] var value: Long = 0L

    def set(value: Long) {
      this.value = value
    }

    def get = value
  }

  class InternalEventHandler(id: Long) extends EventHandler[Event] {
    private[this] var summary: Long = 0

    def onEvent(event: Event, sequence: Long, endOfBatch: Boolean) {
      summary += 1
    }
  }

}
