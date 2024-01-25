import { getSideBar } from "./sidebar.js";
import { getTopBar } from "./topbar.js";

const badgeUrl = "https://mm-level-badges.s3.us-east-2.amazonaws.com";
getSideBar("regular");
const userLevel = await getTopBar();
getProfileData();

async function getProfileData() {
    try {
        const response = await fetch("/profile_data");
        const profileData = await response.json();
        console.log(profileData);
        console.log(userLevel);
        populateTables(profileData);
    } catch(error) {
        console.log("Error: could not get profile data.");
    } 
}

function populateTables(profileData) {
    document.getElementById("games_played").textContent = profileData[0];
    document.getElementById("games_won").textContent = profileData[1];
    document.getElementById("prob_solved").textContent = profileData[2];
    genBadgeTable();
}

function genBadgeTable() {
    const tbody = document.getElementById("badges_body");
    let count = 1;
    for (let i = 0; i < 5; i++) {
        const row = document.createElement('tr');
        // Loop through columns
        for (let j = 0; j < 5; j++) {
            const cell = document.createElement('td');
            const imgBadge = document.createElement('img');
            if(count <= userLevel) {
                imgBadge.src = badgeUrl + "/b" + count + ".png";
            } else {
                imgBadge.src = "/img/lock_badge.png";
            }
            imgBadge.style.width = "50px";
            cell.appendChild(imgBadge)
            row.appendChild(cell);
            count++;
        }
        tbody.appendChild(row);
    }
}
