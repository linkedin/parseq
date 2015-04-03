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

var progressColorsMap = {
  '#e0ffe0': '#60D060',
  '#ffe0e0': '#FF6F6F',
  '#fffacd': '#FFFD76',
  '#cccccc': '#aaaaaa'
}

var dot = require('graphlib-dot'),
    dotify = require('../trace/dotify'),
    sha1 = require('sha1');

module.exports = render;

function render(root, graph) {
  root.classed('graphvizview', true);

  var minStartMs = Math.min.apply(Math,
                                graph.nodes().map(function(u) {
                                  return graph.node(u).startNanos;
                                })) / (1000 * 1000);
                                
  var maxEndMs = Math.max.apply(Math,
                                graph.nodes().map(function(u) {
                                  return graph.node(u).endNanos;
                                })) / (1000 * 1000);

  var timings = [];
  
  graph.nodes().map(function(u) {
    var node = graph.node(u);
    timings[u] = {
      start: (graph.node(u).startNanos  / (1000 * 1000)) - minStartMs,
      end: (graph.node(u).endNanos / (1000 * 1000)) - minStartMs
    };
  });

  var graph = dotify(graph);
  var dotFormat = dot.write(graph);
  var hash = sha1(dotFormat);
  
  var xhr = new XMLHttpRequest();
  root.selectAll("*").remove();
  
  xhr.onreadystatechange = function() {
  if (xhr.readyState == 4) {
    if (xhr.status == 200) {
      root.append('img')
        .attr('id', 'graphviz-img')
        .attr('class', 'inject-me')
        .attr('data-src', 'cache/' + hash + '.svg');
        
      var mySVGsToInject = document.querySelectorAll('img.inject-me');
      
      var sliderUpdate = function(value) {
            d3.selectAll('.pb-rect').each(function(d, i) {
              var _this = d3.select(this);
              var id = this.getAttribute('id').substring(8);
              var timing = timings[id];
              var scale = (value - timing.start) / (timing.end - timing.start);
              if (scale < 0) {
                scale = 0;
              }
              if (scale > 1) {
                scale = 1;
              }
              var startX = _this.node().getAttribute('x');
              _this.attr('transform', 'translate(' + (-1 * startX * (scale -1)) + ') scale(' + scale + ', 1)');
            });
            d3.select('#slider-text').text(value.toFixed(2) + 'ms');
      }
      
      SVGInjector(mySVGsToInject, {
        evalScripts: 'once',
        each: function (svg) {

            var svgWidth = parseInt(svg.getAttribute('width'), 10);
            var svgHeight = parseInt(svg.getAttribute('height'), 10);
            var divWidth = document.getElementById('resultView').clientWidth;
            var divHeight = window.innerHeight - document.getElementById('app-container').clientHeight - 20;
            svg.setAttribute('style', 'width: ' + divWidth + ' ; height: ' + divHeight);
            svg.setAttribute('width', divWidth);
            svg.setAttribute('height', divHeight);
            var svgFit = true; 
            var svgScale = Math.max(svgWidth / divWidth, svgHeight / divHeight);
            if (svgWidth < divWidth && svgHeight < divHeight) {
              var graph = document.getElementsByClassName('graph')[0];
              var svgTransform = graph.getAttribute('transform');
              graph.setAttribute('transform', 'scale(' + svgScale + ' ' + svgScale + ') ' + svgTransform);
              svgFit = false;
            }
            var beforePan = function(oldPan, newPan) {
            var stopHorizontal = false,
              stopVertical = false,
              gutterWidth = 100,
              gutterHeight = 100,
               // Computed variables
              sizes = this.getSizes(),
              leftLimit = -((sizes.viewBox.x + sizes.viewBox.width) * sizes.realZoom) + gutterWidth,
              rightLimit = sizes.width - gutterWidth - (sizes.viewBox.x * sizes.realZoom),
              topLimit = -((sizes.viewBox.y + sizes.viewBox.height) * sizes.realZoom) + gutterHeight,
              bottomLimit = sizes.height - gutterHeight - (sizes.viewBox.y * sizes.realZoom)

              customPan = {}
              customPan.x = Math.max(leftLimit, Math.min(rightLimit, newPan.x))
              customPan.y = Math.max(topLimit, Math.min(bottomLimit, newPan.y))
              return customPan
            }

            var panZoom = window.panZoom = svgPanZoom(svg, {
              zoomEnabled: true,
              controlIconsEnabled: true,
              fit: svgFit,
              maxZoom: (svgScale > 1) ? 10*svgScale : 10,
              center: true,
              beforePan: beforePan
            });
            
            window.onresize = function() {
              document.getElementById('graphviz-img')
                .setAttribute('style', 'width: ' + document.getElementById('resultView').clientWidth +
                ' ; height: ' + (window.innerHeight - document.getElementById('app-container').clientHeight - 20));
              document.getElementById('graphviz-img')
                .setAttribute('width', document.getElementById('resultView').clientWidth);
              document.getElementById('graphviz-img')
                .setAttribute('height', (window.innerHeight - document.getElementById('app-container').clientHeight - 20));
              panZoom.resize();
              panZoom.fit();
              panZoom.center();
            };
            
        }
      }, function() {

      root.append('div')
        .attr('id', 'graphviz-footer')
        .attr('class', 'navbar navbar-fixed-bottom row-fluid')
        .append('div')
        .attr('id', 'graphviz-footer-left')
        .attr('class', 'span3')
        .append('small')
        .attr('style', 'margin: 0px; padding: 0px;')
        .append('a')
        .attr('style', 'padding-left: 10px;')
        .attr("href", "https://github.com/linkedin/parseq/wiki/Tracing#graphviz-view")
        .text('Understanding this diagram');

      var svg = root.select('#graphviz-img').node();
            
      var defs = d3.select('.graph').append('defs');
            
      root.selectAll('.node').each(function(d, i) {
        var _this = d3.select(this);
        var title = _this.select('title').text();
        if (!title.startsWith('source_')) {
          if (title.startsWith('sink_')) {
            //sinks of clusters
            var id = title.substring(5);
            var ellipse = _this.select('ellipse');
            var bbox = ellipse.node().getBBox();
            var rect = defs.append('clipPath')
              .attr('id', 'pb-' + id)
              .append('rect')
              .attr('class', 'pb-rect')
              .attr('id', 'pb-rect-' + id)
              .attr("x", bbox.x)
              .attr("y", bbox.y)
              .attr("width", (d3.rgb(ellipse.style('fill')).toString() == '#cccccc') ? bbox.width / 2 : bbox.width)
              .attr("height", bbox.height);
            var cloned = ellipse.node().cloneNode();
            this.insertBefore(cloned, ellipse.node().nextSibling);
            d3.select(cloned)
              .style("fill", progressColorsMap[d3.rgb(ellipse.style('fill')).toString()])
              .attr('clip-path', 'url(#pb-' + id + ')');
          } else {
            //normal nodes
            var id = title;
            var path = _this.select('path');
            var bbox = path.node().getBBox();
            var rect = defs.append('clipPath')
              .attr('id', 'pb-' + id)
              .append('rect')
              .attr('class', 'pb-rect')
              .attr('id', 'pb-rect-' + id)
              .attr("x", bbox.x)
              .attr("y", bbox.y)
              .attr("width", (d3.rgb(path.style('fill')).toString() == '#cccccc') ? bbox.width / 2 : bbox.width)
              .attr("height", bbox.height);
            var cloned = path.node().cloneNode();
            this.insertBefore(cloned, path.node().nextSibling);
            d3.select(cloned)
              .style("fill", progressColorsMap[d3.rgb(path.style('fill')).toString()])
              .attr('clip-path', 'url(#pb-' + id + ')');
          }
        }
        
        sliderUpdate(0);
      });
        
      var slider = d3.slider().min(0).max(maxEndMs - minStartMs).axis(d3.svg.axis().orient("top").ticks(10))
        .on("slide", function(evt, value) {
           sliderUpdate(value);
         })

      root.select('#graphviz-footer')
        .append('div')
        .attr('id', 'graphviz-footer-right')
        .attr('class', 'span6')
        .attr('style', 'padding-bottom: 20px;')
        .append('div')
        .attr('id', 'graphviz-slider')
        .call(slider);
        
      root.select('#graphviz-footer')
        .append('div')
        .attr('id', 'graphviz-footer-slider-text')
        .attr('class', 'span1')
        .append('h4')
        .append('span')
        .attr('id', 'slider-text')
        .text('0.00ms');
      
      });
        
    } else {
      var textarea = root.append('textarea')
        .style('width', '100%')
        .style('height', '600px');
      textarea.text(dot.write(graph));
      alert('Contacting TracevisServer failed, status: ' + xhr.status + '\n' + xhr.responseText);
    }
  }
  }
  xhr.open("POST", "dot?hash=" + hash , true);
  xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
  xhr.send(dotFormat);
}
