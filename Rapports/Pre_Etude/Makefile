SOURCES = $(shell ls | grep tex)

.PHONY: all main clean

all: main

main: main.tex $(SOURCES)
	pdflatex -shell-escape main.tex
	bibtex main
	pdflatex -shell-escape main.tex
	pdflatex -shell-escape main.tex

clean:
	rm -rf _minted*
	rm -f *.aux *.bbl *.blg *.log *.out *.toc *.pdf