window.onload = function() {
  $.getJSON('http://localhost:8080/mathcounts', function(data) {     

  var object = data[0];
  var object_length = Object.keys(object).length;
  for(var j = 0; j < object_length; j++) {
    $('#content').append($('<div/>', { id: 'p' + j}))
  }

  var divs = document.getElementById("content").children;

  var index = 0;
  for(var property in object) {
    if(property != null) {
      divs[index].innerHTML = object[property];
    }
    index++;
  }
    
  });
}