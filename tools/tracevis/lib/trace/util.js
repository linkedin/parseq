exports.alignMillis = function(millis) {
  var millisStr = String(millis);
  var indexOfPoint = millisStr.indexOf('.');
  if (indexOfPoint === -1) {
    return millisStr + '.000';
  } else {
    var delta = millisStr.length - indexOfPoint - 1;
    if (delta < 3) {
      millisStr += new Array(4 - delta).join('0');
    } else if (delta > 3) {
      millisStr = millisStr.substring(0, indexOfPoint + 4);
    }
  }
  return millisStr;
};
