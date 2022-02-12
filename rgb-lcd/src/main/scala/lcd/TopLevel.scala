package lcd

import spinal.core._

// IDIV_SEL 0~63
// FBDIV_SEL 0~63
// ODIV_SEL 2,4,8,16,32,48,64,80,96,112,128
// DYN_SDIV_SEL 2~128
class rPLL(idivSel: Int, fbdivSel: Int, odivSel: Int) extends BlackBox {
  val generic = new Generic {
    val FCLKIN           = "27"
    val DYN_IDIV_SEL     = "false"
    val IDIV_SEL         = idivSel
    val DYN_FBDIV_SEL    = "false"
    val FBDIV_SEL        = fbdivSel
    val DYN_ODIV_SEL     = "false"
    val ODIV_SEL         = odivSel
    val PSDA_SEL         = "0000"
    val DYN_DA_EN        = "true"
    val DUTYDA_SEL       = "1000"
    val CLKOUT_FT_DIR    = B"1"
    val CLKOUTP_FT_DIR   = B"1"
    val CLKOUT_DLY_STEP  = 0
    val CLKOUTP_DLY_STEP = 0
    val CLKFB_SEL        = "internal"
    val CLKOUT_BYPASS    = "false"
    val CLKOUTP_BYPASS   = "false"
    val CLKOUTD_BYPASS   = "false"

    val DYN_SDIV_SEL = 2
    val CLKOUTD_SRC  = "CLKOUT"
    val CLKOUTD3_SRC = "CLKOUT"
    val DEVICE       = "GW1NR-9C"
  }
  val io = new Bundle {
    val CLKIN    = in Bool ()
    val CLKFB    = in Bool ()
    val RESET    = in Bool ()
    val RESET_P  = in Bool ()
    val FBDSEL   = in Bits (6 bits)
    val IDSEL    = in Bits (6 bits)
    val ODSEL    = in Bits (6 bits)
    val PSDA     = in Bits (4 bits)
    val DUTYDA   = in Bits (4 bits)
    val FDLY     = in Bits (4 bits)
    val CLKOUT   = out Bool ()
    val LOCK     = out Bool ()
    val CLKOUTP  = out Bool ()
    val CLKOUTD  = out Bool ()
    val CLKOUTD3 = out Bool ()
  }
  noIoPrefix()
  setBlackBoxName("rPLL")

  def assignDefaults = {
    io.RESET   := False
    io.RESET_P := False
    io.CLKFB   := False
    io.FBDSEL  := B(0, 6 bits)
    io.IDSEL   := B(0, 6 bits)
    io.ODSEL   := B(0, 6 bits)
    io.DUTYDA  := B(0, 4 bits)
    io.PSDA    := B(0, 4 bits)
    io.FDLY    := B(0, 4 bits)
  }
}

class TopLevel extends Component {
  val io = new Bundle {
    val user_button  = in Bool ()
    val reset_button = in Bool ()
    val xtal_in      = in Bool ()
    val leds         = out UInt (6 bits)
    val lcd_de       = out Bool ()
    val lcd_hsync    = out Bool ()
    val lcd_vsync    = out Bool ()
    val lcd_dclk     = out Bool ()
    val lcd_r        = out UInt (5 bits)
    val lcd_g        = out UInt (6 bits)
    val lcd_b        = out UInt (5 bits)
  }
  // or else .cst requires a `io_` prefix.
  noIoPrefix()

  // io.leds(5 downto 0) := U"111111"

  val rpll = new rPLL(8, 1, 80) // ?6MHz
  rpll.assignDefaults
  rpll.io.CLKIN := io.xtal_in

  io.lcd_dclk := rpll.io.CLKOUT // rpll.io.CLKOUT
  // io.lcd_de   := False // DE mode

  val clk = ClockDomain(
    clock = rpll.io.CLKOUT,
    reset = io.reset_button,
    config = ClockDomainConfig(resetActiveLevel = LOW)
  )
  val coreArea = new ClockingArea(clk) {
    val leds = Reg(UInt(6 bits)) init (U"101010")
    io.leds <> ~leds

    val counter  = Reg(UInt(32 bits)) init (0)
    val counterX = Reg(UInt(32 bits)) init (0)
    val counterY = Reg(UInt(32 bits)) init (0)

    val pixel = Reg(UInt(24 bits)) init (0xffffff)

    io.lcd_r := pixel(23 downto 19)
    io.lcd_g := pixel(15 downto 10)
    io.lcd_b := pixel(7 downto 3)

    val THP = 2
    val THB = 41
    val THD = 480
    val THF = 2

    val TVP = 11
    val TVB = 11
    val TVD = 272
    val TVF = 2

    counterX := counterX + 1
    when(counterX === THP + THB + THD + THF) {
      counterX := 0
      counterY := counterY + 1
    }
    when(counterY === TVP + TVB + TVD + TVF) {
      counterY := 0
    }

    io.lcd_hsync := !(counterX >= THP)
    io.lcd_vsync := !(counterY >= TVP)
    io.lcd_de := (counterY >= TVP + TVB && counterY <= TVP + TVB + TVD && counterX >= THP + THB && counterX <= THP + THB + THD)

    when(counterX > THP + THB && counterX <= THP + THB + 60) {
      pixel := 0xffffff
    }.elsewhen(counterX > THP + THB + 60 && counterX <= THP + THB + 120) {
      pixel := 0xffff00
    }.elsewhen(counterX > THP + THB + 120 && counterX <= THP + THB + 180) {
      pixel := 0x00ffff
    }.elsewhen(counterX > THP + THB + 180 && counterX <= THP + THB + 240) {
      pixel := 0x00ff00
    }.elsewhen(counterX > THP + THB + 240 && counterX <= THP + THB + 300) {
      pixel := 0xff00ff
    }.elsewhen(counterX > THP + THB + 300 && counterX <= THP + THB + 360) {
      pixel := 0xff0000
    }.elsewhen(counterX > THP + THB + 360 && counterX <= THP + THB + 420) {
      pixel := 0x0000ff
    }.elsewhen(counterX > THP + THB + 420 && counterX <= THP + THB + 480) {
      pixel := 0x000000
    }.otherwise {
      pixel := 0xffffff
    }

    when(counterY === TVP + TVB + 1) {
      pixel := 0xff0000
    }.elsewhen(counterY === TVP + TVB + 272) {
      pixel := 0xff0000
    }

    counter := counter + 1
    when(counter === 6_000_000) {
      counter := 0
      leds := ~leds
    }.otherwise {
      // io.leds := U"111111"
    }
  }
}

// Run this main to generate the RTL
object Main {
  def main(args: Array[String]): Unit = {
    new java.io.File("rtl").mkdirs
    SpinalConfig(targetDirectory = "rtl").generateVerilog(new TopLevel)
  }
}
