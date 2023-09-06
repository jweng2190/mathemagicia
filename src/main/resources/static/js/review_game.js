import { postRequest } from "./post_request.js";

const template = document.getElementById('table-component');

let gameUrl = window.location.href;
let gameIdIndex = gameUrl.lastIndexOf("/") + 1;
var gameId = gameUrl.substring(gameIdIndex);

let contentDiv = document.getElementById("problems_solutions");
let problems;

try {
    problems = await postRequest("/game/problem_list", gameId, "plain text", "json");
    console.log(problems);
} catch(e) {
    throw new Error(e);
}

for(let i = 0; i < problems.length; i++) {
    let problem = problems[i];
    let problemUrl = problem.image;
    let solutionUrl = problem.solution;

    const cloneProblem = document.importNode(template.content, true);
    const cloneSolution = document.importNode(template.content, true);

    cloneProblem.querySelector('img').src = problemUrl;
    cloneProblem.querySelector('th').textContent = "Problem";
    cloneSolution.querySelector('img').src = solutionUrl;
    cloneSolution.querySelector('th').textContent = "Solution";

    contentDiv.appendChild(cloneProblem);
    contentDiv.appendChild(cloneSolution);
}