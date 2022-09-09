const baseUrl = 'http://localhost:8080';

const params = new Proxy(new URLSearchParams(window.location.search), {
  get: (searchParams, prop) => searchParams.get(prop),
});
let contest = params.contest;

//render the problem display template
let tmpl = document.getElementById('tmpl')
let elem = document.createElement('div');
elem.id = 'content';

elem.append(tmpl.content.cloneNode(true));
document.body.append(elem);

$.getJSON(baseUrl + '/problems?contest=' + contest, function(data) {     

var object = data[1];

var form = document.getElementById('answer');
var div1 = document.createElement('div');
div1.id = 'p0';
var div2 = document.createElement('div');
div2.id = 'p1';

elem.insertBefore(div1, form);
elem.insertBefore(div2, form);

div1.innerHTML = object["problemDescription"];

var image = new Image();
let binaryString = object["image"];
image.src = 'data:image/jpeg;base64,' + binaryString;

div2.append(image); 
});
