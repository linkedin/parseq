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
  var _toMillisFloat = function(time) {
    return time / 1000000;
  };

  /**
   * Renders the view and append it to a d3 selection.
   */
  function render(selection, root) {
    selection.classed("waterfallview", true);

    var w = 960,

        // We use a margin for the axes
        margin = { top: 40, bottom: 10, left: 10, right: 250 },
        barHeight = 25,
        barSpacing = 10,
        minBarWidth = 3,

        // transition duration
        defaultDuration = 300,

        alphaDimmed = 0.3,

        taskFill = { "default":       "#DDD",
                     "success":       "#E0FFE0",
                     "error":         "#FFE0E0",
                     "early_finish":  "#FFC" };

    // Mapping of descendant to a set (expressed as an object) of a ancestors
    var ancestors = {};
    function ancestorPreorder(x) {
      var xAncestors = ancestors[x.id] = {};
      if (x.parent) {
        xAncestors[x.parent.id] = true;
        for (var ancestorId in ancestors[x.parent.id]) {
          xAncestors[ancestorId] = true;
        }
      }
      if (x.children) {
        for (var i = 0; i < x.children.length; ++i) {
          ancestorPreorder(x.children[i]);
        }
      }
    }
    ancestorPreorder(root)

    var svg = selection.append("svg")
      .attr("width", w + margin.left + margin.right);

    var vis = svg.append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top +")")
      .append("g");

    var xLabel = svg.append("g")
      .attr("class", "x axis label")
      .attr("transform", "translate(" + margin.left + "," + (margin.top - 15) + ")")
      .append("g");

    var xGrid = svg.append("g")
      .attr("class", "x axis grid")
      .attr("transform", "translate(" + margin.left + "," + (margin.top - 15) + ")")
      .append("g");

    update(root, 0);

    function update(source, duration) {
      var traces = TRACE.flatten(root);

      var x = d3.scale.linear()
        .domain([0, d3.max(traces, function(d) { return d.start + d.elapsed; })])
        .range([0, w]);

      // Resize the canvas
      var h = traces.length * (barHeight + barSpacing) + margin.top + margin.bottom;
      if (svg.attr("height") - h < 0) {
        svg.attr("height", h);
      }

      traces.forEach(function(d, i) {
        d.x = x(_toMillisFloat(d.startNanos));
        d.w = x(_toMillisFloat(d.startNanos + d.elapsedNanos)) - d.x;

        if (d.w < minBarWidth) {
          d.x = Math.max(d.x - minBarWidth, d.parent ? d.parent.x : 0);
          d.w = minBarWidth;
        }

        d.y = i * (barHeight + barSpacing);
        d.h = barHeight;
      });

      xGrid.transition()
        .duration(duration)
        .call(d3.svg.axis()
        .scale(x)
        .orient("top")
        .tickSubdivide(10)
        .tickSize(-h,-h,0)
        .tickFormat(""));

      xLabel.call(d3.svg.axis()
        .scale(x)
        .orient("top")
        .tickSubdivide(10)
        .tickSize(6, 3, 0));

      // Update the traces
      var trace = vis.selectAll("g.trace")
        .data(traces, function(d) { return d.id });

      var traceEnter = trace.enter().append("g")
        .classed("trace", true)
        .classed("composite", function(d) { return d.children || d._children; })
        .classed("hidden", false)
        .attr("transform", function(d) { return "translate(" + source.x0 + "," + source.y0 + ")"; })
        .style("opacity", 1e-6)
        .on("click", toggleCollapse);

      trace.classed("collapsed", function(d) { return d._children; });

      // Enter any new traces at the parent's previous position.
      traceEnter.append("rect")
        .attr("y", -barHeight / 2)
        .attr("rx", 3)
        .attr("height", function(d) { return d.h; })
        .attr("width", function(d) { return d.w; })
        .style("fill", function(d) { return taskFill[d.resultType] || taskFill["default"]; })
        .on("mouseover", mouseover)
        .on("mouseout", mouseout);

      traceEnter.append("text")
        .attr("dy", 4)
        .attr("dx", 6);

      trace.select("text")
        .text(function(d) {
            var name = d.name + " (" + d.elapsed + " ms)";
            if (d.children) {
              name = "[-] " + name;
            } else if (d._children) {
              name = "[+] " + name;
            }
            return name;
          });

      // Transition traces to their new position.
      traceEnter.transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
        .style("opacity", function(d) { return d.waterfallDimmed ? alphaDimmed : 1; });

      trace.transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
        .style("opacity", function(d) { return d.waterfallDimmed ? alphaDimmed : 1; });

      // Transition exiting traces to the parent's new position.
      trace.exit()
        .classed("hidden", true)
          .transition()
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

    function toggleCollapse(d) {
      if (d.children) {
        d._children = d.children;
        delete d.children;
      } else {
        d.children = d._children;
        delete d._children;
      }
      update(d, defaultDuration);
    }

    function mouseover(d) {
      if (d.children) {
        vis.selectAll("g.trace")
          .filter(function(e) { return e !== d && !ancestors[e.id][d.id]; })
          .each(function(d) { d.waterfallDimmed = true; });
        update(d, 0);
      }
    }

    function mouseout(d) {
      vis.selectAll("g.trace").each(function(d) { delete d.waterfallDimmed; });
      update(d, 0);
    }
  };

  return {
    render: render
  };
})();
