#!/usr/bin/env gw_sh
# A simple gw_sh build script

set __dir__ [ file dirname [ file normalize [ info script ] ] ]

source $__dir__/common.tcl

# NOTE: The following can be replaced by `run all`
run syn
run pnr
