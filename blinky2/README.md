## blinky2

### Usage

**NOTE**: Before you start, you should have `sbt` installed.

```shell
cd blinky2

sbt "runMain demo.Main"

# then the project with Gowin IDE
```

### For Linux

Install [openFPGALoader](https://github.com/trabucayre/openFPGALoader).
Add `Gowin/IDE/bin` to your `PATH`.
Or set `GOWINHOME` environment variable.

```
openFPGALoader --detect

make        # compile to verilog, synth, PnR

make flash  # use openFPGALoader to program
```

### Directories

- `src/main`: Spinal code in Scala
- `rtl/`: Build scripts, physical constraits file
- `rtl/*.v`: The generated verilog file
