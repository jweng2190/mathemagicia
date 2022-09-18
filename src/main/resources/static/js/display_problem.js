const baseUrl = 'http://localhost:8080';
if(localStorage.getItem('currentIndex') == null) {
  var currentIndex = 0;
} else {
  currentIndex = localStorage.getItem('currentIndex');
}

const params = new Proxy(new URLSearchParams(window.location.search), {
  get: (searchParams, prop) => searchParams.get(prop),
});
let contest = params.contest;
let problemId = params.problemId;

//render the problem display template
let tmpl = document.getElementById('tmpl')
let elem = document.createElement('div');
elem.id = 'content';

elem.append(tmpl.content.cloneNode(true));
document.body.append(elem);

$.getJSON(baseUrl + '/problems?contest=' + contest, function(data) {     
  var object = data[currentIndex];

  var form = document.getElementById('answer');
  var div1 = document.createElement('div');
  div1.id = 'p0';
  var div2 = document.createElement('div');
  div2.id = 'p1';

  elem.insertBefore(div1, form);
  elem.insertBefore(div2, form);

  div1.innerHTML = object["problemDescription"];

  let binaryString = object["image"];
  if(binaryString != null) {
    var image = new Image();
    image.src = 'data:image/jpeg;base64,' + binaryString;
    div2.append(image); 
  }

  document.getElementById('next').addEventListener("click", function() {
    if(currentIndex + 1 < data.length) {
      currentIndex++;
      object = data[currentIndex];
      localStorage.setItem('currentIndex', currentIndex);
      problemId = object['problemId'];
      window.location.replace(baseUrl + '/view?contest=' + contest + "&problemId=" + problemId);
    } else {
      currentIndex = 0;
      object = data[currentIndex];
      localStorage.setItem('currentIndex', currentIndex);
      problemId = object['problemId'];
      window.location.replace(baseUrl + '/view?contest=' + contest + "&problemId=" + problemId);
    }
  });
});
