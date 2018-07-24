.PHONY: clean clean-test clean-pyc clean-build docs help
.DEFAULT_GOAL := help
define BROWSER_PYSCRIPT
import os, webbrowser, sys
try:
	from urllib import pathname2url
except:
	from urllib.request import pathname2url

webbrowser.open("file://" + pathname2url(os.path.abspath(sys.argv[1])))
endef
export BROWSER_PYSCRIPT

define PRINT_HELP_PYSCRIPT
import re, sys

for line in sys.stdin:
	match = re.match(r'^([a-zA-Z_-]+):.*?## (.*)$$', line)
	if match:
		target, help = match.groups()
		print("%-20s %s" % (target, help))
endef
export PRINT_HELP_PYSCRIPT
BROWSER := python -c "$$BROWSER_PYSCRIPT"

help:
	@python -c "$$PRINT_HELP_PYSCRIPT" < $(MAKEFILE_LIST)

clean: ## run mvn clean
	mvn clean
	/bin/rm -rf dist

checkrepo: ## checks if remote repo is CRBS
	@therepo=`git remote get-url origin | sed "s/^.*://" | sed "s/\/.*//"` ;\
	if [ "$$therepo" != "CRBS" ] ; then \
	echo "ERROR can only do a release from master repo, not from $$therepo" ; \
	exit 1 ;\
	else \
	echo "Repo appears to be master $$therepo" ; \
	fi

lint: ## check style with checkstyle:checkstyle
	mvn checkstyle:checkstyle

test: ## run tests with mvn test
	mvn test

coverage: ## check code coverage with jacoco
	mvn test jacoco:report
	$(BROWSER) target/site/jacoco/index.html

install: clean ## install the package to local repo
	mvn install

updateversion: ## updates version in pom.xml via maven command
	mvn versions:set

dist: install ## generates distribution suitable for release
	@vers=`grep "<version>" pom.xml | head -n 1 | sed "s/^.*<version>//" | sed "s/<.*//"` ;\
	hvers=`echo $${vers} | sed "s/\./-/g"` ; \
	/bin/mkdir -p dist ;\
	cat aws/basic_cloudformation.json | sed "s/@@VERSION@@/$${vers}/g" > dist/probabilitymapviewer_$${vers}_basic_cloudformation.json ;\
	cp target/probabilitymapviewer-$${vers}-jar-with-dependencies.jar dist/ ;\
	cp README.md dist/. ;\
	sed -i "s/ probabilitymapviewer-.*-jar/ probabilitymapviewer-$${vers}-jar/g" dist/README.md ;\
	sed -i "s/probabilitymapviewer-stack-.*template/probabilitymapviewer-stack-$$hvers\&template/g" dist/README.md ;\
	sed -i "s/releases\/.*\/probabilitymapviewer.*\.json/releases\/$$vers\/probabilitymapviewer\_$$vers\_basic\_cloudformation.json/g" dist/README.md ;\
	ls -l dist/

release: dist ## package and upload a release to s3
	@echo "WARNING Creating new release in 15 seconds. Be sure to read this page first: https://github.com/CRBS/probabilitymapviewer/wiki/Creating-a-new-release"
	@echo "If you dont want to do this hit Ctrl-c now!!!!"
	sleep 15
	@vers=`grep "<version>" pom.xml | head -n 1 | sed "s/^.*<version>//" | sed "s/<.*//"` ;\
	cloudform=probabilitymapviewer_$${vers}_basic_cloudformation.json ;\
	aws s3 cp dist/$$cloudform s3://probabilitymapviewer-releases/$${vers}/$$cloudform --acl public-read ;\
	cp dist/README.md . ;\
	branchy=`git branch --list | egrep "^\*" | sed "s/^\* *//"` ;\
	git commit -m 'updated launch stack link and jar with version' README.md ;\
	git push origin $$branchy ;\
	git tag -a v$${vers} -m 'new release' ;\
	git push origin v$${vers} ;\
	echo "Congratulations on the new release" ;\
	echo "A new tag v$${vers} has been pushed to github as well as a new README.md file" ;\
	echo "Also a cloud formation template has been uploaded to s3://probabilitymapviewer-releases/$${vers}/$$cloudform" ;\
	echo "" ;\
	echo "Be sure to create a new release on https://github.com/CRBS/probabilitymapviewer/releases by following instructions on this wiki" ;\
	echo "https://github.com/CRBS/probabilitymapviewer/wiki/Creating-a-new-release"

