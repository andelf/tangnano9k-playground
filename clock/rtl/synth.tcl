#!/usr/bin/env gw_sh
# A simple gw_sh build script
# by andelf

set build_script_path [ file dirname [ file normalize [ info script ] ] ]

source $build_script_path/common.tcl

run syn
run pnr
