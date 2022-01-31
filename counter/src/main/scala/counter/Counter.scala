package counter

import spinal.core._

case class Counter(width: Int) extends Component {
  val io = new Bundle {
    val clear = in Bool ()
    val value = out UInt (width bits)
    val full  = out Bool ()
  }

  val counter = Reg(UInt(width bits)) init (0)

  io.value := counter
  io.full  := False

  //TODO define the logic
  when(counter.andR) {
    io.full := True
  }.elsewhen(io.clear) {
    // io.value := U(io.value.range -> true)
    // io.value := (default -> true)
    counter.clearAll()
  }.otherwise {
    counter := counter + 1
  }
}
