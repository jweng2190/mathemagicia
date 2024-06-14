const problemTemplate = document.getElementById('table-component');
const solutionTemplate = document.getElementById('solution_templ');
const breakTempl = document.getElementById('break');

let gameUrl = window.location.href;
let gameIdIndex = gameUrl.lastIndexOf("/") + 1;
var gameId = gameUrl.substring(gameIdIndex);

let contentDiv = document.getElementById("problems_solutions");

async function getProblems() {
    const response = await fetch("/game/full_problems/" + gameId);

    if (!response.ok) {
        const message = `An error has occured: ${response.status}`;
        throw new Error(message);
    }

    const problems = await response.json();
    return problems;
}

getProblems().then(data => {
    console.log(data);
    showProblems(data);
})
.catch(error => {
    error.message;
});

function showProblems(problems) {
    for(let i = 0; i < problems.length; i++) {
        let problem = problems[i];
        let problemUrl = problem.image;
        let solutionUrl = problem.solution;
    
        const cloneProblem = document.importNode(problemTemplate.content, true);
        const cloneSolution = document.importNode(solutionTemplate.content, true);
        const cloneBreak = document.importNode(breakTempl.content, true);
    
        cloneProblem.querySelector('img').src = problemUrl;
        cloneProblem.querySelector('th').textContent = "Problem " + (i + 1);
        cloneSolution.querySelector('a').href = solutionUrl;
        cloneSolution.getElementById('creds').innerHTML = (problem.description + " - Used with permission of the MAA");
    
        contentDiv.appendChild(cloneProblem);
        contentDiv.appendChild(cloneSolution);
        if(i !== problems.length - 1) {
            contentDiv.appendChild(cloneBreak);
        }
    }
}
