#!/bin/zsh

if [ $1 ]
then
    case "$1" in
    bib )       
        latex paper.tex
        bibtex paper     
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
latex paper.tex
open *.pdf

rm footempfile.txt
rm *.bbl
rm *.blg
rm *.log
rm *.aux
rm *-old