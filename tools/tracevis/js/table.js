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

var TABLE = (function() {
  var _depthPaddingEm = 1;

  /**
   * Given a d3 selection, build the basic table structure as a child and
   * return the table element.
   */
  var _buildTable = function(selection) {
    var table = selection.append("table").attr("class", "table table-bordered");
    var thead = table.append("thead");
    thead.append("th").text("Name");
    thead.append("th").html("Start (ms)");
    thead.append("th").html("Run (ms)");
    thead.append("th").html("Total (ms)");
    thead.append("th").text("Result");
    thead.append("th").text("Value");
    return table;
  };

  /**
   * Renders the view and append it to a d3 selection.
   */
  var render = function(selection, root) {
    selection.classed("tableview", true);
    var table = _buildTable(selection);

    function update(source) {
      var data = TRACE.flatten(root);

      var rows = table.selectAll("tr")
        .classed("hidden", false)
        .data(data, function(d) { return d.id; });

      var rowsEnter = rows.enter().append("tr");

      rows.exit().classed("hidden", true);

      var nameCol = rowsEnter.append("td")
        .style("padding-left", function(d) { return (d.depth * _depthPaddingEm + 0.5) + "em"; });

      nameCol.append("span")
          .on("click", toggleExpand)
          .classed("expandable", true)
          .text("[-] ");

      nameCol.append("span")
          .classed("composite", function(d) { return d.children; })
          .text(function(d) { return d.name; });

      rowsEnter.append("td")
        .classed("numeric", true)
        .html(function(d) { return "<br/>" + TRACE.alignMillis(d.startMillis); });

      rowsEnter.append("td")
        .classed("numeric", true)
        .html(function(d) {
          if ('runMillis' in d) {
            return "+" + TRACE.alignMillis(d.runMillis) + "<br/>" +
                   TRACE.alignMillis(d.startMillis + d.runMillis);
          }
          return '?';
        });

      rowsEnter.append("td")
        .classed("numeric", true)
        .html(function(d) {
          return "+" + TRACE.alignMillis(d.totalMillis) + "<br/>" +
                 TRACE.alignMillis(d.startMillis + d.totalMillis);
        });

      rowsEnter.append("td")
        .attr("class", function(d) { return d.resultType; })
        .text(function(d) { return d.resultType; });

      rowsEnter.append("td")
        .classed("value", true)
        .append("textarea")
          .attr("rows", 2)
          .text(function(d) { return d.value; });

      rows.selectAll(".expandable")
          .text(function(d) {
            if (d.children) {
              return "[-] ";
            } else if (d._children) {
              return "[+] ";
            } else {
              return "";
            }
          });

    }

    update(root);

    function toggleExpand(d) {
      if (d.children) {
        d._children = d.children;
        d.children = null;
      } else if (d._children) {
        d.children = d._children;
        d._children = null;
      }
      update(d);
    }
  };

  return {
    render: render
  };
})();
