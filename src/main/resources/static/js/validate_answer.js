
function submitAnswer(event) {
    var inputAnswer = document.getElementById('problem_answer').value;
    const params = new Proxy(new URLSearchParams(window.location.search), {
        get: (searchParams, prop) => searchParams.get(prop),
    });
    let contest = params.contest;

    $.getJSON(baseUrl + "/problems?contest=" + contest, function(data) {
    var object = data[1];
    var problemId = object["problemId"];
        $.get(baseUrl + '/answer?problemId=' + problemId, function(answer) {
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
