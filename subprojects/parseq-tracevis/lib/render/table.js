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

// How far should we indent for each subgraph in ems
var DEPTH_PADDING_EM = 1;

function render(root, graph) {
  root.classed('tableview', true);
  var table = createTable(root);
  redraw(table, d3Treeify(graph));
}

/*
 * Creates a table element and adds the standard header. Returns the table
 * element.
 */
function createTable(root) {
  var table = root.append('table').attr('class', 'table table-bordered'), thead = table
      .append('thead');

  thead.append('th').text('Name');
  thead.append('th').html('Start (ms)');
  thead.append('th').html('Run (ms)');
  thead.append('th').html('Total (ms)');
  thead.append('th').text('Result');
  thead.append('th').text('Value');

  return table;
}

function redraw(table, data) {
  var rows, // All rows as a d3 selection
  rowsEnter, // All new rows as a d3 selection
  col; // All columns as a d3 selection

  function toggleExpand(d) {
    if (d.children) {
      d._children = d.children;
      delete d.children;
    } else if (d._children) {
      d.children = d._children;
      delete d._children;
    }
    redraw(table, data);
  }

  var nodes = d3.layout.hierarchy()
  // Tell D3 not to overwrite our 'value' property
  .value(undefined).sort(function(a, b) {
    return a.order - b.order;
  })(data);

  nodes = nodes.filter(function(d) {
    return d.id !== null;
  });

  rows = table.selectAll('tr').classed('hidden', false).data(nodes,
      function(d) {
        return d.id;
      });

  rowsEnter = rows.enter().append('tr').attr('id', function(d) {
    return 'trace-' + d.id;
  });
  rows.exit().classed('hidden', true);

  col = rowsEnter.append('td').style('padding-left', function(d) {
    return (d.depth * DEPTH_PADDING_EM + 0.5) + 'em';
  });

  col.append('span').on('click', toggleExpand).classed('expandable', true)
      .text('[-] ');

  col.append('span').classed('composite', function(d) {
    return d.children && d.children.length;
  }).text(function(d) {
    return d.name;
  });

  rowsEnter.append('td').classed('numeric', true).html(function(d) {
    return '<br/>' + util.alignMillis(d.startMillis);
  });
  rowsEnter.append('td').classed('numeric', true).html(
      function(d) {
        if ('runMillis' in d) {
          return '+' + util.alignMillis(d.runMillis) + '<br/>'
              + util.alignMillis(d.startMillis + d.runMillis);
        }
        return '?';
      });
  rowsEnter.append('td').classed('numeric', true).html(
      function(d) {
        return '+' + util.alignMillis(d.totalMillis) + '<br/>'
            + util.alignMillis(d.startMillis + d.totalMillis);
      });
  rowsEnter.append('td').attr('class', function(d) {
    return d.resultType.toLowerCase();
  }).text(function(d) {
    return d.resultType;
  });
  rowsEnter.append('td').classed('value', true).append('textarea').attr('class', 'text-monospace form-control').attr('rows',
      2).text(function(d) {
    return d.value;
  });

  rows.selectAll('.expandable').text(function(d) {
    if (d.children && d.children.length) {
      return '[-] ';
    } else if (d._children) {
      return '[+] ';
    } else {
      return '';
    }
  });

  rows.filter(':not(.hidden)').classed('oddrow', function(_, i) {
    return i % 2;
  });
}
