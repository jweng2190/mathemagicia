function convertToLatex() {
    const userInput = document.getElementById('user-input')
    const userInputValue = userInput.value;
    const result = evaluateMathExpression(userInputValue);
    const latexCode = parseUserInputToLatex(result);
    if(latexCode === 'Error') {
        document.getElementById('latex-output').innerText = latexCode;
    } else {
        const formattedLatex = `\\(` + latexCode + `\\)`
        document.getElementById('latex-output').innerText = formattedLatex;
        MathJax.Hub.Queue(['Typeset', MathJax.Hub, 'latex-output']);
    }
}

function parseUserInputToLatex(input) {
    var {a, sign, b, c, d, hasPi, hasPiInDenominator} = extractValuesFromInput(input);
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
        if(hasPiInDenominator) {
            latexCode += `{` + d + `\\pi` + `}`;
        } else {
            latexCode += `${d}`;
        }
    }

    return latexCode;
}

function renderMath() {
    // The MathJax API is accessible through the MathJax global object
    MathJax.Hub.Queue(['Typeset', MathJax.Hub, 'latex-output']);
}

// Function to evaluate the user's math input using Math.js
function evaluateMathExpression(input) {
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

function extractValuesFromInput(input) {
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
        var denominatorPart = match[6];
        //var hasPiInDenominator = denominatorPart.includes('pi');

        return { a, sign, b, c, d, hasPi };
    } else {
        return null; // Return null if the input does not match the expected pattern
    }
}
  
// Test cases
console.log(extractValuesFromInput("(2 + 3*pi*sqrt(5))/4")); // { a: 2, b: 3, c: 5, d: 4 }
console.log(extractValuesFromInput("(1 - 2*pi*sqrt(3))/5")); // { a: 1, b: -2, c: 3, d: 5 }
console.log(extractValuesFromInput("(5 + 7*pi)/2")); // { a: 5, b: 7, c: 0, d: 2 } (no sqrt(c) part)
console.log(extractValuesFromInput("(6 + 4*pi*sqrt(2))")); // { a: 6, b: 4, c: 2, d: 1 } (no division part)
console.log(extractValuesFromInput("(3*sqrt(2))/4")); // { a: 0, b: 3, c: 2, d: 4 } (no "pi" part)
console.log(extractValuesFromInput("1/6"));
console.log(extractValuesFromInput("2"));
console.log(extractValuesFromInput("invalid input")); // null (does not match the expected pattern)

  