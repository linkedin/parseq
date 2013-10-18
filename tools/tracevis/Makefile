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

TEST_UNIT_COV = build/coverage/unit
TEST_INT_COV = build/coverage/int
TEST_ALL_COV = build/coverage/all

# Targets
.PHONY: all \
		test test-unit test-int lint coverage \
		clean fullclean

.DELETE_ON_ERROR:

all: build test

build: build/$(MODULE_JS) build/$(MODULE_MIN_JS) build/$(MODULE)

build/$(MODULE_JS): browser.js node_modules $(SRC_FILES)
	mkdir -p $(@D)
	$(BROWSERIFY) -x node_modules/d3/index-browserify.js $(BROWSERIFY_OPTS) $< > $@

build/$(MODULE_MIN_JS): build/$(MODULE_JS)
	$(UGLIFY) $(UGLIFY_OPTS) $< > $@

build/$(MODULE): bootstrap css index.html | build/$(MODULE)/js
	mkdir -p $@
	cp -r $^ $@
	sed -e "s|node_modules/d3/d3.js|js/d3.min.js|" -e "s|build/$(MODULE_JS)|js/$(MODULE_MIN_JS)|" index.html > build/$(MODULE)/index.html
	@echo

build/$(MODULE)/js: build/$(MODULE_JS) build/$(MODULE_MIN_JS) node_modules/d3/d3.min.js
	rm -rf $@
	mkdir -p $@
	cp $^ $@

dist: test dist/$(MODULE_TAR_GZ)

dist/$(MODULE_TAR_GZ): build/$(MODULE)
	mkdir -p $(@D)
	tar cfzC $@ build $(MODULE)
	@echo

test: coverage

test-unit: $(TEST_UNIT_COV) lint

$(TEST_UNIT_COV): $(TEST_UNIT_FILES) $(SRC_FILES) | node_modules
	rm -rf $@
	$(ISTANBUL) cover $(MOCHA) --dir $@ -x **/lib/render/** --report none -- $(MOCHA_OPTS) $(TEST_UNIT_FILES)

test-int: $(TEST_INT_COV)

build/instrumentation:
	mkdir -p $@

build/instrumentation/lib: $(SRC_FILES)
	rm -rf $@
	mkdir -p $(@D)
	$(ISTANBUL) instrument lib --output $@

build/instrumentation/%.js: %.js | build/instrumentation
	cp $< $@

build/instrumentation/$(MODULE_JS): build/instrumentation/browser.js build/instrumentation/index.js node_modules build/instrumentation/lib
	$(BROWSERIFY) -x node_modules/d3/index-browserify.js $< > $@

build/instrumentation/$(MODULE): build/$(MODULE) build/instrumentation/$(MODULE_JS)
	rm -rf $@
	cp -r $< $@
	cp build/instrumentation/$(MODULE_JS) $@/js/$(MODULE_MIN_JS)

$(TEST_INT_COV): $(TEST_INT_FILES) build/instrumentation/$(MODULE)
	mkdir -p $(@D)
	$(PHANTOMJS) $<
	touch $@
	@echo

coverage: $(TEST_ALL_COV)

$(TEST_ALL_COV): $(TEST_UNIT_COV) $(TEST_INT_COV)
	rm -rf $@
	$(ISTANBUL) report --root build/coverage --dir $@ lcov

lint: build/lint

build/lint: $(SRC_FILES) $(TEST_FILES)
	mkdir -p $(@D)
	$(JSHINT) $?
	touch $@
	@echo

clean:
	rm -rf build dist

fullclean: clean
	rm -rf ./node_modules

node_modules: package.json
	$(NPM) install
