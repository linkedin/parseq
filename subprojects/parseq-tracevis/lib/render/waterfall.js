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

var d3Treeify = require('../trace/d3Treeify'), util = require('../trace/util');

module.exports = render;

var WIDTH = 960, MARGIN = {
  top : 40,
  bottom : 10,
  left : 10,
  right : 2130 // the width of each character is 7~8 muliply by the length of the name string(less than 255 chars)
}, BAR_HEIGHT = 25, BAR_SPACING = 10, MIN_BAR_WIDTH = 3,

// How long in ms to take when hiding / revealing elements
TRANSITION_TIME = 250,

// When dimming elements that aren't highlighted, this is the alpa we use
DIMMED_ALPHA = 0.3;

function render(root, graph) {
  root.classed('waterfallview', true);

  var svg = root.append('svg')
      .attr('width', WIDTH + MARGIN.left + MARGIN.right), vis = svg.append('g')
      .attr('transform', 'translate(' + MARGIN.left + ',' + MARGIN.top + ')');

  redraw(svg, vis, d3Treeify(graph), buildAncestorMap(graph), 0, {
    x : 0,
    y : 0
  });
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
  .value(undefined).sort(function(a, b) {
    return a.order - b.order;
  })(data);

  // Remove the fake root node
  nodes = nodes.filter(function(d) {
    return d.id !== null;
  });

  var x = d3.scale.linear().domain([ 0, d3.max(nodes, function(d) {
    return d.startMillis + d.totalMillis;
  }) ]).range([ 0, WIDTH ]);

  var height = nodes.length * (BAR_HEIGHT + BAR_SPACING);
  var y = d3.scale.linear().domain([ 0, nodes.length ]).range([ 0, height ]);

  // Pre-compute the target x, y coordinates for each bar
  nodes.forEach(function(d, i) {
    d.x = x(d.startMillis);
    d.y = y(i);
    d.deleted = false;
  });

  // Dynamically determine height required to render all tasks and resize the
  // svg canvas if needed.
  var svgHeight = height + MARGIN.top + MARGIN.bottom;
  if (svg.attr('height') - svgHeight < 0) {
    svg.attr('height', svgHeight);
  }

  redrawGrid(svg, x, transitionTime, svgHeight);

  var bars = vis.selectAll('g .trace').data(nodes, function(d) {
    return d.id;
  });

  var barsEnter = bars.enter().append('g').classed('trace', true).classed(
      'composite', function(d) {
        return d.children || d._children;
      }).style('opacity', 1e-6).on('mouseover', mouseover).on('mouseout',
      mouseout);

  // Add expand / collapse icon and lines indicating parent / child relationships
  barsEnter.each(function(d) {
    var elem = d3.select(this);
    if (d.children || d._children) {
      elem.append('path').attr('d', 'M10 5 L0 10 L0 0 Z');
      elem.append('line').classed('vertical', true).attr('x1', -5).attr('y1',
          BAR_HEIGHT / 2).attr('x2', -5).attr('y2', BAR_HEIGHT / 2);
    } else if (d.parent.id !== null) {
      elem.append('circle').attr('r', 4).attr('cx', -5).attr('cy',
          BAR_HEIGHT / 2);
    }

    if (d.parent.id !== null) {
      elem.append('line').classed('horizontal', true).attr('x1', -5).attr('y1',
          BAR_HEIGHT / 2).attr('x2', -5).attr('y2', BAR_HEIGHT / 2);
    }
  });

  drawBars(barsEnter, function(d) {
    return x(d.runMillis);
  }).style('stroke-opacity', 0);

  drawBars(barsEnter, function(d) {
    return x(d.totalMillis);
  }).style('fill-opacity', 0.3);

  bars.on('click', toggleCollapse);

  var textEnter = barsEnter.append('text').attr('dy', '1.3em').attr('dx',
      '0.5em');

  textEnter.append('tspan').text(function(d) {
    return (d.name.length > 255 ? d.name.substring(0, 255) : d.name) + ' (' + util.alignMillis(d.totalMillis) + ' ms)';
  });

  bars.exit().each(function(d) {
    if (d.parent) {
      d.x = rootCoords.x;
      d.y = rootCoords.y;
    }
    d.deleted = true;
  });

  createTransition(bars, transitionTime).selectAll('path').attr(
      'transform',
      function(d) {
        return 'translate(' + (d.children ? 0 : -10) + ','
            + (BAR_HEIGHT / 2 - 5) + ') ' + 'rotate(' + (d.children ? 90 : 0)
            + ')';
      });

  createTransition(bars.exit(), transitionTime).each('end', function() {
    d3.select(this.remove());
  });

  function drawBars(selection, widthFunction) {
    return selection.attr('transform',
        'translate(' + rootCoords.x + ',' + rootCoords.y + ')').append('rect')
        .attr('rx', 3).attr('width', function(d) {
          return Math.max(widthFunction(d), MIN_BAR_WIDTH);
        }).attr('height', function() {
          return BAR_HEIGHT;
        }).each(function(d) {
          d3.select(this).classed(d.resultType.toLowerCase(), true);
        });
  }

  function mouseover(d) {
    if (d.children || d._children) {
      vis.selectAll('g.trace').filter(function(e) {
        return e !== d && !ancestors[e.id][d.id];
      }).each(function(e) {
        e.waterfallDimmed = true;
      }).style('opacity', alpha);
    }
  }

  function mouseout() {
    vis.selectAll('g.trace').filter(function(d) {
      return d.waterfallDimmed;
    }).each(function(d) {
      delete d.waterfallDimmed;
    }).style('opacity', alpha);
  }

  function toggleCollapse(d, i) {
    if (d3.event.shiftKey) {
      recursiveCollapse(d, d.children);
    } else {
      collapseOne(d, d.children);
    }
    redraw(svg, vis, data, ancestors, TRANSITION_TIME, {
      x : x(d.startMillis),
      y : y(i)
    });
  }
}

