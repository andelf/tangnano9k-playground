#!/usr/bin/env gw_sh
# A simple gw_sh build script

add_file -type verilog "rtl/TopLevel.v"
add_file -type cst "rtl/tang-nano-9k.cst"

# NOTE: Tang Nano 9k is a GW1NR-9C device! not GW1NR-9
set_device GW1NR-LV9QN88PC6/I5 -name GW1NR-9C

set_option -synthesis_tool gowinsynthesis
set_option -output_base_name TopLevel
set_option -cst_warn_to_error 0
set_option -bit_security 0

