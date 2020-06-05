// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

var numDescriptor = 0;
var timeout = setTimeout(console.log("Hello"), 1);

function addRandomFact() {
    const facts = 
        ['I am allergic to the allium family', 'I have backpacked over 400 miles', 
        'My favorite sport is soccer'];

    const fact = facts[Math.floor(Math.random() * facts.length)];

    const factContainer = document.getElementById('fact-container');
    factContainer.innerText = fact;
}

function setStatus() {
    const descriptors = ["Person","Hiker", "Coder", "Athlete"];
    const colors = ["darkgreen","red", "green", "blue"];
    numDescriptor++;
    if(numDescriptor>=descriptors.length){
        numDescriptor = 0;
    }

    const descriptorContainer = document.getElementById('descriptorContainer');
    descriptorContainer.innerText = descriptors[numDescriptor];
    descriptorContainer.style.color = colors[numDescriptor];
    timeout = setTimeout(setRandomStatus, 1500);
    _resetStatusAnimation();
}

function setStatus(status, color){
    descriptorContainer.innerText = status;
    descriptorContainer.style.color = color;
    _resetStatusAnimation();
}

function _resetStatusAnimation(){
    descriptorContainer.style.animation = "none";
    setTimeout(()=>descriptorContainer.style.animation = null, 5);
}

/*
Stops the carousel of descriptors and locks from reactivating for 1s
*/
function stopStatus() {
    clearTimeout(timeout);
    const descriptorContainer = document.getElementById('descriptorContainer');
    descriptorContainer.innerText = "";
}