function createTransition(selection, transitionTime) {
  var transition = selection.style('pointer-events', 'none').transition()
      .duration(transitionTime).attr('transform', function(d) {
        return 'translate(' + d.x + ',' + d.y + ')';
      }).style('opacity', alpha);

  transition.each('end', function() {
    d3.select(this).style('opacity', alpha);
    d3.select(this).style('pointer-events', undefined);
  });

  transition.selectAll('line.horizontal').attr('x2', function(d) {
    var delta = 0;
    // Check for id is important since we have a fake node at the root
    if (d.parent.id !== null) {
      delta = d.parent.x - d.x;
    }
    return delta - 4;
  });

  transition.selectAll('line.vertical').attr('y2', function(d) {
    var delta = 0;
    if (d.children) {
      delta = Math.max.apply(Math, d.children.map(function(c) {
        return c.y;
      })) - d.y;
    }
    return delta + BAR_HEIGHT / 2;
  });

  return transition;
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

function alpha(d) {
  return d.deleted ? 1e-6 : (d.waterfallDimmed ? DIMMED_ALPHA : 1);
}

function redrawGrid(svg, x, transitionTime, height) {
  function addOrUpdate(type) {
    // D3 throws an error when trying to redraw grids with large >50, >100 tick
    // counts. For now we just delete the axis and redraw it.
    var selected = svg.select('g.x-axis.' + type);
    if (!selected.empty())
      selected.remove();

    return svg.insert('g', ':first-child').attr('class', 'x-axis ' + type)
        .attr('transform',
            'translate(' + MARGIN.left + ',' + (MARGIN.top - 15) + ')').append(
            'g');
  }

  addOrUpdate('label').call(
      d3.svg.axis().scale(x).orient('top').innerTickSize(6).outerTickSize(3));

  addOrUpdate('grid').call(
      d3.svg.axis().scale(x).orient('top').innerTickSize(-height)
          .tickFormat('').ticks(10));

  addOrUpdate('minor-grid').call(
      d3.svg.axis().scale(x).orient('top').innerTickSize(-height)
          .tickFormat('').ticks(100));
}
