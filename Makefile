PACKAGE=jsdsi
PACKAGES=jsdsi jsdsi.test jsdsi.sexp jsdsi.xml
JAR=jar
JAVAC=javac
JAVADOC=javadoc
SOURCE=README LICENSE Makefile cache? certs.*.? jsdsi.properties
HTML=index.html tool.html
USER=sajma
HOST=jsdsi.sourceforge.net
DEST=/home/groups/j/js/jsdsi/htdocs
FULLDEST=$(USER)@$(HOST):$(DEST)

all: build

build:
	find . -name "*.java" | grep -v src | xargs $(JAVAC) -source 1.4 -d .
	$(JAR) cf $(PACKAGE).jar $(PACKAGE)

docs:
	$(JAVADOC) -author -package -d javadoc -source 1.4 -sourcepath .. $(PACKAGES)

dist-html: $(HTML)
	scp $(HTML) $(FULLDEST)

dist-source: $(SOURCE)
	find . -name "*.java" | xargs zip -q $(PACKAGE).src.zip $(SOURCE)
	scp $(PACKAGE).src.zip $(FULLDEST)

dist-build: build
	zip -q $(PACKAGE).jar.zip $(PACKAGE).jar jsdsi.properties
	scp $(PACKAGE).jar.zip $(FULLDEST)

dist-docs: docs
	scp -r javadoc/* $(FULLDEST)/$(PACKAGE)-javadoc

dist: dist-html dist-source dist-build dist-docs

clean:
	rm -f `find $(PACKAGE) -follow -name "*.class"`