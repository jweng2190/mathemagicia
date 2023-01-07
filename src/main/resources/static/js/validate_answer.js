if(localStorage.getItem('mcIndex') == null) {
    localStorage.setItem('mcIndex', 0);
}

if(localStorage.getItem('amc8Index') == null) {
    localStorage.setItem('amc8Index', 0);
}

function submitAnswer(event) {
    var inputAnswer = document.getElementById('problem_answer').value;
    const params = new Proxy(new URLSearchParams(window.location.search), {
        get: (searchParams, prop) => searchParams.get(prop),
    });
    let contest = params.contest;
    let problemId = params.problemId;

    $.getJSON("/problems?contest=" + contest, function(data) {
    if(contest == 'mathcounts') {
        var currentIndex = localStorage.getItem('mcIndex');
    }
    
    if(contest == 'amc8') {
        var currentIndex = localStorage.getItem('amc8Index');
    }

    var object = data[currentIndex];
        $.get('/answer?problemId=' + problemId, function(answer) {
            if(inputAnswer == answer) {
                log.textContent = "Correct!\nSolution:\n" + object["solution"];    
            } else {
                log.textContent = "Incorrect.\nSolution:\n" + object["solution"]; 
            }
        }) 
    });

    event.preventDefault();
}

const form = document.getElementById('answer');
const log = document.getElementById('log');
form.addEventListener('submit', submitAnswer);
