package jmh

import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.openjdk.jmh.annotations._

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@State(Scope.Thread)
class Benchmarks {

  var mapper:ObjectMapper = _

  var list: Seq[User] = _

  var json: String = _

  @Setup
  def setup: Unit = {
    mapper = {
      val m = new ObjectMapper()
      m.registerModule(DefaultScalaModule)
      m
    }
    list = Seq.fill(10)(User("hogefuga", 32))
    json = mapper.writeValueAsString(list)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput, Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  def write(): Unit = {
    mapper.writeValueAsString(list)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.Throughput, Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  def read(): Unit = {
    mapper.readValue(json, classOf[Seq[User]])
  }
}

case class User(name: String, age: Long)
