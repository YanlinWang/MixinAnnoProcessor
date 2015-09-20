#!/usr/bin/env python
import subprocess

subprocess.call("ruby computePositions.rb", shell=True)
subprocess.call("xelatex paper.tex", shell=True)
