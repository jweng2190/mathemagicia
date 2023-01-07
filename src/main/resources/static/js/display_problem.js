if(localStorage.getItem('mcIndex') == null) {
  var mcIndex = 0;
} else {
  mcIndex = parseInt(localStorage.getItem('mcIndex'));
}

if(localStorage.getItem('amc8Index') == null) {
  var amc8Index = 0;
} else {
  amc8Index = parseInt(localStorage.getItem('amc8Index'));
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

$.getJSON('/problems?contest=' + contest, function(data) {    
  if(contest == 'mathcounts') {
    var object = data[mcIndex];
    var currentIndex = mcIndex;
  }

  if(contest == 'amc8') {
    var object = data[amc8Index];
    var currentIndex = amc8Index;
  }

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
      if(contest=='mathcounts') {
        mcIndex = currentIndex;
        localStorage.setItem('mcIndex', currentIndex);
      }
      if(contest=='amc8') {
        amc8Index = currentIndex;
        localStorage.setItem('amc8Index', currentIndex);
      }

      problemId = object['problemId'];

      if(contest=='mathcounts') {
        localStorage.setItem('mathcountsId', problemId);
      }
      if(contest=='amc8') {
        localStorage.setItem('amc8Id', problemId);
      }

      window.location.replace('/view?contest=' + contest + "&problemId=" + problemId);
    } else {
      currentIndex = 0;
      object = data[currentIndex];

      if(contest=='mathcounts') {
        mcIndex = currentIndex;
        localStorage.setItem('mcIndex', currentIndex);
      }
      if(contest=='amc8') {
        amc8Index = currentIndex;
        localStorage.setItem('amc8Index', currentIndex);
      }

      problemId = object['problemId'];
      window.location.replace('/view?contest=' + contest + "&problemId=" + problemId);
    }
  });
});
