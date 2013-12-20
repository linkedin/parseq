/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the 'License'); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

var d3Treeify = require('../trace/d3Treeify'),
    util = require('../trace/util');

module.exports = render;

var WIDTH = 960,
    MARGIN = { top: 40, bottom: 10, left: 10, right: 250 },
    BAR_HEIGHT = 25,
    BAR_SPACING = 10,
    MIN_BAR_WIDTH = 3,

    // How long in ms to take when hiding / revealing elements
    TRANSITION_TIME = 250,

    // When dimming elements that aren't highlighted, this is the alpa we use
    DIMMED_ALPHA = 0.3;

function render(root, graph) {
  root.classed('waterfallview', true);

  var svg = root.append('svg')
                .attr('width', WIDTH + MARGIN.left + MARGIN.right),
      vis = svg.append('g')
               .attr('transform', 'translate(' + MARGIN.left + ',' + MARGIN.top + ')');

  redraw(svg, vis, d3Treeify(graph), buildAncestorMap(graph), 0, { x: 0, y: 0 });
}

function buildAncestorMap(g) {
  var ancestors = {};
  function preorder(u) {
    var uAncestors = ancestors[u] = {};
    if (u !== null) {
      var parent = g.parent(u);
      Object.keys(ancestors[parent]).forEach(function(v) {
        uAncestors[v] = true;
      });
      uAncestors[parent] = true;
    }
    g.children(u).forEach(function(v) {
      preorder(v);
    });
  }
  preorder(null);
  return ancestors;
}

