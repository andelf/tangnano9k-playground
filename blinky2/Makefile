YOSYS ?= yowasp-yosys
NEXTPNR ?= yowasp-nextpnr-gowin
PROGRAMMER ?= openFPGALoader

PROGRAMMER_ARGS ?= -b tangnano9k


SOURCES = src/main/scala/demo/TopLevel.scala
VERILOG_FILE := rtl/TopLevel.v
BITSTREAM_FILE := rtl/TopLevel.fs

DEVICE := GW1NR-LV9QN88PC6/I5

all: $(BITSTREAM_FILE)

$(VERILOG_FILE): $(SOURCES) build.sbt
	sbt "runMain demo.Main"

rtl/top-synth.json: $(VERILOG_FILE)
	$(YOSYS) -p "read_verilog $^; synth_gowin -json $@" $^

rtl/top-netlist.json: rtl/top-synth.json rtl/tang-nano-9k.cst
	$(NEXTPNR) --json $< --write $@ --device $(DEVICE) --cst rtl/tang-nano-9k.cst

# NOTE: Actually it's a GW1NR-9C. This is a mistake in prj apycula.
$(BITSTREAM_FILE): rtl/top-netlist.json
	gowin_pack -d GW1N-9C -o $@ $^

clean:
	rm -rf impl $(VERILOG_FILE) rtl/*.json

flash: $(BITSTREAM_FILE)
	$(PROGRAMMER) $(PROGRAMMER_ARGS) $<

.PHONY: all clean flash
