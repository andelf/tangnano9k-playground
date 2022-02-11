# Genreated by project template

# GOWINHOME=

ifneq ($(GOWINHOME),)
GW_SH := $(GOWINHOME)/IDE/bin/gw_sh
else
GW_SH := gw_sh
endif

SOURCES = src/main/scala/lcd/TopLevel.scala
VERILOG_FILE := rtl/TopLevel.v
BITSTREAM_FILE := impl/pnr/TopLevel.fs
REPORT_FILE := impl/pnr/TopLevel.rpt.txt

all: $(BITSTREAM_FILE)

$(BITSTREAM_FILE) $(REPORT_FILE): $(VERILOG_FILE) rtl/*.tcl
	$(GW_SH) rtl/synth.tcl
	@cat $(REPORT_FILE) | grep -A32 "Resource Usage Summary"

$(VERILOG_FILE): $(SOURCES) build.sbt rtl/tang-nano-9k.cst
	sbt "runMain lcd.Main"

clean:
	rm -rf impl $(VERILOG_FILE)

flash: $(BITSTREAM_FILE)
	openFPGALoader -b tangnano9k $<

.PHONY: all clean flash
