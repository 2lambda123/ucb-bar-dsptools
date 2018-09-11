// See LICENSE for license details.

package examples

import chisel3._
import dsptools.{ReplOptionsManager, DspTester}
import dsptools.numbers.DspReal
import org.scalatest.{FlatSpec, Matchers}

class RealAdder extends Module {
  val io = IO(new Bundle {
    val a1 = Input(new DspReal)
    val a2 = Input(new DspReal)
    val c  = Output(new DspReal)
  })

  val register1 = Reg(new DspReal)

  register1 := io.a1 + io.a2

  io.c := register1
}

object RealAdder {
  def main(args: Array[String]) {
    val optionsManager = new ReplOptionsManager
    if(optionsManager.parse(args)) {
      dsptools.Driver.executeFirrtlRepl(() => new RealAdder, optionsManager)
    }
  }
}

class RealAdderTester(c: RealAdder) extends DspTester(c) {
  for {
    iBD <- BigDecimal(0) to 1 by 0.25
    jBD <- BigDecimal(0) to 4 by 0.5
  } {
    val (i, j) = (iBD.toDouble, jBD.toDouble)
    poke(c.io.a1, i)
    poke(c.io.a2, j)
    step(1)

    expect(c.io.c, i + j)
  }
}


class RealAdderSpec extends FlatSpec with Matchers {
  behavior of "adder circuit on blackbox real"

  it should "allow registers to be declared that infer widths" in {
    dsptools.Driver.execute(() => new RealAdder, Array("--backend-name", "firrtl")) { c =>
      new RealAdderTester(c)
    } should be (true)
  }
}
