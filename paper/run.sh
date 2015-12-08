#!/bin/bash

if [ $1 ]
then
    case "$1" in
    bib )       
        pdflatex paper.tex
        bibtex paper
        bibtex paper-without-appendix     
    ;;
    * )
        echo "Usage (In terminal with zsh):"
        echo "\t\t./run.sh"
        echo "render and open a new pdf"
        echo "\t\t./run.sh bib"
        echo "render and open a new pdf with bib changes"
    esac   
fi

ruby computePositions.rb
pdflatex paper.tex
open paper.pdf

pdflatex paper-without-appendix.tex
open paper-without-appendix.pdf

rm footempfile.txt
# rm *.bbl
# rm *.blg
rm *.log
rm *-old