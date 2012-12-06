/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

var WATERFALL = (function() {
  var _traceClass = function (d) {
    var cls = ["trace", d.resultType];
    if (d.children || d._children) {
      cls.push("composite");
      if (d._children) {
        cls.push("collapsed");
      }
    }
    return cls.join(' ');
  };

  var _toMillisFloat = function(time) {
    return time / 1000000;
  };

  /**
   * Renders the view and append it to a d3 selection.
   */
  var render = function(selection, root) {
    selection.classed("waterfallview", true);

    var w = 960,

        // We update height when we know how tall we need the chart
        h = 0,

        // We use a margin for the axes
        margin = { top: 40, bottom: 10, left: 10, right: 250 },
        barHeight = 25,
        barSpacing = 10,
        minBarWidth = 3,

        // transition duration
        duration = 0,

        // root of the tree
        root;

    var svg = selection.append("svg")
      .attr("width", w + margin.left + margin.right);

    var xLabel = svg.append("g")
      .attr("class", "x axis label")
      .attr("transform", "translate(" + margin.left + "," + (margin.top - 15) + ")");

    var xGrid = svg.append("g")
      .attr("class", "x axis grid")
      .attr("transform", "translate(" + margin.left + "," + (margin.top - 15) + ")");

    var vis = svg.append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top +")");

    function update(source) {
      var traces = TRACE.flatten(root);

      // Resize the canvas
      var newHeight = traces.length * (barHeight + barSpacing) + margin.top + margin.bottom;
      if (newHeight > h) {
        h = newHeight;
        svg.attr("height", h);
      }

      // Compute the "layout".
      var xScale = d3.scale.linear()
        .domain([0, d3.max(traces, function(d) { return d.start + d.elapsed; })])
        .range([0, w]);

      traces.forEach(function(d, i) {
        d.x = xScale(_toMillisFloat(d.startNanos));
        d.w = xScale(_toMillisFloat(d.startNanos + d.elapsedNanos)) - d.x;

        if (d.w < minBarWidth) {
          d.x = Math.max(d.x - minBarWidth, d.parent ? d.parent.x : 0);
          d.w = minBarWidth;
        }

        d.y = i * (barHeight + barSpacing);
        d.h = barHeight;
      });

      xGrid.call(d3.svg.axis()
        .scale(xScale)
        .orient("top")
        .tickSubdivide(10)
        .tickSize(-h,-h,0)
        .tickFormat(""));

      xLabel.call(d3.svg.axis()
        .scale(xScale)
        .orient("top")
        .tickSubdivide(10)
        .tickSize(6, 3, 0));

      // Update the traces
      var trace = vis.selectAll("g.trace")
        .data(traces, function(d) { return d.id })
        .attr("class", _traceClass);

      var traceEnter = trace.enter().append("g")
        .attr("class", _traceClass)
        .attr("transform", function(d) { return "translate(" + source.x0 + "," + source.y0 + ")"; })
        .style("opacity", 1e-6);

      // Enter any new traces at the parent's previous position.
      traceEnter.append("rect")
        .attr("y", -barHeight / 2)
        .attr("height", function(d) { return d.h; })
        .attr("width", function(d) { return d.w; });

      traceEnter.append("text")
        .attr("dy", 4)
        .attr("dx", 6)
        .text(function(d) { return d.name + " (" + d.elapsed + " ms)"; });

      // Transition traces to their new position.
      traceEnter.transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
        .style("opacity", 1);

      trace.transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
        .style("opacity", 1);

      // Transition exiting traces to the parent's new position.
      trace.exit().transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + source.x + "," + source.y + ")"; })
        .style("opacity", 1e-6)
        .remove();

      // Stash the old positions for transition.
      traces.forEach(function(d) {
        d.x0 = d.x;
        d.y0 = d.y;
      });

    }

    update(root);
  };

  return {
    render: render
  };
})();
