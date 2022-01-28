package demo.clock

import spinal.core._

class Osc(freqDiv: Int) extends BlackBox {
  val generic = new Generic {
    val FREQ_DIV = freqDiv
    val DEVICE   = "GW1NR-9C"
  }
  val io = new Bundle {
    val OSCOUT = out Bool ()
  }
  noIoPrefix()
  setBlackBoxName("OSC")
}

class TopLevel extends Component {
  val io = new Bundle {
    val user_button  = in Bool ()
    val reset_button = in Bool ()
    val xtal_in      = in Bool ()
    val leds         = out UInt (6 bits)
  }
  // or else .cst requires a `io_` prefix.
  noIoPrefix()

  val osc_clk = new Bool()

  val osc = new Osc(100)
  osc_clk <> osc.io.OSCOUT

  val clk = ClockDomain(
    clock = osc_clk, // io.xtal_in,
    reset = io.reset_button,
    config = ClockDomainConfig(resetActiveLevel = LOW)
  )
  val coreArea = new ClockingArea(clk) {
    val leds = Reg(UInt(6 bits)) init (U"000000")
    io.leds <> ~leds
    // to hold 27_000_000
    var counter = Reg(UInt(25 bits)) init (0)
    counter := counter + 1
    // var is_overflow = Bool()
    //is_overflow :=

    when(counter === 27_000_000) {
      counter := 0
      // leds := leds + 1
      leds := ~leds
    }.otherwise {
      // io.leds := U"111111"
    }
  }
}

//Define a custom SpinalHDL configuration with synchronous reset instead of the default asynchronous one. This configuration can be resued everywhere
object MySpinalConfig
    extends SpinalConfig(
      targetDirectory = "rtl",
      device = Device(family = "GW1NR", name = "GW1NR-9C", vendor = "Gowin"),
      defaultClockDomainFrequency = FixedFrequency(27 MHz),
      defaultConfigForClockDomains = ClockDomainConfig(resetKind = SYNC)
    )

// Run this main to generate the RTL
object Main {
  def main(args: Array[String]): Unit = {
    new java.io.File("rtl").mkdirs
    MySpinalConfig.generateVerilog(new TopLevel)
  }
}