function redraw(svg, vis, data, ancestors, transitionTime, rootCoords) {
  var nodes = d3.layout.hierarchy()
    // Tell D3 not to overwrite our 'value' property
    .value(undefined)
    .sort(function(a, b) { return a.order - b.order; })
    (data);

  nodes = nodes.filter(function(d) { return d.id !== null; });

  var x = d3.scale.linear()
    .domain([0, d3.max(nodes, function(d) { return d.startMillis + d.totalMillis; })])
    .range([0, WIDTH]);

  var height = nodes.length * (BAR_HEIGHT + BAR_SPACING);
  var y = d3.scale.linear()
    .domain([0, nodes.length])
    .range([0, height]);

  // Dynamically determine height required to render all tasks and resize the
  // svg canvas if needed.
  var svgHeight = height + MARGIN.top + MARGIN.bottom;
  if (svg.attr('height') - svgHeight < 0) {
    svg.attr('height', svgHeight);
  }

  redrawGrid(svg, x, transitionTime, svgHeight);

  var bars = vis
    .selectAll('g .trace')
    .data(nodes, function(d) { return d.id; });

  var barsEnter = bars
    .enter()
    .append('g')
      .classed('trace', true)
      .classed('composite', function(d) {
          return d.children || d._children;
      })
      .classed('hidden', false)
      .style('opacity', 1e-6)
      .on('mouseover', mouseover)
      .on('mouseout', mouseout);

  bars.classed('collapsed', function(d) { return d._children; });

  barsEnter
    .attr('transform', 'translate(' + rootCoords.x + ',' + rootCoords.y + ')')
    .append('rect')
      .attr('rx', 3)
      .attr('width', function(d) { return Math.max(x(d.runMillis), MIN_BAR_WIDTH); })
      .attr('height', function() { return BAR_HEIGHT; })
      .each(function(d) { d3.select(this).classed(d.resultType.toLowerCase(), true); })
      .style('stroke-opacity', 0);

  barsEnter
    .attr('transform', 'translate(' + rootCoords.x + ',' + rootCoords.y + ')')
    .append('rect')
      .attr('rx', 3)
      .attr('width', function(d) { return Math.max(x(d.totalMillis), MIN_BAR_WIDTH); })
      .attr('height', function() { return BAR_HEIGHT; })
      .each(function(d) { d3.select(this).classed(d.resultType.toLowerCase(), true); })
      .style('fill-opacity', 0.3);

  var textEnter = barsEnter
    .append('text')
      .attr('dy', '1.3em')
      .attr('dx', '1em');

  textEnter
    .append('tspan')
      .classed('collapse-toggle', true);

  bars
    .on('click', toggleCollapse)
    .selectAll('text tspan.collapse-toggle')
      .text(function(d) {
        if (d.children) {
          return '[-] ';
        } else if (d._children) {
          return '[+] ';
        } else {
          return '';
        }
      });

  textEnter
    .append('tspan')
      .text(function(d) { return d.name + ' (' + util.alignMillis(d.totalMillis) + ' ms)'; });

  bars
    .style('pointer-events', 'none')
    .transition()
      .duration(transitionTime)
      .attr('transform', function(d, i) {
        return 'translate(' + x(d.startMillis) + ',' + y(i) + ')';
      })
      .style('opacity', alpha)
      .each('end', function() {
        d3.select(this).style('opacity', alpha);
        d3.select(this).style('pointer-events', undefined);
      });

  bars
    .exit()
      .style('pointer-events', 'none')
      .classed('hidden', true)
      .transition()
        .duration(transitionTime)
        .attr('transform', 'translate(' + rootCoords.x + ',' + rootCoords.y + ')')
        .style('opacity', 1e-6)
        .each('end', function() { d3.select(this).style('pointer-events', undefined); })
        .remove();

  function mouseover(d) {
    if (d.children || d._children) {
      vis.selectAll('g.trace')
        .filter(function(e) { return e !== d && !ancestors[e.id][d.id]; })
        .each(function(e) { e.waterfallDimmed = true; })
        .style('opacity', alpha);
    }
  }

  function mouseout() {
    vis.selectAll('g.trace')
      .filter(function(d) { return d.waterfallDimmed; })
      .style('opacity', function() {
        return d3.select(this).classed('hidden') ? 0 : 1;
      })
      .each(function(d) { delete d.waterfallDimmed; });
  }

  function toggleCollapse(d, i) {
    if (d3.event.shiftKey) {
      recursiveCollapse(d, d.children);
    } else {
      collapseOne(d, d.children);
    }
    redraw(svg, vis, data, ancestors, TRANSITION_TIME, { x: x(d.startMillis), y: y(i) });
  }

  function recursiveCollapse(d, collapse) {
    collapseOne(d, collapse);
    var children = d.children || d._children || [];
    children.forEach(function(child) {
      recursiveCollapse(child, collapse);
    });
  }

  function collapseOne(d, collapse) {
    if (collapse) {
      if (d.children) {
        d._children = d.children;
        delete d.children;
      }
    } else {
      if (d._children) {
        d.children = d._children;
        delete d._children;
      }
    }
  }
}

function alpha(d) {
  return d.waterfallDimmed ? DIMMED_ALPHA : 1;
}

function redrawGrid(svg, x, transitionTime, height) {
  function addOrUpdate(type) {
    var selected = svg.select('g.x-axis.' + type);
    if (selected.empty()) {
      selected = svg
        .insert('g', ':first-child')
        .attr('class', 'x-axis ' + type)
        .attr('transform', 'translate(' + MARGIN.left + ',' + (MARGIN.top - 15) + ')')
        .append('g');
    }
    return selected;
  }

  addOrUpdate('label')
    .call(d3.svg.axis()
    .scale(x)
    .orient('top')
    .innerTickSize(6)
    .outerTickSize(3));

  addOrUpdate('grid')
    .transition()
    .duration(transitionTime)
    .call(d3.svg.axis()
      .scale(x)
      .orient('top')
      .innerTickSize(-height)
      .tickFormat('')
      .ticks([10]));

  addOrUpdate('minor-grid')
    .transition()
    .duration(transitionTime)
    .call(d3.svg.axis()
      .scale(x)
      .orient('top')
      .innerTickSize(-height)
      .tickFormat('')
      .ticks([100]));
}
