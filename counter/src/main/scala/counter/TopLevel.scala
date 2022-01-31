package counter

import spinal.core._

class TopLevel extends Component {
  val io = new Bundle {
    val reset_button = in Bool ()
    val xtal_in      = in Bool ()
    val leds         = out UInt (6 bits)
  }
  noIoPrefix()

  val clk = ClockDomain(
    clock = io.xtal_in,
    reset = io.reset_button,
    config = ClockDomainConfig(resetActiveLevel = LOW)
  )
  val coreArea = new ClockingArea(clk) {
    // must be in this Clocking Area
    val counter = new Counter(28)
    counter.io.clear    := ~io.reset_button
    io.leds(5 downto 1) := ~counter.io.value(27 downto 23)
    io.leds(0)          := ~counter.io.full

  }
}

// Run this main to generate the RTL
object Main {
  def main(args: Array[String]): Unit = {
    new java.io.File("rtl").mkdirs
    SpinalConfig(targetDirectory = "rtl").generateVerilog(new TopLevel)
  }
}
