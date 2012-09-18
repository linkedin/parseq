NODE?=node
NPM?=npm
MOCHA?=./node_modules/mocha/bin/mocha
MOCHA_OPTS?=-R spec

ALL: test

node_modules:
	@npm install

test: node_modules
	$(MOCHA) $(MOCHA_OPTS) --recursive test

clean:
	rm -rf ./node_modules

.PHONY: test clean
