const template = document.getElementById('table-component');
const breakTempl = document.getElementById('break');

let gameUrl = window.location.href;
let gameIdIndex = gameUrl.lastIndexOf("/") + 1;
var gameId = gameUrl.substring(gameIdIndex);

let contentDiv = document.getElementById("problems_solutions");

try {
    const response = await fetch("/game/full_problems/" + gameId);
    const problems = await response.json();
    if(response.ok) {
        console.log(problems);
        showProblems(problems);
    }
} catch(e) {
    throw new Error(e);
}

function showProblems(problems) {
    for(let i = 0; i < problems.length; i++) {
        let problem = problems[i];
        let problemUrl = problem.image;
        let solutionUrl = problem.solution;
    
        const cloneProblem = document.importNode(template.content, true);
        const cloneSolution = document.importNode(template.content, true);
        const cloneBreak = document.importNode(breakTempl.content, true);
    
        cloneProblem.querySelector('img').src = problemUrl;
        cloneProblem.querySelector('th').textContent = "Problem " + (i + 1);
        cloneSolution.querySelector('img').src = solutionUrl;
        cloneSolution.querySelector('th').textContent = "Solution";
    
        contentDiv.appendChild(cloneProblem);
        contentDiv.appendChild(cloneSolution);
        if(i !== problems.length - 1) {
            contentDiv.appendChild(cloneBreak);
        }
    }
}
