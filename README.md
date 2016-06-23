# jvstm-benchmarks

This repository contains the benchmarks used to evaluate JVSTM's tuning system, as well as two quick-and-dirty helper applications to parse result data.

## Benchmarks

* Vacation (part of the STAMP suite - <https://github.com/chrisseaton/stamp> and <http://csl.stanford.edu/~christos/publications/2008.stamp.iiswc.pdf>)
* STMBench7 (<http://www.eurotm.org/tmbenchmarks> and <https://infoscience.epfl.ch/record/101108>)

For configurations and parameters refer to the original benchmark's documentation - to make them compatible with JVSTM and tuning, some negligible changes were made in the benchmarks' code, but they do not affect the general behaviour or command-line options. Additional parameter information can be found on the JVSTM+tuning project's run configurations (<https://github.com/zsimoes/jvstm>).

## Data processors

DataProcessors contains two Eclipse Java projects to parse benchmark data.

* ProcessVacationResults reads JVSTM logs and log folders, extracts execution time and other ouput, and generates gnuplot (and intermediate data) files ready to plot data.
  JVSTM logs are produced by the tuning statistics collection.
* ProcessSB7Results processes STMBench7 results produced directly by the benchmark into gnuplot files.

Gnuplot file templates are embedded directly in the code. Refer to each project's command line help message for a complete list of options.
