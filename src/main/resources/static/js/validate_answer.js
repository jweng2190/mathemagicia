const { data } = require("jquery");

function submitAnswer(event) {
    var inputAnswer = document.getElementById('problem_answer').value;


    /*if (inputAnswer == '8') {
        log.textContent = `Correct answer. Form Submitted! Time stamp: ${event.timeStamp}`;
    } else {
        log.textContent = `Incorrect answer. Form Submitted! Time stamp: ${event.timeStamp}`;
    }*/
    event.preventDefault();
}

const form = document.getElementById('answer');
const log = document.getElementById('log');
form.addEventListener('submit', submitAnswer);

$.getJSON('http://localhost:8080/mathcounts', function(data) {
    var object = data[0];
    var problemId = object["problemId"];
    $get('http://localhost:8080/answer?problemId=' + problemId, function(answer) {
        if(inputAnswer == answer) {
            log.textContent = `Correct answer. Form Submitted! Time stamp: ${event.timeStamp}`;    
        } else {
            log.textContent = `Incorrect answer. Form Submitted! Time stamp: ${event.timeStamp}`;
        }
    }) 
}


