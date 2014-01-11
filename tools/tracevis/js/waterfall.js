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
        defaultDuration = 250,

        alphaDimmed = 0.3;

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
    ancestorPreorder(root);

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
      var traces = TRACE.identifyLowestChild(TRACE.flatten(root));

      var x = d3.scale.linear()
        .domain([0, d3.max(traces, function(d) { return d.startMillis + d.totalMillis; })])
        .range([0, w]);

      // Resize the canvas
      var h = traces.length * (barHeight + barSpacing) + margin.top + margin.bottom;
      if (svg.attr("height") - h < 0) {
        svg.attr("height", h);
      }

      traces.forEach(function(d, i) {
        d.x = x(d.startMillis);
        if ('runMillis' in d) {
          d.runWidth = Math.max(x(d.runMillis), minBarWidth);
        } else {
          d.runWidth = 0;
        }
        d.totalWidth = Math.max(x(d.totalMillis), minBarWidth);

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
        .data(traces, function(d) { return d.id; });

      var traceEnter = trace.enter().append("g")
        .classed("trace", true)
        .classed("composite", function(d) { return d.children || d._children; })
        .classed("hidden", false)
        .attr("transform", function(d) { return "translate(" + source.x0 + "," + source.y0 + ")"; })
        .style("opacity", 1e-6)
        .on("mouseover", mouseover)
        .on("mouseout", mouseout)
        .on("click", toggleCollapse);

      trace.classed("collapsed", function(d) { return d._children; });
      
      var verticalEnter = traceEnter.filter( function(d) { return d.children || d._children;; } )
      verticalEnter.append("line")
      .classed("vertical", true)
      .attr("x1", -3)
      .attr("y1", 4)
      .attr("x2", -3)
      .attr("y2", function (d) {
      	  if (d.lowestChild)
      	    return (d.lowestChild  - d.level) * (barHeight + barSpacing);
      	  else
      	    4;
      	} )
      .style("stroke", "black")
      .style("stroke-width", 1)
      .style("stroke-linecap", "round")
      .style("stroke-dasharray", 3);

      var vertical = trace.select("line.vertical");

      vertical.attr("y2", function (d) {
      	  if (d.lowestChild)
      	    return (d.lowestChild  - d.level) * (barHeight + barSpacing)
      	  else
      	    this.y2;
      	} );

      var noRootEnter = traceEnter.filter( function (d, i) { return i != 0 })
      noRootEnter.append("line")
      .attr("x1", -4)
      .attr("y1", 0)
      .attr("x2", function (d) {
    	  return -4 - d.x + d.parent.x;
	  	} )
      .attr("y2", 0)
      .style("stroke", "black")
      .style("stroke-width", 1)
      .style("stroke-linecap", "round")
      .style("stroke-dasharray", 3);
      
      traceEnter.append("circle")
      .attr("r", 4)
      .attr("cx", -3)
      .style("stroke", "black")
      .style("stroke-width", 1);

      // Enter any new traces at the parent's previous position.
      traceEnter.append("rect")
        .attr("y", -barHeight / 2)
        .attr("rx", 3)
        .attr("height", function(d) { return d.h; })
        .attr("width", function(d) { return d.runWidth; })
        .each(function(d) { d3.select(this).classed(d.resultType, true); })
        .style("stroke-opacity", 0);

      traceEnter.append("rect")
        .attr("y", -barHeight / 2)
        .attr("rx", 3)
        .attr("height", function(d) { return d.h; })
        .attr("width", function(d) { return d.totalWidth; })
        .each(function(d) { d3.select(this).classed(d.resultType, true); })
        .style("fill-opacity", 0.3);


      var textEnter = traceEnter.append("text")
        .attr("dy", 4)
        .attr("dx", 6);

      textEnter.append("tspan").attr("class", "expando");
      textEnter.append("tspan").text(function(d) { return d.name + " (" + d.totalMillis + " ms)"; });

      var text = trace.select("text tspan.expando")
          .style("font-family", "monospace")
          .text(function(d) {
              if (d.children) {
                return "[-] ";
              } else if (d._children) {
                return "[+] ";
              }
            });

      trace.transition()
        .duration(duration)
        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
          .style("opacity", function(d) { return d.waterfallDimmed ? alphaDimmed : 1; })
          .each("end", function() { d3.select(this).style("opacity", function(d) { return d.waterfallDimmed ? alphaDimmed : 1; }); });

      // Transition traces to their new position.
      traceEnter
        .style("pointer-events", "none")
        .transition()
          .duration(duration)
          .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
          .style("opacity", function(d) { return d.waterfallDimmed ? alphaDimmed : 1; })
          .each("end", function() { d3.select(this).style("pointer-events", undefined); });

      // Transition exiting traces to the parent's new position.
      trace.exit()
        .style("pointer-events", "none")
        .classed("hidden", true)
        .transition()
          .duration(duration)
          .attr("transform", function(d) { return "translate(" + source.x + "," + source.y + ")"; })
          .style("opacity", 1e-6)
          .each("end", function() { d3.select(this).style("pointer-events", undefined); })
          .remove();

      // Stash the old positions for transition.
      traces.forEach(function(d) {
        d.x0 = d.x;
        d.y0 = d.y;
      });
    }

    function toggleCollapse(d) {
      if (d3.event.shiftKey) {
        recursiveCollapse(d, d.children);
      } else {
        collapseOne(d, d.children);
      }
      update(d, defaultDuration);
    }

    function recursiveCollapse(trace, collapse) {
      collapseOne(trace, collapse);
      (trace.children || trace._children || []).forEach(function(child) {
        recursiveCollapse(child, collapse);
      });
    }

    function collapseOne(trace, collapse) {
      if (collapse) {
        if (trace.children) {
          trace._children = trace.children;
          delete trace.children;
        }
      } else {
        if (trace._children) {
          trace.children = trace._children;
          delete trace._children;
        }
      }
    }

    function mouseover(d) {
      if (d.children || d._children) {
        vis.selectAll("g.trace")
          .filter(function(e) { return e !== d && !ancestors[e.id][d.id]; })
          .each(function(e) { e.waterfallDimmed = true; })
          .style("opacity", alphaDimmed);
      }
    }

    function mouseout(d) {
      vis.selectAll("g.trace")
        .filter(function(e) { return e.waterfallDimmed; })
        .style("opacity", function() { return d3.select(this).classed("hidden") ? 0 : 1; })
        .each(function(e) { delete e.waterfallDimmed; });
    }
  }

  return {
    render: render
  };
})();
