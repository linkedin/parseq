# Binaries we use
NPM = npm

BROWSERIFY = ./node_modules/browserify/bin/cmd.js
ISTANBUL = ./node_modules/istanbul/lib/cli.js
JSHINT = ./node_modules/jshint/bin/jshint
MOCHA = ./node_modules/mocha/bin/_mocha
PHANTOMJS = ./node_modules/phantomjs/bin/phantomjs
UGLIFY = ./node_modules/uglify-js/bin/uglifyjs

# Module def
MODULE = parseq-tracevis
MODULE_JS = $(MODULE).js
MODULE_MIN_JS = $(MODULE).min.js
MODULE_TAR_GZ = $(MODULE).tar.gz

# Various files
SRC_FILES = $(wildcard lib/*.js lib/*/*.js lib/*/*/*.js)
TEST_UNIT_FILES = $(wildcard test/unit/*.js test/unit/**/*.js)
TEST_INT_FILES = $(wildcard test/int/*.js)
TEST_FILES = $(TEST_UNIT_FILES) $(TEST_INT_FILES)

COVERAGE_UNIT=build/coverage/unit

# Targets
.PHONY: all \
		test test-unit test-int lint \
		clean fullclean

all: build test

build: build/$(MODULE_JS) build/$(MODULE_MIN_JS) build/$(MODULE)

build/$(MODULE_JS): browser.js node_modules $(SRC_FILES)
	mkdir -p build
	$(BROWSERIFY) -x node_modules/d3/index-browserify.js $(BROWSERIFY_OPTS) $< > $@

build/$(MODULE_MIN_JS): build/$(MODULE_JS)
	$(UGLIFY) $(UGLIFY_OPTS) $< > $@

build/$(MODULE): bootstrap css index.html | build/$(MODULE)/js
	mkdir -p $@
	cp -r $^ $@
	sed -e "s|node_modules/d3/d3.js|js/d3.min.js|" -e "s|build/$(MODULE_JS)|js/$(MODULE_MIN_JS)|" index.html > build/$(MODULE)/index.html
	@echo

build/$(MODULE)/js: build/$(MODULE_JS) build/$(MODULE_MIN_JS) node_modules/d3/d3.min.js
	mkdir -p $@
	cp $? $@

dist: test dist/$(MODULE_TAR_GZ)

dist/$(MODULE_TAR_GZ): build/$(MODULE)
	mkdir -p dist
	tar cfzC $@ build $(MODULE)
	@echo

test: test-unit test-int lint

test-unit: $(COVERAGE_UNIT)

$(COVERAGE_UNIT): $(TEST_UNIT_FILES) node_modules
	$(ISTANBUL) cover $(MOCHA) --dir $@ -x **/lib/render/** -- $(MOCHA_OPTS) $(TEST_UNIT_FILES)

test-int: build/test-int

build/test-int: $(TEST_INT_FILES) node_modules build/$(MODULE)
	$(PHANTOMJS) $^ | tee $@
	@echo

lint: build/lint

build/lint: $(SRC_FILES) $(TEST_FILES)
	$(JSHINT) $^ | tee $@
	@echo

clean:
	rm -rf build dist

fullclean: clean
	rm -rf ./node_modules

node_modules: package.json
	$(NPM) install
