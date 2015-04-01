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

var dot = require('graphlib-dot'),
    dotify = require('../trace/dotify'),
    sha1 = require('sha1');

module.exports = render;

function render(root, graph) {
  root.classed('graphvizview', true);

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
      var injectorOptions = {
         evalScripts: 'once',
         each: function (svg) {
          if (svg) {
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
        }
      };
      SVGInjector(mySVGsToInject, injectorOptions);
      root.append('div')
        .attr('class', 'navbar navbar-fixed-bottom')
        .append('small')
        .attr('style', 'margin: 0px; padding: 0px;')
        .append('a')
        .attr("href", "https://github.com/linkedin/parseq/wiki/Tracing#graphviz-view")
        .text('Understanding this diagram');
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
