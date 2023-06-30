//var currentIndex = localStorage.getItem('mcIndex');
var currentIndex = 0;

const form1 = document.getElementById("form1");
const form2 = document.getElementById("form2");
const log1 = document.getElementById("log1");
const log2 = document.getElementById("log2");

function validateAnswer() {
    let answer1 = form1.elements["answer1"].value;
    let answer2 = form2.elements["answer2"].value;
    let submitElement = document.activeElement;
    $.getJSON("/problems?contest=mathcounts", function(data) {
        var object = data[currentIndex];
        var problemId = 1;
        $.get('/answer?problemId=' + problemId, function(answer) {
            if(submitElement.id == "button1") {
                if(answer1 == answer) {
                    log1.textContent = "Correct!\nSolution:\n" + object["solution"];
                } else {
                    log1.textContent = "Incorrect.\nSolution:\n" + object["solution"];
                }
                document.getElementById("answer1").value = "";
            }
            if(submitElement.id == "button2") {
                if(answer2 == answer) {
                    log2.textContent = "Correct!\nSolution:\n" + object["solution"];
                } else {
                    log2.textContent = "Incorrect.\nSolution:\n" + object["solution"];
                } 
                document.getElementById("answer2").value = ""; 
            }
        })
    }); 
    event.preventDefault();
}

form1.addEventListener("submit", validateAnswer);
form2.addEventListener("submit", validateAnswer);
