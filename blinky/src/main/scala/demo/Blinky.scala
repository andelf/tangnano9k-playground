package demo

import spinal.core._

class Blinky extends Component {
  val io = new Bundle {
    val user_button = in Bool ()
    val reset_button = in Bool ()
    val xtal_in = in Bool ()
    val leds = out UInt (6 bits)
  }
  // or else .cst requires a `io_` prefix.
  noIoPrefix()

  val clk = ClockDomain(
    clock = io.xtal_in,
    reset = io.reset_button,
    config = ClockDomainConfig(resetActiveLevel = LOW)
  )
  val coreArea = new ClockingArea(clk) {
    var counter = Reg(UInt(26 bits)) init (0)
    counter := counter + 1

    when(io.user_button) {
      io.leds := counter(25 downto 20)
    }.otherwise {
      io.leds := U"111111"
    }
  }
}

// Run this main to generate the RTL
object Main {
  def main(args: Array[String]): Unit = {
    new java.io.File("rtl").mkdirs
    SpinalConfig(targetDirectory = "rtl").generateVerilog(new Blinky)
  }
}
