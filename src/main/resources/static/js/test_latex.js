var inputMode = 'regular';

var buttonRegular = document.getElementById('regular');
var buttonScientific = document.getElementById('scientific');
//var buttonLatex = document.getElementById("convert_latex");

//buttonLatex.addEventListener("click", convertToLatex);

function regularMode() {
    inputMode = 'regular';
    console.log(inputMode);
}

function scientificMode() {
    inputMode = 'scientific';
    console.log(inputMode);
}

function convertToLatex() {
    const userInput = document.getElementById('user-input')
    const userInputValue = userInput.value;
    const result = evaluateMathExpression(userInputValue);
    //const latexCode = parseUserInputToLatex(userInputValue);
    if(result === 'Error') {
        document.getElementById('latex-output').innerText = result;
    } else {
        const latexCode = parseToLatex(userInputValue);
        console.log(latexCode);
        const formattedLatex = `\\(` + latexCode + `\\)`
        document.getElementById('latex-output').innerText = formattedLatex;
        MathJax.Hub.Queue(['Typeset', MathJax.Hub, 'latex-output']);
    }
}

function parseToLatex(input) {
    const nodeInput = math.parse(input);
    const latexCode = nodeInput.toTex();
    return latexCode;
}

export function parseUserInputToLatex(input) {
    if(inputMode === 'regular') {
        try {
            var {a, sign, b, c, d, hasPi} = extractValuesFromInput(input, inputMode);
        } catch(e) {
            console.log(e);
        }
        let latexCode = "";

        // Append 'a' if it's not zero
        if (a !== 0) {
            latexCode += a;
        }

        // Append '+ b' or '- b' if 'b' is not zero
        if(!isNaN(b)) {
            if (b !== 0) {
                if (sign === '+' && a !== 0) {
                    latexCode += " + ";
                } else if (sign === "-") {
                    latexCode += " - ";
                }
                latexCode += (Math.abs(b) !== 1) ? Math.abs(b) : "";
            }
        }

        if (hasPi && b !== 0) {
            latexCode += "\\pi";
        }

        if(c !== 0) {
            latexCode += `\\sqrt{${c}}`;
        }

        if (d !== 1) {
            latexCode = "\\frac{" + latexCode + "}";
            latexCode = latexCode + `{` + `${d}` + `}`;
        }

        return latexCode;
    } else if(inputMode === 'scientific') {
        var {a, b} = extractValuesFromInput(input, inputMode);
        let latexCode = "";

        if(typeof(a) !== "undefined") {
            latexCode += a;
        }

        if(typeof(b) !== "undefined") {
            latexCode += " \\times " + "10\^\{" + b + "\}";
        }

        return latexCode;
    }
}

function renderMath() {
    // The MathJax API is accessible through the MathJax global object
    MathJax.Hub.Queue(['Typeset', MathJax.Hub, 'latex-output']);
}

// Function to evaluate the user's math input using Math.js
export function evaluateMathExpression(input) {
    try {
        // Use the evaluate function from Math.js to parse and evaluate the expression
        const result = math.parse(input);
        return result.toString(); // Convert the result to a string
    } catch (error) {
        return 'Error'; // Return "Error" if the input is invalid or cannot be evaluated
    }
}

MathJax.Hub.Config({
    tex2jax: {
        inlineMath: [['\\(', '\\)']],
        displayMath: [['$$', '$$']]
    }
});

function extractValuesFromInput(input, inputMode) {
    if(inputMode === 'regular') {
        // Regular expression to match the pattern (a + b*pi*sqrt(c))/d with or without "pi"
        const regex = /(?:\()?(-?\d*)(?:\s*([+-])\s*(\d*))?(?:\s*\*?\s*pi)?(?:\s*\*?\s*sqrt\((\d*)\))?\)?(?:\s*\/\s*(\d*))?/;

        // Match the input against the regex
        const match = input.match(regex);

        const hasPi = input.includes('pi');

        // Extract the values from the regex match
        if (match) {
            var a = parseInt(match[1] || 0);
            var sign = match[2];
            var b = 1;
            if(match[3] === '') {
                b = 1;
            } else {
                b = parseInt(match[3]);
            }
            
            var c = parseInt(match[4] || 0);
            var d = parseInt(match[5] || 1); // Default to 1 if d is not present

            return { a, sign, b, c, d, hasPi };
        } else {
            return null; // Return null if the input does not match the expected pattern
        }
    } else if(inputMode === 'scientific') {
        const regexScientificNotation = /(-?\d+(\.\d*)?)(?:\s*\*\s*10\s*\^\s*(-?\d+))?/i;

        const match = input.match(regexScientificNotation);

        var a;
        var b;

        if(typeof(match[1]) !== 'undefined') {
            var precision = countDigitsAfterDecimal(match[1]);
            a = parseFloat(match[1]).toFixed(precision);
        }

        if(typeof(match[3]) !== 'undefined') {
            b = parseInt(match[3]);
        }

        return {a, b};
    }
}

function countDigitsAfterDecimal(input) {
    const decimalIndex = input.indexOf('.');
    if (decimalIndex !== -1) {
        return input.length - decimalIndex - 1;
    }
    return 0;
}

/* // Test cases
console.log(extractValuesFromInput("(2 + 3*pi*sqrt(5))/4")); // { a: 2, b: 3, c: 5, d: 4 }
console.log(extractValuesFromInput("(1 - 2*pi*sqrt(3))/5")); // { a: 1, b: -2, c: 3, d: 5 }
console.log(extractValuesFromInput("(5 + 7*pi)/2")); // { a: 5, b: 7, c: 0, d: 2 } (no sqrt(c) part)
console.log(extractValuesFromInput("(6 + 4*pi*sqrt(2))")); // { a: 6, b: 4, c: 2, d: 1 } (no division part)
console.log(extractValuesFromInput("(3*sqrt(2))/4")); // { a: 0, b: 3, c: 2, d: 4 } (no "pi" part)
console.log(extractValuesFromInput("1/6"));
console.log(extractValuesFromInput("2"));
console.log(extractValuesFromInput("invalid input")); // null (does not match the expected pattern) */

  